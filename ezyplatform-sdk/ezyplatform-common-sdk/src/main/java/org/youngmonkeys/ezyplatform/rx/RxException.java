package org.youngmonkeys.ezyplatform.rx;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

import static com.tvd12.ezyfox.io.EzyStrings.exceptionsToString;
import static com.tvd12.ezyfox.io.EzyStrings.traceStackToString;

@Getter
public class RxException extends RuntimeException {
    private static final long serialVersionUID = -8667468848025369677L;

    private final List<Exception> exceptions;

    public RxException(Exception exception) {
        super(traceStackToString(exception));
        this.exceptions = Collections.singletonList(exception);
    }

    public RxException(List<Exception> exceptions) {
        super(exceptionsToString(exceptions));
        this.exceptions = exceptions;
    }
}