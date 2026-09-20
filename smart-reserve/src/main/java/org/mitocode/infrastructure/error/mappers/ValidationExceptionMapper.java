package org.mitocode.infrastructure.error.mappers;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.infrastructure.error.dto.ApiErrorResponse;
import org.mitocode.infrastructure.error.dto.ErrorDetailDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        log.error("ValidationExceptionMapper-error: {}", exception.getMessage(), exception);

        List<ErrorDetailDto> errors = exception
                .getConstraintViolations()
                .stream()
                .map(v -> new ErrorDetailDto(
                        obtenerNombreCampo(v), // Extrae el nombre del campo (ej: "nombre")
                        v.getMessage()// El mensaje del DTO (ej: "El nombre es obligatorio")
                )).toList();

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(
                        ApiErrorResponse
                                .builder()
                                .errorId(UUID.randomUUID().toString())
                                .typeError(ErrorType.VALIDATION_ERROR.name())
                                .message(ErrorType.VALIDATION_ERROR.getDescription())
                                .status(ErrorType.VALIDATION_ERROR.getStatus().getStatusCode())
                                .errorDetails(errors)
                                .timestamp(LocalDateTime.now())
                                .build()

                )
                .build();
    }

    private String obtenerNombreCampo(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        return path.substring(path.lastIndexOf('.') + 1);
    }
}
