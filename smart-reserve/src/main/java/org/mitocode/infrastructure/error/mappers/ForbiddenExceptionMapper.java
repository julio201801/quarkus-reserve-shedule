package org.mitocode.infrastructure.error.mappers;

import io.quarkus.security.ForbiddenException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.domain.enums.ErrorType;
import org.mitocode.infrastructure.error.dto.ApiErrorResponse;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Provider
public class ForbiddenExceptionMapper implements ExceptionMapper<ForbiddenException> {


    @Override
    public Response toResponse(ForbiddenException e) {
        log.error("Error ForbiddenException: ", e);

        return Response
                .status(ErrorType.FORBIDDEN_ERROR.getStatus().getStatusCode())
                .entity(ApiErrorResponse
                        .builder()
                        .errorId(UUID.randomUUID().toString())
                        .typeError(ErrorType.FORBIDDEN_ERROR.name())
                        .message(ErrorType.FORBIDDEN_ERROR.getDescription())
                        .status(ErrorType.FORBIDDEN_ERROR.getStatus().getStatusCode())
                        .timestamp(LocalDateTime.now())
                        .build())
                .build();
    }
}
