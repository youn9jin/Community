package com.example.community.global.exception;

public class DuplicateNicknameException extends RuntimeException {
    public DuplicateNicknameException() {
        super("Already registered nickname");
    }
}