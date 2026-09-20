package org.mitocode.infrastructure.filters;

import io.opentelemetry.api.trace.Span;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.*;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.mitocode.infrastructure.context.TransactionContext;

import java.io.IOException;

@Slf4j
@Provider
// @PreMatching: se ejecuta antes del matching de rutas, es decir, antes de que Quarkus
// resuelva qué recurso/anotaciones de seguridad aplican. En quarkus-rest (RESTEasy Reactive)
// el chequeo de autenticación/@RolesAllowed NO es un ContainerRequestFilter más que respete
// @Priority: es un handler interno insertado en una fase fija del pipeline. Por eso, con un
// token inválido/expirado, un filtro post-matching (aunque tenga prioridad baja) nunca llega
// a ejecutarse; @PreMatching sí garantiza que este filtro corra siempre, sin importar el token.
@PreMatching
@Priority(Priorities.AUTHENTICATION - 1)
public class IncomingRequestResponseFilter implements ContainerRequestFilter, ContainerResponseFilter {

    /**
     * Representa la cabecera 'traceparent' según el estándar W3C Trace Context
     * Se utiliza en la trazabilidad distribuida para vincular y correlacionar
     * las solicitudes a medida que viajan a través de múltiples microservicios.
     */
    private static final String TRACEPARENT_HEADER = "traceparent";
    private static final String START_TIME_PROPERTY = "requestStartTimeNanos";

    private TransactionContext transactionContext;

    public IncomingRequestResponseFilter(TransactionContext transactionContext) {
        this.transactionContext = transactionContext;
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext) throws IOException {

        transactionContext.setRequestStartTimeNanos(System.nanoTime());
        String method = containerRequestContext.getMethod();
        String path = containerRequestContext.getUriInfo().getAbsolutePath().toString();

        transactionContext.setPath(path);
        transactionContext.setMethod(method);

        // Omitir la validación para el openapi/swagger
        if (path.startsWith("q/")) {
            return;
        }

        String traceparent = "";

        transactionContext.setTraceParent(traceparent);
        Span currentSpan = Span.current();
        String traceId = currentSpan.getSpanContext().getTraceId();
        String spanId = currentSpan.getSpanContext().getSpanId();

        transactionContext.setTraceParent(traceparent);

        log.info("[START] - request init: traceparent:{}, traceId:{}, spanId:{}, method:{}, path:{}",
                traceparent, traceId, spanId, method, path);
    }

    @Override
    public void filter(ContainerRequestContext containerRequestContext, ContainerResponseContext containerResponseContext) throws IOException {

        int statusCode = containerResponseContext.getStatus();

        log.info("[END] - response: path:{}, method:{}, statusCode:{}, duration(ms):{}",
                transactionContext.getPath(), transactionContext.getMethod(), statusCode, durationInMs());
    }

    private long durationInMs() {

        return (System.nanoTime() - transactionContext.getRequestStartTimeNanos()) / 1_000_000;
    }
}

