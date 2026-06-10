package com.example.community.global.exception;

public class CommentNotFoundException extends RuntimeException {
    public CommentNotFoundException(Integer commentId) {
        super("Resource not found in the system");
    }
}
