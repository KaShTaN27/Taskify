package com.example.taskify.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ExceptionWithHttpStatus extends RuntimeException{
    private HttpStatus status;
    private String message;
}
