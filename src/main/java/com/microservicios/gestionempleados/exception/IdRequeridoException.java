package com.microservicios.gestionempleados.exception;

public class IdRequeridoException extends RuntimeException {
    public IdRequeridoException() {
        super("El id del empleado es obligatorio");
    }
}
