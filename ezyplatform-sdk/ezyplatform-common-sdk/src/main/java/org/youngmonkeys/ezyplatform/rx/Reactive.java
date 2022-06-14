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
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyStrings.exceptionsToString;
import static com.tvd12.ezyfox.io.EzyStrings.traceStackToString;

public final class Reactive {

    private static final ExecutorService EXECUTOR_SERVICE;
    private static final int DEFAULT_TIMEOUT_SECONDS = 15;

    static {
        EXECUTOR_SERVICE = EzyExecutors.newFixedThreadPool(
            1,
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

    public static Multiple multiple(ReturnType returnType) {
        return new Multiple(returnType);
    }

    public static void destroy() {
        EXECUTOR_SERVICE.shutdown();
    }

    public static interface Operation {
        <R> List<R> blockingGetList();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static class Single<T> implements Operation {

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
            return multiple(ReturnType.LIST).registers(
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
            return toMultiple().blockingGetList();
        }
    }

    public static class Multiple implements Operation {

        private final ReturnType returnType;
        private final List<Object> taskKeys = new ArrayList<>();
        private final Map<Object, Object> tasks = new HashMap<>();

        public Multiple() {
            this(ReturnType.DEFAULT);
        }

        public Multiple(ReturnType returnType) {
            this.returnType = returnType;
        }

        public Multiple register(Object name, Operation operation) {
            taskKeys.add(name);
            tasks.put(name, operation);
            return this;
        }

        public Multiple register(Object name, RxSupplier supplier) {
            taskKeys.add(name);
            tasks.put(name, supplier);
            return this;
        }

        public Multiple registerOperation(Object name, RxOperation operation) {
            taskKeys.add(name);
            return register(name, () -> {
                operation.run();
                return null;
            });
        }

        public <T, R> Multiple registers(Collection<T> values, RxFunction<T, R> func) {
            taskKeys.addAll(values);
            values.forEach(it -> register(it, () -> func.apply(it)));
            return this;
        }

        public <T> Multiple registerConsumers(Collection<T> values, RxConsumer<T> consumer) {
            taskKeys.addAll(values);
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

        public <T> T blockingCastGet() {
            return blockingGet(it -> it.castGet(returnType));
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
            if (tasks.isEmpty()) {
                return mapper.apply(RxValueMap.EMPTY_MAP);
            }
            RxValueMap result = new RxValueMap(taskKeys);
            List<InternalTask> flattenTasks = flatTasks(result);
            List<Exception> exceptions = Collections.synchronizedList(
                new ArrayList<>()
            );
            CountDownLatch countDown = new CountDownLatch(flattenTasks.size());
            for (InternalTask task : flattenTasks) {
                EXECUTOR_SERVICE.execute(() -> {
                    try {
                        Object value = task.supplier.get();
                        if (value != null) {
                            task.resultMap.put(task.taskKey, value);
                        }
                    } catch (Exception e) {
                        exceptions.add(e);
                    } finally {
                        countDown.countDown();
                    }
                });
            }
            try {
                if (!countDown.await(timeout, timeUnit)) {
                    exceptions.add(
                        new TimeoutException(
                            "timeout, maybe some tasks undone: " +
                                getEmptyValueTasks(flattenTasks)
                        )
                    );
                }
            } catch (Exception e) {
                exceptions.add(e);
            }
            if (exceptions.isEmpty()) {
                convertRxMapValues(result);
                return mapper.apply(result);
            } else {
                throw new RxException(exceptions);
            }
        }

        private List<InternalTask> flatTasks(
            RxValueMap rootValueMap
        ) {
            RxValueMap parentResultMap = rootValueMap;
            List<InternalTask> flattenTasks = new ArrayList<>();
            Queue<MultipleTask> taskQueue = new LinkedList<>();
            Set<Entry<Object, Object>> taskEntries = tasks.entrySet();
            while (true) {
                for (Entry<Object, Object> e : taskEntries) {
                    Object taskKey = e.getKey();
                    Object task = e.getValue();
                    if (task instanceof RxSupplier) {
                        flattenTasks.add(
                            new InternalTask(
                                parentResultMap,
                                taskKey,
                                (RxSupplier) task
                            )
                        );
                    } else {
                        @SuppressWarnings("rawtypes")
                        Multiple multiple = task instanceof Multiple
                            ? (Multiple) task
                            : ((Single) task).toMultiple();
                        RxValueMap resultMap = new RxValueMap(multiple.taskKeys);

                        parentResultMap.put(taskKey, resultMap);
                        taskQueue.offer(
                            new MultipleTask(resultMap, multiple.tasks.entrySet())
                        );
                    }
                }
                MultipleTask multipleTask = taskQueue.poll();
                if (multipleTask == null) {
                    break;
                }
                parentResultMap = multipleTask.resultMap;
                taskEntries = multipleTask.taskEntries;
            }
            return flattenTasks;
        }

        @SuppressWarnings("unchecked")
        private void convertRxMapValues(RxValueMap rootValueMap) {
            Stack<Object> stack = new Stack<>();
            RxValueMap resultMap = rootValueMap;
            while (true) {
                int rxMapCount = 0;
                for (Entry<Object, Object> e : resultMap.map.entrySet()) {
                    Object value = e.getValue();
                    if (value instanceof RxValueMap) {
                        stack.push(e);
                        stack.push(resultMap);
                        ++ rxMapCount;
                    }
                }
                if (rxMapCount == 0 && stack.size() > 0) {
                    Entry<Object, Object> e = (Entry<Object, Object>) stack.pop();
                    RxValueMap parent = (RxValueMap) stack.pop();
                    parent.put(e.getKey(), resultMap);
                }
                if (stack.isEmpty()) {
                    break;
                }
                Entry<Object, Object> e = (Entry<Object, Object>) stack.peek();
                resultMap = (RxValueMap) e.getValue();
            }
        }

        private List<Object> getEmptyValueTasks(
            List<InternalTask> tasks
        ) {
            return tasks.stream()
                .filter(it -> it.resultMap.isEmpty())
                .map(it -> it.taskKey)
                .collect(Collectors.toList());
        }
    }

    private static class MultipleTask {

        private final RxValueMap resultMap;
        private final Set<Entry<Object, Object>> taskEntries;

        private MultipleTask(
            RxValueMap resultMap,
            Set<Entry<Object, Object>> taskEntries
        ) {
            this.resultMap = resultMap;
            this.taskEntries = taskEntries;
        }
    }

    private static class InternalTask {

        private final Object taskKey;
        private final RxSupplier supplier;
        private final RxValueMap resultMap;

        private InternalTask(
            RxValueMap resultMap,
            Object taskKey,
            RxSupplier supplier
        ) {
            this.taskKey = taskKey;
            this.supplier = supplier;
            this.resultMap = resultMap;
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

        private final List<Object> taskKeys;
        private final Map<Object, Object> map;

        public static final RxValueMap EMPTY_MAP = new RxValueMap(
            Collections.emptyList()
        ) {
            @Override
            public void put(Object key, Object value) {}
        };

        public RxValueMap(List<Object> taskKeys) {
            this.taskKeys = taskKeys;
            this.map = new ConcurrentHashMap<>(taskKeys.size());
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
            List<T> answer = new ArrayList<>();
            for (Object taskKey : taskKeys) {
                answer.add((T) map.get(taskKey));
            }
            return answer;
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

        @SuppressWarnings("unchecked")
        public <T> T castGet(ReturnType returnType) {
            if (returnType == ReturnType.FIST) {
                return firstValue();
            }
            if (returnType == ReturnType.FIST_OR_NULL) {
                return firstValueOrNull();
            }
            if (returnType == ReturnType.LIST) {
                return (T) valueList();
            }
            if (returnType == ReturnType.SET) {
                return (T) valueSet();
            }
            if (returnType == ReturnType.MAP) {
                return (T) map;
            }
            return (T) this;
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

    public static enum ReturnType {
        DEFAULT,
        FIST,
        FIST_OR_NULL,
        MAP,
        LIST,
        RX_MAP,
        SET
    }
}
