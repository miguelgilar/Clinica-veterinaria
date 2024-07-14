package com.hospital.exception;

public class MascotaNoEncontradaException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
    public MascotaNoEncontradaException(String message) {
        super(message);
    }
    
}
