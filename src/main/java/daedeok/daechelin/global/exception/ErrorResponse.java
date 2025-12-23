package daedeok.daechelin.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorResponse {
    private int status;      // HTTP 상태 코드 (예: 400, 404)
    private String message;  // 에러 메시지 (예: "이미 존재하는 아이디입니다.")
}