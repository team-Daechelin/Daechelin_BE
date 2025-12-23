package daedeok.daechelin.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import daedeok.daechelin.domain.token.entity.RefreshToken;
import daedeok.daechelin.domain.token.repository.RefreshTokenRepository;
import daedeok.daechelin.global.request.LoginRequest; // 작성하신 LoginRequest DTO 사용
import daedeok.daechelin.global.security.config.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;

@RequiredArgsConstructor
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    // /login 요청이 오면 실행되는 메소드 (
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try {
            // JSON으로 들어온 로그인 요청을 DTO로 변환
            ObjectMapper om = new ObjectMapper();
            LoginRequest loginRequest = om.readValue(request.getInputStream(), LoginRequest.class);

            String username = loginRequest.getUsername();
            String password = loginRequest.getPassword();

            // 인증 토큰 생성
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, null);

            // AuthenticationManager에게 검증 위임
            return authenticationManager.authenticate(authToken);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // 인증 성공 시 실행 ]
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {

        String username = authentication.getName();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        // 1. 토큰 생성
        // Access Token: 10분
        String access = jwtUtil.createJwt("access", username, role, 600000L);
        // Refresh Token: 24시간
        String refresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // 2. Redis에 Refresh Token 저장
        addRefreshToken(username, refresh, 86400000L);

        // 3. 응답 설정
        response.setHeader("access", access); // Access Token은 헤더에
        response.setHeader("refresh", refresh); // 편의상 헤더에 같이 보냄 (쿠키로 해도 됨)
        response.setStatus(HttpStatus.OK.value());
    }

    private void addRefreshToken(String username, String refresh, Long expiredMs) {
        // Redis에 저장 (key: refreshToken, value: username)
        RefreshToken refreshToken = new RefreshToken(refresh, username);
        refreshTokenRepository.save(refreshToken);
    }

    // 3. 인증 실패 시 실행
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setStatus(401); // 401 Unauthorized
    }
}