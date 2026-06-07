package com.example.community.global.exception;

public class ForbiddenException extends RuntimeException {
    public ForbiddenException() {
        this("don't have rights to modify");
    }

    public ForbiddenException(String message) {
        super(message);
    }
}
