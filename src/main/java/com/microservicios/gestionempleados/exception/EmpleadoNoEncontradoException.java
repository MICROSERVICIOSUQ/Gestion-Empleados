package com.microservicios.gestionempleados.exception;

public class EmpleadoNoEncontradoException extends RuntimeException {

    private final String id;

    public EmpleadoNoEncontradoException(String id) {
        super("El empleado con id " + id + " no existe");
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
