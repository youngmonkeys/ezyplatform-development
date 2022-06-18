package org.youngmonkeys.ezyplatform.rx;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RxValueMap {

    <T> T get(Object key);

    <T> T get(Object key, T defaultValue);

    <T> T firstValue();

    <T> T firstValueOrNull();

    <T> List<T> valueList();

    <T> Set<T> valueSet();

    Map<String, Object> valueMap();

    <K, V> Map<K, V> typedValueMap();

    <T> T castGet();

    int size();

    boolean isEmpty();
}
