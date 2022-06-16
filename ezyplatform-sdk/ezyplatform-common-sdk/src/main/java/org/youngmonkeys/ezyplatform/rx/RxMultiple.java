package org.youngmonkeys.ezyplatform.rx;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public interface RxMultiple extends RxOperation {

    <R> RxMultiple mapBegin(RxFunction<RxValueMap, R> mapper);

    <T, R> RxMultiple map(RxFunction<T, R> mapper);

    void blockingConsume(Consumer<RxValueMap> consumer);

    <T> T blockingGet(RxFunction<RxValueMap, T> mapper);

    <T> T blockingGet(
        RxFunction<RxValueMap, T> mapper,
        int timeout,
        TimeUnit timeUnit
    );
}
