package daedeok.daechelin.domain.token.controller;

import daedeok.daechelin.domain.token.repository.RefreshTokenRepository;
import daedeok.daechelin.global.security.config.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LogoutController {

    private final JWTUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        String refresh = request.getHeader("refresh");

        if (refresh == null) {
            return new ResponseEntity<>("refresh token null", HttpStatus.BAD_REQUEST);
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            return new ResponseEntity<>(HttpStatus.OK);
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            return new ResponseEntity<>("invalid refresh token", HttpStatus.BAD_REQUEST);
        }

        if (refreshTokenRepository.existsById(refresh)) {
            refreshTokenRepository.deleteById(refresh);
        }

        return new ResponseEntity<>("로그아웃 성공", HttpStatus.OK);
    }
}