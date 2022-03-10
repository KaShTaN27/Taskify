package com.example.taskify.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

@Getter
public class InvalidTokenException extends AuthenticationException {
    private final HttpStatus status = HttpStatus.UNAUTHORIZED;

    public InvalidTokenException(String msg) {
        super(msg);
    }
}
