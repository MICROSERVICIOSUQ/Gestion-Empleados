package com.microservicios.gestionempleados.exception;

import com.microservicios.gestionempleados.exception.ErrorResponse.CampoInvalido;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 404 — el empleado solicitado no está registrado. */
    @ExceptionHandler(EmpleadoNoEncontradoException.class)
    public ResponseEntity<ErrorResponse> empleadoNoEncontrado(EmpleadoNoEncontradoException ex,
                                                              HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, ex.getMessage(), request, null);
    }

    /**
     * 409 — los datos enviados chocan con información que ya existe en el sistema.
     * La petición está bien formada, por eso no es un 400.
     */
    @ExceptionHandler({
            IdYaRegistradoException.class,
            EmailYaRegistradoException.class,
            NumeroEmpleadoYaRegistradoException.class
    })
    public ResponseEntity<ErrorResponse> conflictoDeDatos(RuntimeException ex,
                                                          HttpServletRequest request) {
        return construir(HttpStatus.CONFLICT, ex.getMessage(), request, null);
    }

    /** 400 — faltan campos obligatorios o alguno tiene formato inválido. */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> camposInvalidos(MethodArgumentNotValidException ex,
                                                         HttpServletRequest request) {
        List<CampoInvalido> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> new CampoInvalido(fe.getField(), fe.getDefaultMessage()))
                .sorted(Comparator.comparing(CampoInvalido::campo))
                .toList();
        return construir(HttpStatus.BAD_REQUEST,
                "La solicitud contiene campos inválidos", request, errores);
    }

    /** 400 — el JSON no se puede interpretar (sintaxis rota, enum o fecha inválidos). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> cuerpoIlegible(HttpMessageNotReadableException ex,
                                                        HttpServletRequest request) {
        return construir(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es un JSON válido o contiene valores con formato incorrecto",
                request, null);
    }

    /** 404 — ruta inexistente o método no soportado en una ruta existente. */
    @ExceptionHandler({NoHandlerFoundException.class, HttpRequestMethodNotSupportedException.class})
    public ResponseEntity<ErrorResponse> recursoNoEncontrado(HttpServletRequest request) {
        return construir(HttpStatus.NOT_FOUND, "Recurso no encontrado", request, null);
    }

    private ResponseEntity<ErrorResponse> construir(HttpStatus status, String mensaje,
                                                    HttpServletRequest request,
                                                    List<CampoInvalido> errores) {
        ErrorResponse cuerpo = new ErrorResponse(
                OffsetDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensaje,
                request.getRequestURI(),
                errores
        );
        return ResponseEntity.status(status).body(cuerpo);
    }
}
