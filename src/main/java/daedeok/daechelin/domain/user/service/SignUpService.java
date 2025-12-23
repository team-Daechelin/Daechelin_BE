package daedeok.daechelin.domain.user.service;

import daedeok.daechelin.domain.user.dto.request.SignUpRequest;
import daedeok.daechelin.domain.user.entity.User;
import daedeok.daechelin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SignUpService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public void join(SignUpRequest signUpRequest) {

        //ID 중복 검사
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new RuntimeException("이미 존재하는 아이디입니다.");
        }

        //닉네임 중복 검사
        if (userRepository.existsByNickname(signUpRequest.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        //이메일 중복 검사
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        if(userRepository.existsByStudentId(signUpRequest.getStudentId())){
            throw new RuntimeException("이미 사용 중인 학번입니다.");
        }

        User user = User.builder()
                .username(signUpRequest.getUsername())
                .password(bCryptPasswordEncoder.encode(signUpRequest.getPassword()))
                .nickname(signUpRequest.getNickname())
                .name(signUpRequest.getName())
                .email(signUpRequest.getEmail())
                .studentId(signUpRequest.getStudentId())
                .role("ROLE_USER")
                .build();

        userRepository.save(user);
    }

}
