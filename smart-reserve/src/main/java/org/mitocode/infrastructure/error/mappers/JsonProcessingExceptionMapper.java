package org.mitocode.infrastructure.error.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
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
public class JsonProcessingExceptionMapper implements ExceptionMapper<JsonProcessingException> {

    @Override
    public Response toResponse(JsonProcessingException exception) {
        log.error("JsonProcessingException: {}", exception.getMessage(), exception);

        List<ErrorDetailDto> list = List.of(new ErrorDetailDto(exception.getMessage()));

        return Response
                .status(Response.Status.BAD_REQUEST)
                .entity(
                        ApiErrorResponse
                                .builder()
                                .errorId(UUID.randomUUID().toString())
                                .typeError(ErrorType.VALIDATION_ERROR.name())
                                .message(ErrorType.VALIDATION_ERROR.getDescription())
                                .status(ErrorType.VALIDATION_ERROR.getStatus().getStatusCode())
                                .errorDetails(list)
                                .timestamp(LocalDateTime.now())
                                .build()
                )
                .build();
    }
}
