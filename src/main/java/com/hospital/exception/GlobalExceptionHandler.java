package com.hospital.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IngresoNoEncontradoException.class)
    public ResponseEntity<?> resourceNotFoundException(IngresoNoEncontradoException ex, WebRequest request) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(FechaFinalizacionRequeridaException.class)
    public ResponseEntity<String> handleFechaFinalizacionRequeridaException(FechaFinalizacionRequeridaException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(FechaFormatoInvalidoException.class)
    public ResponseEntity<String> handleInvalidFormatException(FechaFormatoInvalidoException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    
}
