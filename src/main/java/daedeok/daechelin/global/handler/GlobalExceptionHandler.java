package daedeok.daechelin.global.handler;

import daedeok.daechelin.global.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice // 모든 컨트롤러의 예외를 감지함
public class GlobalExceptionHandler {

    // 1. 서비스 로직에서 발생하는 에러 처리 (예: 중복 가입 시도)
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException e) {
        // SignUpService에서 던진 "이미 존재하는..." 메시지를 그대로 가져옵니다.
        ErrorResponse response = new ErrorResponse(400, e.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    // 2. @Valid 유효성 검사 실패 처리 (예: 아이디 공백, 이메일 형식 오류)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        // 첫 번째로 발생한 에러 메시지만 뽑아서 보냄
        String errorMessage = e.getBindingResult().getAllErrors().get(0).getDefaultMessage();

        ErrorResponse response = new ErrorResponse(400, errorMessage);
        return ResponseEntity.status(400).body(response);
    }
}