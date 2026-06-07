package com.example.community.global.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException() {
        super("Unauthorized request");
    }
}
