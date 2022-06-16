package org.youngmonkeys.ezyplatform.rx;

public interface RxFunction<T, R> {

    R apply(T t) throws Exception;
}
