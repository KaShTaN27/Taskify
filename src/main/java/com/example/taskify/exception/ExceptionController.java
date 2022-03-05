package com.example.taskify.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ExceptionWithHttpStatus.class)
    public ResponseEntity<?> handleException(ResourceNotFoundException exception) {
        return ResponseEntity.status(exception.getStatus()).body(exception.getMessage());
    }
}
