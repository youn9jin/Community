package com.example.community.global.exception;

public class LikesNotFoundException extends RuntimeException {
    public LikesNotFoundException(Integer postId) {
        super("Resource not found in the system");
    }
}
