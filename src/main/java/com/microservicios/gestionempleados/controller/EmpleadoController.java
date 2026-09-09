package com.microservicios.gestionempleados.controller;

import com.microservicios.gestionempleados.model.Empleado;
import com.microservicios.gestionempleados.service.EmpleadoService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/empleados")
public class EmpleadoController {

    private final EmpleadoService service;

    public EmpleadoController(EmpleadoService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Empleado> registrar(@Valid @RequestBody Empleado empleado) {
        return ResponseEntity.ok(service.registrar(empleado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> obtener(@PathVariable String id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }
}
