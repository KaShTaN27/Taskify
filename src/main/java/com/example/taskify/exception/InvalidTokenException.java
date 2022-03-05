package com.example.taskify.exception;

import static org.springframework.http.HttpStatus.FORBIDDEN;

public class InvalidTokenException extends ExceptionWithHttpStatus{
    public InvalidTokenException(String message) {
        super(FORBIDDEN, message);
    }
}
