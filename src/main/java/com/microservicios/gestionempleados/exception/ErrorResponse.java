package com.microservicios.gestionempleados.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * Cuerpo JSON común a todas las respuestas de error.
 * La lista {@code errores} solo aparece en los fallos de validación.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private List<CampoInvalido> errores;

    public ErrorResponse() {}

    public ErrorResponse(OffsetDateTime timestamp, int status, String error,
                         String message, String path, List<CampoInvalido> errores) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.errores = errores;
    }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public List<CampoInvalido> getErrores() { return errores; }
    public void setErrores(List<CampoInvalido> errores) { this.errores = errores; }

    /** Detalle de un campo que no pasó la validación. */
    public record CampoInvalido(String campo, String mensaje) {}
}
