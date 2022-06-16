package org.youngmonkeys.ezyplatform.rx;

public interface RxConsumer<T> {

    void accept(T t) throws Exception;
}
