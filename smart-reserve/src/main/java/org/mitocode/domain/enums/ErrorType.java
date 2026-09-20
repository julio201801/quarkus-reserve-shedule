package org.mitocode.domain.enums;

import jakarta.ws.rs.core.Response;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PACKAGE)
public enum ErrorType {
    VALIDATION_ERROR("V-01", "Error en la validación de datos", Response.Status.BAD_REQUEST),
    GENERIC_ERROR("G-02", "Error interno del sistema. Reintentar más tarde", Response.Status.INTERNAL_SERVER_ERROR),
    BUSINESS_NOT_FOUND_ERROR("B-03", "Valor no encontrado", Response.Status.PRECONDITION_REQUIRED),
    UNAUTHORIZED_ERROR("S-04", "No Autorizado", Response.Status.UNAUTHORIZED),
    FORBIDDEN_ERROR("S-05", "Acceso no Permitido", Response.Status.FORBIDDEN),
    SCHEDULE_CONFLICT_ERROR("C-06", "El horario se solapa con otro existente", Response.Status.CONFLICT),
    BUSINESS_NOT_FOUND_CUSTOMER("B-03", "Cliente no encontrado", Response.Status.PRECONDITION_REQUIRED),
    BUSINESS_NOT_FOUND_CUSTOMER_NOT_ACTIVE("B-03", "Cliente no encontrado o inactivo", Response.Status.PRECONDITION_REQUIRED),
    BUSINESS_NOT_FOUND_PROFESSIONAL("B-03", "Profesional no encontrado", Response.Status.PRECONDITION_REQUIRED),
    SCHEDULE_NOT_AVAILABLE_ERROR("C-07","No hay horarios disponibles que coincidan con el intervalo solicitado",Response.Status.CONFLICT),
    SCHEDULE_RESERVATION_CONFLICT_ERROR("C-07","El intervalo solicitado coincide con una reserva activa existente.",Response.Status.CONFLICT);
    private final String code;
    private final String description;
    private final Response.Status status;
}
