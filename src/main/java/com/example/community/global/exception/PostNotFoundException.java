package com.example.community.global.exception;

public class PostNotFoundException extends RuntimeException {
    public PostNotFoundException(Integer postId) {
        super("Resource not found in the system");
    }
}
