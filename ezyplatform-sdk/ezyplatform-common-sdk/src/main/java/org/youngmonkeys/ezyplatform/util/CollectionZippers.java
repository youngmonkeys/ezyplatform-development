package org.youngmonkeys.ezyplatform.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

public final class CollectionZippers {

    private CollectionZippers() {}

    public static <K, V> Map<K, V> zip(
        Collection<K> keys,
        Collection<V> values
    ) {
        Map<K, V> map = new HashMap<>();
        Iterator<V> valueIterator = values.iterator();
        for (K key : keys) {
            if (valueIterator.hasNext()) {
                map.put(key, valueIterator.next());
            } else {
                break;
            }
        }
        return map;
    }

    public static <K, VI, VO> Map<K, VO> zip(
        Collection<K> keys,
        Collection<VI> values,
        Function<VI, VO> valueConverter
    ) {
        Map<K, VO> map = new HashMap<>();
        Iterator<VI> valueIterator = values.iterator();
        for (K key : keys) {
            if (valueIterator.hasNext()) {
                map.put(
                    key,
                    valueConverter.apply(valueIterator.next())
                );
            } else {
                break;
            }
        }
        return map;
    }
}
