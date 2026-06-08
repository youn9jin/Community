package com.example.community.global;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseWrapper<T> {

    private String message;   // 성공 시 message
    private T data;           // 성공 시 응답 데이터
    private ErrorBody error;  // 실패 시 에러 정보

    // 성공 응답
    public static <T> ResponseWrapper<T> success(String message, T data) {
        return new ResponseWrapper<>(message, data, null);
    }

    // 실패
    public static <T> ResponseWrapper<T> error(int code, String message, String status) {
        return new ResponseWrapper<>(null, null,
                new ErrorBody(code, message, status));
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorBody {
        private int code;
        private String message;
        private String status;
    }
}