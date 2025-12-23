package daedeok.daechelin.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpRequest {
    //숫자에는 @NotNull 더 적합.
    @NotBlank(message = "아이디에 공백, 띄어쓰기를 사용할 수 없습니다")
    @Size(min = 3, max = 12, message = "최소 3자 ~ 최대 12자까지 가능합니다")
    private String username;

    @NotBlank(message = "비밀번호에 공백, 띄어쓰기를 사용할 수 없습니다")
    @Pattern(regexp = "^[a-zA-Z0-9_!#$*]{8,255}$", message = "최소 8자 ~ 최대 225자까지 가능하며, 숫자, 영어 대소문자와 _!#$*만 허용됩니다")
    private String password;

    @NotBlank(message = "닉네임에 공백, 띄어쓰기를 사용할 수 없습니다")
    @Size(max = 8, message = "닉네임은 최대 8글자 입니다")
    private String nickname;

    @NotBlank(message = "이름은 필수 입력 값입니다.") // 추가 권장
    private String name;

    @NotBlank(message = "이메일은 필수 입력 값입니다.") // 추가 권장
    @Email(message = "이메일 형식이 올바르지 않습니다.") // 추가 권장
    private String email;

    @NotBlank(message = "학번은 필수 입력 값입니다.")
    @Size(max = 4, min = 4, message = "학번은 4글자 입니다")
    @JsonProperty("student_id")
    private String studentId;
}