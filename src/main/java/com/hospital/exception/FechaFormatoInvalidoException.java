package com.hospital.exception;

public class FechaFormatoInvalidoException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
    public FechaFormatoInvalidoException(String message) {
        super(message);
    }
    
}
