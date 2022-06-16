package org.youngmonkeys.ezyplatform.rx;

import java.util.List;
import java.util.Set;

public interface RxOperation {

    void blockingExecute();

    <T> List<T> blockingGetList();

    <T> Set<T> blockingGetSet();

    <T> T blockingCastGet();

    RxValueMap blockingGet();
}
