package com.microservicios.gestionempleados.exception;

public class NumeroEmpleadoYaRegistradoException extends RuntimeException {
    public NumeroEmpleadoYaRegistradoException(String numeroEmpleado) {
        super("El número de empleado '" + numeroEmpleado + "' ya está registrado");
    }
}
