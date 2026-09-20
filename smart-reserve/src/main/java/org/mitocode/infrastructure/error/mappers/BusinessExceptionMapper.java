package org.mitocode.infrastructure.error.mappers;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.infrastructure.error.dto.ApiErrorResponse;
import org.mitocode.infrastructure.error.dto.ErrorDetailDto;
import org.mitocode.infrastructure.error.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Provider
public class BusinessExceptionMapper implements ExceptionMapper<BusinessException> {
    @Override
    public Response toResponse(BusinessException e) {
        log.error("Error BusinessException:{}", e.getDescription());

        List<ErrorDetailDto> list = List.of(new ErrorDetailDto(e.getDescription()));

        return Response
                .status(e.getType().getStatus())
                .entity(
                        ApiErrorResponse
                                .builder()
                                .errorId(e.getId().toString())
                                .typeError(e.getType().name())
                                .message(e.getType().getDescription())
                                .status(e.getType().getStatus().getStatusCode())
                                .errorDetails(list)
                                .timestamp(LocalDateTime.now())
                                .build()
                ).build();
    }
}
