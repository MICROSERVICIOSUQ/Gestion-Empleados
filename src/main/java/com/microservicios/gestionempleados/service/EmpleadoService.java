package com.microservicios.gestionempleados.service;

import com.microservicios.gestionempleados.exception.EmailYaRegistradoException;
import com.microservicios.gestionempleados.exception.EmpleadoNoEncontradoException;
import com.microservicios.gestionempleados.exception.NumeroEmpleadoYaRegistradoException;
import com.microservicios.gestionempleados.model.Empleado;
import com.microservicios.gestionempleados.model.EstadoEmpleado;
import com.microservicios.gestionempleados.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmpleadoService {

    private final EmpleadoRepository repository;

    public EmpleadoService(EmpleadoRepository repository) {
        this.repository = repository;
    }

    public Empleado registrar(Empleado empleado) {
        if (repository.existsByEmail(empleado.getEmail())) {
            throw new EmailYaRegistradoException(empleado.getEmail());
        }
        if (repository.existsByNumeroEmpleado(empleado.getNumeroEmpleado())) {
            throw new NumeroEmpleadoYaRegistradoException(empleado.getNumeroEmpleado());
        }
        empleado.setId(UUID.randomUUID().toString());
        empleado.setEstado(EstadoEmpleado.ACTIVO);
        return repository.save(empleado);
    }

    public Empleado buscarPorId(String id) {
        return repository.findById(id)
                .orElseThrow(() -> new EmpleadoNoEncontradoException(id));
    }
}
