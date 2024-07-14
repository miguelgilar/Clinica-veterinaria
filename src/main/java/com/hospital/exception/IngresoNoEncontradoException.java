package com.hospital.exception;

public class IngresoNoEncontradoException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public IngresoNoEncontradoException (String mensaje) {
		super (mensaje);
	}
}
