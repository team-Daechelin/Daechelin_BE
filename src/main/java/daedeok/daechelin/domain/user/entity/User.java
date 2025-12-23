package daedeok.daechelin.domain.user.entity;

import jakarta.persistence.*;
import lombok.*; // Lombok 전체 import


@Table(name = "tbl_user")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true, nullable = false, length = 30)
    private String username;//아이디

    @Column(nullable = false, unique = true)
    private String nickname;//닉네임

    @Column(nullable = false)
    private String name;//실명

    @Column(nullable = false, length = 225)
    private String password;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, unique = true, name = "student_id")
    private String studentId;

    private String role;


    // 수정이 필요할 땐 이렇게 '의미 있는 메서드'를 직접 만듭니다.
    public void updateNickname(String newNickname) {
        this.nickname = newNickname;
    }
}
