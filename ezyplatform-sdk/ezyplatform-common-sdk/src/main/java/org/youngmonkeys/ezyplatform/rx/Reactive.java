/*
 * Copyright 2022 youngmonkeys.org
 * 
 * Licensed under the ezyplatform, Version 1.0.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     https://youngmonkeys.org/licenses/ezyplatform-1.0.0.txt
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.youngmonkeys.ezyplatform.rx;

import com.tvd12.ezyfox.concurrent.EzyExecutors;
import lombok.Getter;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.tvd12.ezyfox.io.EzyStrings.traceStackToString;
import static org.youngmonkeys.ezyplatform.util.Exceptions.exceptionsToString;

public final class Reactive {

    private static final ExecutorService EXECUTOR_SERVICE;
    private static final int DEFAULT_TIMEOUT_SECONDS = 15;

    static {
        EXECUTOR_SERVICE = EzyExecutors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 4,
            "reactive"
        );
    }

    private Reactive() {}

    public static <T> Single<T> single(Collection<T> values) {
        return new Single<>(values);
    }

    public static Multiple multiple() {
        return new Multiple();
    }

    public static void destroy() {
        EXECUTOR_SERVICE.shutdown();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class Single<T> {

        private final List<Object> firstValues;
        private final List<RxFunction> mappers = new ArrayList<>();

        public Single(Collection<T> values) {
            this.firstValues = new ArrayList<>(values);
        }

        public <R> Single<R> map(RxFunction<T, R> mapper) {
            mappers.add(mapper);
            return (Single) this;
        }

        public Single<T> operate(RxConsumer<T> consumer) {
            return map(it -> {
                consumer.accept(it);
                return null;
            });
        }

        public Multiple toMultiple() {
            return multiple().registers(
                firstValues,
                it -> {
                    Object mappedValue = it;
                    for (RxFunction mapper : mappers) {
                        mappedValue = mapper.apply(mappedValue);
                    }
                    return mappedValue;
                }
            );
        }

        public <R> List<R> blockingGetList() {
            RxValueMap map = toMultiple().blockingGet();
            List<R> answer = new ArrayList<>();
            for (Object firstValue : firstValues) {
                answer.add(map.get(firstValue));
            }
            return answer;
        }
    }

    public static class Multiple {

        private final Map<Object, RxSupplier> suppliers = new HashMap<>();

        public Multiple register(Object name, RxSupplier supplier) {
            suppliers.put(name, supplier);
            return this;
        }

        public Multiple registerOperation(Object name, RxOperation operation) {
            return register(name, () -> {
                operation.run();
                return null;
            });
        }

        public <T, R> Multiple registers(Collection<T> values, RxFunction<T, R> func) {
            values.forEach(it -> register(it, () -> func.apply(it)));
            return this;
        }

        public <T> Multiple registerConsumers(Collection<T> values, RxConsumer<T> consumer) {
            return registers(values, it -> {
                consumer.accept(it);
                return null;
            });
        }

        public void blockingExecute() {
            blockingGet(it -> Function.identity());
        }

        public void blockingConsume(Consumer<RxValueMap> consumer) {
            blockingGet(it -> {
                consumer.accept(it);
                return null;
            });
        }

        public <T> List<T> blockingGetList() {
            return blockingGet(RxValueMap::valueList);
        }

        public <T> Set<T> blockingGetSet() {
            return blockingGet(RxValueMap::valueSet);
        }

        public RxValueMap blockingGet() {
            return blockingGet(it -> it);
        }

        public <T> T blockingGet(Function<RxValueMap, T> mapper) {
            return blockingGet(
                mapper,
                DEFAULT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
            );
        }

        public <T> T blockingGet(
            Function<RxValueMap, T> mapper,
            int timeout,
            TimeUnit timeUnit
        ) {
            if (suppliers.isEmpty()) {
                return mapper.apply(RxValueMap.EMPTY_MAP);
            }
            int taskCount = suppliers.size();
            List<Exception> exceptions = Collections.synchronizedList(
                new ArrayList<>()
            );
            RxValueMap result = new RxValueMap(taskCount);
            CountDownLatch countDown = new CountDownLatch(taskCount);
            for (Entry<Object, RxSupplier> entry : suppliers.entrySet()) {
                Object name = entry.getKey();
                EXECUTOR_SERVICE.execute(() -> {
                    try {
                        Object value = entry.getValue().get();
                        if (value != null) {
                            result.put(name, value);
                        }
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        countDown.countDown();
                    }
                });
            }
            try {
                countDown.await(timeout, timeUnit);
            } catch (Exception e) {
                exceptions.add(e);
            }
            if (exceptions.isEmpty()) {
                return mapper.apply(result);
            } else {
                throw new RxException(exceptions);
            }
        }
    }

    public interface RxOperation {
        void run() throws Exception;
    }

    public interface RxSupplier {
        Object get() throws Exception;
    }

    public interface RxFunction<T, R> {
        R apply(T t) throws Exception;
    }

    public interface RxConsumer<T> {
        void accept(T t) throws Exception;
    }

    public static class RxValueMap {

        private final Map<Object, Object> map;

        public static final RxValueMap EMPTY_MAP = new RxValueMap(
            Collections.emptyMap()
        ) {
            @Override
            public void put(Object key, Object value) {}
        };

        public RxValueMap(int capacity) {
            this(new ConcurrentHashMap<>(capacity));
        }

        public RxValueMap(Map<Object, Object> map) {
            this.map = map;
        }

        public void put(Object key, Object value) {
            this.map.put(key, value);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Object key) {
            return (T) map.get(key);
        }

        @SuppressWarnings("unchecked")
        public <T> T get(Object key, T defaultValue) {
            T value = (T) map.get(key);
            return value != null ? value : defaultValue;
        }

        @SuppressWarnings("unchecked")
        public <T> T firstValue() {
            return (T) map.values().iterator().next();
        }

        public <T> T firstValueOrNull() {
            return isEmpty() ? null : firstValue();
        }

        @SuppressWarnings("unchecked")
        public <T> List<T> valueList() {
            return new ArrayList<>((Collection<T>) map.values());
        }

        @SuppressWarnings("unchecked")
        public <T> Set<T> valueSet() {
            return new HashSet<>((Collection<T>) map.values());
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public Map<String, Object> valueMap() {
            return (Map) map;
        }
    
        @SuppressWarnings({"rawtypes", "unchecked"})
        public <K, V> Map<K, V> typedValueMap() {
            return (Map) map;
        }

        public int size() {
            return map.size();
        }

        public boolean isEmpty() {
            return map.isEmpty();
        }
    }

    @Getter
    public static class RxException extends RuntimeException {
        private static final long serialVersionUID = -8667468848025369677L;
        
        private final List<Exception> exceptions;

        public RxException(Exception exception) {
            super(traceStackToString(exception));
            this.exceptions = Collections.singletonList(exception);
        }

        public RxException(List<Exception> exceptions) {
            super(exceptionsToString(exceptions));
            this.exceptions = exceptions;
        }
    }
}
