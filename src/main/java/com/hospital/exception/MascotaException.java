package com.hospital.exception;

public class MascotaException extends Exception{

	private static final long serialVersionUID = 1L;
	
    public MascotaException(String mensaje) {
        super(mensaje);
    }
}
