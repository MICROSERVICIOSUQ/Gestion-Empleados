package com.microservicios.gestionempleados;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class EmpleadoEndpointsTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String JUAN = """
            {
              "id": "E001",
              "nombre": "Juan",
              "apellido": "Pérez",
              "email": "juan.perez@empresa.com",
              "numeroEmpleado": "EMP-2026-001",
              "cargo": "Desarrollador Senior",
              "area": "Tecnología",
              "departamentoId": "IT",
              "fechaIngreso": "2026-02-10",
              "estado": "ACTIVO"
            }
            """;

    @Test
    @Order(1)
    void registroExitosoConservaElIdDelCliente() throws Exception {
        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JUAN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("E001"))
                .andExpect(jsonPath("$.nombre").value("Juan"))
                .andExpect(jsonPath("$.apellido").value("Pérez"))
                .andExpect(jsonPath("$.numeroEmpleado").value("EMP-2026-001"))
                .andExpect(jsonPath("$.departamentoId").value("IT"))
                .andExpect(jsonPath("$.fechaIngreso").value("2026-02-10"))
                .andExpect(jsonPath("$.estado").value("ACTIVO"));
    }

    @Test
    @Order(2)
    void consultaPorIdDelCliente() throws Exception {
        mockMvc.perform(get("/empleados/E001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("E001"))
                .andExpect(jsonPath("$.email").value("juan.perez@empresa.com"))
                .andExpect(jsonPath("$.numeroEmpleado").value("EMP-2026-001"));
    }

    @Test
    @Order(3)
    void idDuplicadoDevuelve400() throws Exception {
        String mismoIdTodoLoDemasDistinto = JUAN
                .replace("juan.perez@empresa.com", "otro.correo@empresa.com")
                .replace("EMP-2026-001", "EMP-2026-999");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mismoIdTodoLoDemasDistinto))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El id 'E001' ya está registrado"));
    }

    @Test
    @Order(4)
    void emailDuplicadoDevuelve400() throws Exception {
        String otroIdMismoEmail = JUAN
                .replace("\"id\": \"E001\"", "\"id\": \"E002\"")
                .replace("EMP-2026-001", "EMP-2026-999");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(otroIdMismoEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El email 'juan.perez@empresa.com' ya está registrado"));
    }

    @Test
    @Order(5)
    void numeroEmpleadoDuplicadoDevuelve400() throws Exception {
        String otroIdOtroEmailMismoNumero = JUAN
                .replace("\"id\": \"E001\"", "\"id\": \"E003\"")
                .replace("juan.perez@empresa.com", "pedro.lopez@empresa.com");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(otroIdOtroEmailMismoNumero))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El número de empleado 'EMP-2026-001' ya está registrado"));
    }

    @Test
    @Order(6)
    void idAusenteDevuelve400() throws Exception {
        String sinId = JUAN.replace("\"id\": \"E001\",", "");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sinId))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El id del empleado es obligatorio"));
    }

    @Test
    @Order(7)
    void empleadoInexistenteDevuelve404ConMensajeExacto() throws Exception {
        mockMvc.perform(get("/empleados/E999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("El empleado con id E999 no existe"));
    }

    @Test
    @Order(8)
    void rutaNoDefinidaDevuelve404Global() throws Exception {
        mockMvc.perform(get("/ruta-invalida"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Recurso no encontrado"));
    }

    @Test
    @Order(9)
    void metodoNoDefinidoEnRutaExistenteDevuelve404Global() throws Exception {
        mockMvc.perform(get("/empleados"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Recurso no encontrado"));
    }
}
