package org.mitocode.infrastructure.context;

import jakarta.enterprise.context.RequestScoped;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@RequestScoped
public class TransactionContext {

    private String traceParent;
    private String customerCode;
    private long requestStartTimeNanos;
    private String path;
    private String method;
}
