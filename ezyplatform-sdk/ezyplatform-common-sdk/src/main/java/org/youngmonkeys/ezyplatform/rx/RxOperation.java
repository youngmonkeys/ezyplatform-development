package org.youngmonkeys.ezyplatform.rx;

public interface RxOperation {

    <T> T blockingGet();

    void blockingExecute();
}
