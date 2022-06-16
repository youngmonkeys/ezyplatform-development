package org.youngmonkeys.ezyplatform.rx;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@SuppressWarnings({"rawtypes", "unchecked"})
public class RxValueMap {

    protected final List<Object> taskKeys;
    private final List<RxFunction> mappers;
    protected final Map<Object, Object> map;
    protected final Reactive.ReturnType returnType;

    public static final RxValueMap EMPTY_MAP = new RxValueMap(
        Collections.emptyList(),
        Reactive.ReturnType.DEFAULT,
        Collections.emptyList()
    ) {
        @Override
        public void put(Object key, Object value) {}
    };

    public RxValueMap(
        List<Object> taskKeys,
        Reactive.ReturnType returnType,
        List<RxFunction> mappers
    ) {
        this.mappers = mappers;
        this.taskKeys = taskKeys;
        this.returnType = returnType;
        this.map = new ConcurrentHashMap<>(taskKeys.size());
    }

    public void put(Object key, Object value) {
        this.map.put(key, value);
    }

    public <T> T get(Object key) {
        return (T) map.get(key);
    }

    public <T> T get(Object key, T defaultValue) {
        T value = (T) map.get(key);
        return value != null ? value : defaultValue;
    }

    public <T> T firstValue() {
        return (T) map.values().iterator().next();
    }

    public <T> T firstValueOrNull() {
        return isEmpty() ? null : firstValue();
    }

    public <T> List<T> valueList() {
        List<T> answer = new ArrayList<>();
        for (Object taskKey : taskKeys) {
            answer.add((T) map.get(taskKey));
        }
        return answer;
    }

    public <T> Set<T> valueSet() {
        return new HashSet<>((Collection<T>) map.values());
    }

    public Map<String, Object> valueMap() {
        return (Map) map;
    }

    public <K, V> Map<K, V> typedValueMap() {
        return (Map) map;
    }

    public <T> T castGet() {
        if (returnType == Reactive.ReturnType.FIST) {
            return firstValue();
        }
        if (returnType == Reactive.ReturnType.FIST_OR_NULL) {
            return firstValueOrNull();
        }
        if (returnType == Reactive.ReturnType.LIST) {
            return (T) valueList();
        }
        if (returnType == Reactive.ReturnType.SET) {
            return (T) valueSet();
        }
        if (returnType == Reactive.ReturnType.MAP) {
            return (T) map;
        }
        Object finalResult = this;
        if (mappers != null) {
            for (RxFunction mapperItem : mappers) {
                try {
                    finalResult = mapperItem.apply(finalResult);
                } catch (Exception e) {
                    throw new Reactive.RxException(e);
                }
            }
        }
        return (T) finalResult;
    }

    public int size() {
        return map.size();
    }

    public boolean isEmpty() {
        return map.isEmpty();
    }
}
