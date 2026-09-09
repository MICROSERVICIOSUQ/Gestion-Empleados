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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
                .andExpect(jsonPath("$.email").value("juan.perez@empresa.com"));
    }

    @Test
    @Order(3)
    void idDuplicadoDevuelve409() throws Exception {
        String mismoId = JUAN
                .replace("juan.perez@empresa.com", "otro.correo@empresa.com")
                .replace("EMP-2026-001", "EMP-2026-999");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mismoId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("El id 'E001' ya está registrado"))
                .andExpect(jsonPath("$.path").value("/empleados"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }

    @Test
    @Order(4)
    void emailDuplicadoDevuelve409() throws Exception {
        String otroIdMismoEmail = JUAN
                .replace("\"id\": \"E001\"", "\"id\": \"E002\"")
                .replace("EMP-2026-001", "EMP-2026-999");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(otroIdMismoEmail))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("El email 'juan.perez@empresa.com' ya está registrado"));
    }

    @Test
    @Order(5)
    void numeroEmpleadoDuplicadoDevuelve409() throws Exception {
        String otroIdOtroEmail = JUAN
                .replace("\"id\": \"E001\"", "\"id\": \"E003\"")
                .replace("juan.perez@empresa.com", "pedro.lopez@empresa.com");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(otroIdOtroEmail))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message")
                        .value("El número de empleado 'EMP-2026-001' ya está registrado"));
    }

    @Test
    @Order(6)
    void cuerpoVacioDevuelve400ConTodosLosCamposObligatorios() throws Exception {
        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("La solicitud contiene campos inválidos"))
                .andExpect(jsonPath("$.errores", hasSize(10)))
                .andExpect(jsonPath("$.errores[*].campo").value(containsInAnyOrder(
                        "id", "nombre", "apellido", "email", "numeroEmpleado",
                        "cargo", "area", "departamentoId", "fechaIngreso", "estado")));
    }

    @Test
    @Order(7)
    void campoObligatorioAusenteDevuelve400ConEseCampo() throws Exception {
        String sinNombre = JUAN.replace("\"nombre\": \"Juan\",", "");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(sinNombre))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores", hasSize(1)))
                .andExpect(jsonPath("$.errores[0].campo").value("nombre"))
                .andExpect(jsonPath("$.errores[0].mensaje").value("El nombre es obligatorio"));
    }

    @Test
    @Order(8)
    void emailConFormatoInvalidoDevuelve400() throws Exception {
        String emailMalo = JUAN
                .replace("\"id\": \"E001\"", "\"id\": \"E020\"")
                .replace("juan.perez@empresa.com", "esto-no-es-un-correo");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(emailMalo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores[0].campo").value("email"))
                .andExpect(jsonPath("$.errores[0].mensaje").value("El email no tiene un formato válido"));
    }

    @Test
    @Order(9)
    void estadoInvalidoDevuelve400() throws Exception {
        String estadoMalo = JUAN
                .replace("\"id\": \"E001\"", "\"id\": \"E021\"")
                .replace("\"estado\": \"ACTIVO\"", "\"estado\": \"JUBILADO\"");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(estadoMalo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message")
                        .value("El cuerpo de la petición no es un JSON válido o contiene valores con formato incorrecto"));
    }

    @Test
    @Order(10)
    void empleadoInexistenteDevuelve404() throws Exception {
        mockMvc.perform(get("/empleados/E999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("El empleado con id E999 no existe"))
                .andExpect(jsonPath("$.path").value("/empleados/E999"));
    }

    @Test
    @Order(11)
    void rutaNoDefinidaDevuelve404Global() throws Exception {
        mockMvc.perform(get("/ruta-invalida"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado"));
    }

    @Test
    @Order(12)
    void metodoNoDefinidoEnRutaExistenteDevuelve404Global() throws Exception {
        mockMvc.perform(get("/empleados"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Recurso no encontrado"));
    }
}
