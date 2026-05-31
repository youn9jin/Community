package com.example.community.global;

import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.global.exception.DuplicateNicknameException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 400 - @Valid 검증 실패
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleValidation(
            MethodArgumentNotValidException e) {

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(400, "Invalid request", "BAD_REQUEST"));
    }

    // 409 - 이메일 중복
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDuplicateEmail(
            DuplicateEmailException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseWrapper.error(409, e.getMessage(), "CONFLICT"));
    }

    // 409 - 닉네임 중복
    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDuplicateNickname(
            DuplicateNicknameException e) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ResponseWrapper.error(409, e.getMessage(), "CONFLICT"));
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleException(Exception e) {

        log.error("Unexpected error: ", e); // 로그는 500만 남김
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error(500, "Internal server error",
                        "INTERNAL_SERVER_ERROR"));
    }
}