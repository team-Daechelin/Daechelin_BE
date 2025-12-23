package daedeok.daechelin.domain.user.repository;

import daedeok.daechelin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username); // ID 중복체크
    Boolean existsByNickname(String nickname); // 닉네임 중복체크
    Boolean existsByEmail(String email);
    Boolean existsByStudentId(String student_id);
    Optional<User> findByUsername(String username);
}