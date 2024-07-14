package com.hospital.exception;

public class FechaFinalizacionRequeridaException extends Exception{

	private static final long serialVersionUID = 1L;
	
	public FechaFinalizacionRequeridaException (String mensaje) {
		super (mensaje);
	}
	
}
