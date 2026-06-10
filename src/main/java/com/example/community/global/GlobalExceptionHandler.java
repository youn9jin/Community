package com.example.community.global;

import com.example.community.global.exception.DuplicateEmailException;
import com.example.community.global.exception.DuplicateNicknameException;
import com.example.community.global.exception.ForbiddenException;
import com.example.community.global.exception.PostNotFoundException;
import com.example.community.global.exception.UnauthorizedException;
import com.example.community.global.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
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

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.error(400, getValidationErrorMessage(e), "BAD_REQUEST"));
    }

    //401 - 인증 실패
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleUnauthorized(UnauthorizedException e) {

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ResponseWrapper.error(401, e.getMessage(), "UNAUTHORIZED"));
    }

    //403 - 권한 없음
    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleForbidden(ForbiddenException e) {

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ResponseWrapper.error(403, e.getMessage(), "FORBIDDEN"));
    }

    //404 - User 조회 실패
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handleUserNotFound(UserNotFoundException e) {

        return ResponseEntity.status(404)
                .body(ResponseWrapper.error(404, e.getMessage(), "NOT_FOUND"));
    }

    //404 - Post 조회 실패
    @ExceptionHandler(PostNotFoundException.class)
    public ResponseEntity<ResponseWrapper<?>> handlePostNotFound(PostNotFoundException e) {

        return ResponseEntity.status(404)
                .body(ResponseWrapper.error(404, e.getMessage(), "NOT_FOUND"));
    }

    // 409 - 이메일 중복
    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDuplicateEmail(
            DuplicateEmailException e) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseWrapper.error(409, e.getMessage(), "CONFLICT"));
    }

    // 409 - 닉네임 중복
    @ExceptionHandler(DuplicateNicknameException.class)
    public ResponseEntity<ResponseWrapper<Void>> handleDuplicateNickname(
            DuplicateNicknameException e) {

        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ResponseWrapper.error(409, e.getMessage(), "CONFLICT"));
    }

    // 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseWrapper<Void>> handleException(Exception e) {

        log.error("Unexpected error: ", e); // 로그는 500만 남김
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ResponseWrapper.error(500, "Internal server error",
                        "INTERNAL_SERVER_ERROR"));
    }

    // Validation 실패 원인에 따른 400 메시지 구분
    private String getValidationErrorMessage(MethodArgumentNotValidException e) {
        FieldError fieldError = e.getBindingResult().getFieldError();

        if (fieldError != null
                && ("NotEmpty".equals(fieldError.getCode())
                || "NotBlank".equals(fieldError.getCode())
                || "NotNull".equals(fieldError.getCode()))) {
            return "missing field";
        }

        return "bad request";
    }
}
