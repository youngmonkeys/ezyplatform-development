package org.youngmonkeys.ezyplatform.rx;

import java.util.function.Function;

public interface RxOperationSupplier<T> extends Function<T, RxOperation> {}
