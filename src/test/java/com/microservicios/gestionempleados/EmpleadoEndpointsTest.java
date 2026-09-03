package com.microservicios.gestionempleados;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String ANA = """
            {
              "nombre": "Ana",
              "apellido": "García",
              "email": "ana.garcia@empresa.com",
              "numeroEmpleado": "EMP-001",
              "cargo": "Desarrolladora Senior",
              "area": "Tecnología",
              "departamentoId": "DEPT-TI",
              "fechaIngreso": "2024-03-01"
            }
            """;

    private static String idCreado;

    @Test
    @Order(1)
    void registroExitoso() throws Exception {
        String body = mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ANA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACTIVO"))
                .andExpect(jsonPath("$.fechaIngreso").value("2024-03-01"))
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        JsonNode json = mapper.readTree(body);
        idCreado = json.get("id").asText();
    }

    @Test
    @Order(2)
    void consultaEmpleadoExistente() throws Exception {
        mockMvc.perform(get("/empleados/" + idCreado))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("ana.garcia@empresa.com"))
                .andExpect(jsonPath("$.numeroEmpleado").value("EMP-001"));
    }

    @Test
    @Order(3)
    void emailDuplicadoDevuelve400() throws Exception {
        String pedroMismoEmail = ANA.replace("\"numeroEmpleado\": \"EMP-001\"",
                "\"numeroEmpleado\": \"EMP-999\"");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pedroMismoEmail))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El email 'ana.garcia@empresa.com' ya está registrado"));
    }

    @Test
    @Order(4)
    void numeroEmpleadoDuplicadoDevuelve400() throws Exception {
        String otroEmailMismoNumero = ANA.replace("ana.garcia@empresa.com",
                "pedro.lopez@empresa.com");

        mockMvc.perform(post("/empleados")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(otroEmailMismoNumero))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("El número de empleado 'EMP-001' ya está registrado"));
    }

    @Test
    @Order(5)
    void empleadoInexistenteDevuelve404ConMensajeExacto() throws Exception {
        mockMvc.perform(get("/empleados/id-que-no-existe"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("El empleado con id id-que-no-existe no existe"));
    }

    @Test
    @Order(6)
    void rutaNoDefinidaDevuelve404Global() throws Exception {
        mockMvc.perform(get("/ruta-invalida"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Recurso no encontrado"));
    }

    @Test
    @Order(7)
    void metodoNoDefinidoEnRutaExistenteDevuelve404Global() throws Exception {
        mockMvc.perform(get("/empleados"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Recurso no encontrado"));
    }
}
