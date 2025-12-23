package daedeok.daechelin.domain.user.controller;

import daedeok.daechelin.domain.user.dto.request.SignUpRequest;
import daedeok.daechelin.domain.user.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final SignUpService signUpService;

    @PostMapping("/join")
    public ResponseEntity<String> join(@RequestBody @Valid SignUpRequest signUpRequest) {
        signUpService.join(signUpRequest);
        return ResponseEntity.ok("회원가입 성공");
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        String currentUsername = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok("인증 성공! 현재 유저: " + currentUsername);
    }
}
