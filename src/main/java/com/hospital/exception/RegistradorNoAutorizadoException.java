package com.hospital.exception;

public class RegistradorNoAutorizadoException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public RegistradorNoAutorizadoException(String mensaje) {
        super(mensaje);
	}
}
