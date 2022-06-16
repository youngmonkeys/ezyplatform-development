package org.youngmonkeys.ezyplatform.rx;

public interface RxSingle<T> extends RxOperation {

    <R> RxSingle<R> mapItem(RxFunction<T, R> mapper);

    RxSingle<T> mapItemRx(RxOperationSupplier<T> supplier);

    RxSingle<T> operateItem(RxConsumer<T> consumer);

    RxMultiple toMultiple();
}
