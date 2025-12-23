package daedeok.daechelin.domain.token.controller;

import daedeok.daechelin.domain.token.entity.RefreshToken;
import daedeok.daechelin.domain.token.repository.RefreshTokenRepository;
import daedeok.daechelin.global.security.config.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 헤더에서 refresh 토큰 추출
        String refresh = request.getHeader("refresh");

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        // 만료 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>("refresh token expired", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (access 토큰으로 재발급 요청 방지)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        // Redis에 저장되어 있는지 확인
        if (!refreshTokenRepository.existsById(refresh)) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새로운 토큰 생성
        String newAccess = jwtUtil.createJwt("access", username, role, 600000L);
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 86400000L);

        // Redis 업데이트 (기존 것 삭제 후 새 것 저장)
        refreshTokenRepository.deleteById(refresh);
        refreshTokenRepository.save(new RefreshToken(newRefresh, username));

        response.setHeader("access", newAccess);
        response.setHeader("refresh", newRefresh);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}