package com.microservicios.gestionempleados.repository;

import com.microservicios.gestionempleados.model.Empleado;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class EmpleadoRepository {

    private final ConcurrentHashMap<String, Empleado> storage = new ConcurrentHashMap<>();

    public Empleado save(Empleado empleado) {
        storage.put(empleado.getId(), empleado);
        return empleado;
    }

    public Optional<Empleado> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public boolean existsById(String id) {
        return storage.containsKey(id);
    }

    // Las comprobaciones toleran valores nulos almacenados: un registro incompleto
    // no debe hacer fallar las consultas posteriores.
    public boolean existsByEmail(String email) {
        return storage.values().stream()
                .anyMatch(e -> e.getEmail() != null && e.getEmail().equalsIgnoreCase(email));
    }

    public boolean existsByNumeroEmpleado(String numeroEmpleado) {
        return storage.values().stream()
                .anyMatch(e -> e.getNumeroEmpleado() != null
                        && e.getNumeroEmpleado().equals(numeroEmpleado));
    }
}
