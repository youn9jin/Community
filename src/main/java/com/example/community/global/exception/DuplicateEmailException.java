package com.example.community.global.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException() {
        super("Already registered email");
    }
}