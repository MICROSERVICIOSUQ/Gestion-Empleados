package com.microservicios.gestionempleados.exception;

public class IdYaRegistradoException extends RuntimeException {
    public IdYaRegistradoException(String id) {
        super("El id '" + id + "' ya está registrado");
    }
}
