package org.mitocode.infrastructure.input.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.eclipse.microprofile.openapi.annotations.media.Schema;

@Setter
@Getter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "OrderShipmentResponse", description = "Respuesta estándar paginadas o únicas")
public class ApiResponse<T> {

    @Schema(description = "Objeto de datos principal (usado en respuestas simples)")
    private T data;

    @Schema(description = "Código de estado HTTP o interno", example = "200")
    private Integer statusCode;

    @Schema(description = "Número de página actual", example = "1")
    private Integer currentPage;

    @Schema(description = "Cantidad de páginas", example = "10")
    private Integer totalPages;

    @Schema(description = "Total de registros disponibles", example = "150")
    private Integer totalElements;
}