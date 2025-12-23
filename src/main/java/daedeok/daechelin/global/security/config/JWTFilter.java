package daedeok.daechelin.global.security.config;

import daedeok.daechelin.domain.user.entity.User;
import daedeok.daechelin.global.detail.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // 1. 헤더에서 Authorization 키에 담긴 토큰을 꺼냄
        String authorization = request.getHeader("Authorization");

        // 2. 토큰이 없거나 "Bearer "로 시작하지 않으면 다음 필터로 넘김 (로그인 안 한 상태)
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // "Bearer " 부분 제거하고 순수 토큰만 획득
        String token = authorization.split(" ")[1];

        // 3. 토큰 만료 여부 확인
        try {
            if (jwtUtil.isExpired(token)) {
                filterChain.doFilter(request, response);
                return;
            }
        } catch (Exception e) {
            // 토큰 검증 실패 시 그냥 넘김 (인증 실패 처리됨)
            filterChain.doFilter(request, response);
            return;
        }

        // 4. 토큰에서 아이디와 권한 획득
        String username = jwtUtil.getUsername(token);
        String role = jwtUtil.getRole(token);

        // 5. UserDetails에 담아서 임시 세션 생성
        // (비밀번호는 DB 조회가 필요 없으므로 임의의 값으로 설정)
        User user = User.builder()
                .username(username)
                .password("temppassword")
                .role(role)
                .build();

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // 6. 스프링 시큐리티 인증 토큰 생성
        Authentication authToken = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // 7. 세션에 사용자 등록 (이번 요청이 끝날 때까지만 유지됨)
        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }
}