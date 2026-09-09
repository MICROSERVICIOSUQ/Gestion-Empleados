package com.microservicios.gestionempleados.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class Empleado {

    @NotBlank(message = "El id es obligatorio")
    private String id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El email no tiene un formato válido")
    private String email;

    @NotBlank(message = "El numeroEmpleado es obligatorio")
    private String numeroEmpleado;

    @NotBlank(message = "El cargo es obligatorio")
    private String cargo;

    @NotBlank(message = "El area es obligatoria")
    private String area;

    @NotBlank(message = "El departamentoId es obligatorio")
    private String departamentoId;

    @NotNull(message = "La fechaIngreso es obligatoria")
    private LocalDate fechaIngreso;

    @NotNull(message = "El estado es obligatorio")
    private EstadoEmpleado estado;

    public Empleado() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNumeroEmpleado() { return numeroEmpleado; }
    public void setNumeroEmpleado(String numeroEmpleado) { this.numeroEmpleado = numeroEmpleado; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getDepartamentoId() { return departamentoId; }
    public void setDepartamentoId(String departamentoId) { this.departamentoId = departamentoId; }

    public LocalDate getFechaIngreso() { return fechaIngreso; }
    public void setFechaIngreso(LocalDate fechaIngreso) { this.fechaIngreso = fechaIngreso; }

    public EstadoEmpleado getEstado() { return estado; }
    public void setEstado(EstadoEmpleado estado) { this.estado = estado; }
}
