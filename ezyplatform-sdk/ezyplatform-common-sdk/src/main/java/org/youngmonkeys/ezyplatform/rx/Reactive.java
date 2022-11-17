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

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@SuppressWarnings({"rawtypes", "unchecked"})
public final class Reactive {

    private static ExecutorService executorService;
    private static final int DEFAULT_TIMEOUT_SECONDS = 15;

    static {
        executorService = EzyExecutors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 4,
            "reactive"
        );
    }

    private Reactive() {}

    public static <T> Single<T> single(T value) {
        return new Single<>(value);
    }

    public static <T> Single<T> single(Collection<T> values) {
        return new Single<>(values);
    }

    public static Multiple multiple() {
        return new Multiple();
    }

    public static Multiple multiple(RxReturnType returnType) {
        return new Multiple(returnType);
    }

    public static void destroy() {
        executorService.shutdown();
    }

    public static void setExecutorService(ExecutorService executorService) {
        Reactive.executorService = executorService;
    }

    public static class RxComponent<T extends RxComponent<T>> {

        protected RxReturnType returnType;

        public RxComponent(RxReturnType returnType) {
            this.returnType = returnType;
        }

        public T returnTypeFirst() {
            return returnType(RxReturnType.FIST);
        }

        public T returnTypeFirstOrNull() {
            return returnType(RxReturnType.FIST_OR_NULL);
        }

        public T returnTypeList() {
            return returnType(RxReturnType.LIST);
        }

        public T returnTypeMap() {
            return returnType(RxReturnType.MAP);
        }

        public T returnTypeSet() {
            return returnType(RxReturnType.SET);
        }

        public T returnType(RxReturnType returnType) {
            this.returnType = returnType;
            return (T) this;
        }
    }

    public static class Single<T>
        extends RxComponent<Single<T>>
        implements RxSingle<T> {

        private final List<T> firstValues;
        private List<RxFunction> itemMappers;
        private RxOperationSupplier<T> itemOperationSupplier;

        public Single(T value) {
            this(
                Collections.singletonList(value),
                RxReturnType.FIST_OR_NULL
            );
        }

        public Single(Collection<T> values) {
            this(values, RxReturnType.LIST);
        }

        public Single(Collection<T> values, RxReturnType returnType) {
            super(returnType);
            this.firstValues = new ArrayList<>(values);
        }

        @Override
        public <R> Single<R> mapItem(RxFunction<T, R> mapper) {
            if (itemOperationSupplier != null) {
                throw new IllegalArgumentException(
                    "can not use the both mapItem and maxItemRx functions"
                );
            }
            if (itemMappers == null) {
                itemMappers = new ArrayList<>();
            }
            itemMappers.add(mapper);
            return (Single) this;
        }

        @Override
        public Single<T> mapItemRx(RxOperationSupplier<T> supplier) {
            if (itemMappers != null) {
                throw new IllegalArgumentException(
                    "can not use the both mapItem and maxItemRx functions"
                );
            }
            if (itemOperationSupplier != null) {
                throw new IllegalArgumentException(
                    "can not use maxItemRx function 2 times"
                );
            }
            this.itemOperationSupplier = supplier;
            return this;
        }

        @Override
        public RxSingle<T> operateItem(RxConsumer<T> consumer) {
            return mapItem(it -> {
                consumer.accept(it);
                return null;
            });
        }

        @Override
        public Multiple toMultiple() {
            if (itemOperationSupplier != null) {
                return multiple(returnType)
                    .registersRx(
                        firstValues,
                        itemOperationSupplier
                    );
            }
            return multiple(returnType)
                .registers(
                    firstValues,
                    it -> {
                        Object mappedValue = it;
                        if (itemMappers != null) {
                            for (RxFunction mapper : itemMappers) {
                                mappedValue = mapper.apply(mappedValue);
                            }
                        }
                        return mappedValue;
                    }
                );
        }

        @Override
        public void blockingExecute() {
            toMultiple().blockingExecute();
        }

        @Override
        public <R> R blockingGet() {
            return toMultiple().blockingGet();
        }
    }

    public static class Multiple
        extends RxComponent<Multiple>
        implements RxMultiple {

        private List<RxFunction> mappers;
        private final List<Object> taskKeys = new ArrayList<>();
        private final Map<Object, Object> tasks = new HashMap<>();

        public Multiple() {
            this(RxReturnType.DEFAULT);
        }

        public Multiple(RxReturnType returnType) {
            super(returnType);
        }

        public Multiple register(RxSupplier supplier) {
            return register(supplier, supplier);
        }

        public Multiple register(Object name, RxSupplier supplier) {
            taskKeys.add(name);
            tasks.put(name, supplier);
            return this;
        }

        public Multiple registerRx(Object name, RxOperation operation) {
            if (operation != null) {
                taskKeys.add(name);
                tasks.put(name, operation);
            }
            return this;
        }

        public Multiple registerOperation(
            RxRunnable operation
        ) {
            return registerOperation(operation, operation);
        }

        public Multiple registerOperation(
            Object name,
            RxRunnable operation
        ) {
            return register(name, () -> {
                operation.run();
                return null;
            });
        }

        public <T> Multiple registersRx(
            Collection<T> values,
            RxOperationSupplier<T> itemOperationSupplier
        ) {
            for (T value : values) {
                registerRx(value, itemOperationSupplier.apply(value));
            }
            return this;
        }

        public <T, R> Multiple registers(
            Collection<T> values,
            RxFunction<T, R> itemMapper
        ) {
            values.forEach(it -> register(it, () -> itemMapper.apply(it)));
            return this;
        }

        public <T> Multiple registerConsumers(
            Collection<T> values,
            RxConsumer<T> itemConsumer
        ) {
            return registers(values, it -> {
                itemConsumer.accept(it);
                return null;
            });
        }

        @Override
        public <R> Multiple mapBegin(RxFunction<RxValueMap, R> mapper) {
            return this.map(mapper);
        }

        @Override
        public <T, R> Multiple map(RxFunction<T, R> mapper) {
            if (returnType != RxReturnType.DEFAULT) {
                throw new IllegalArgumentException(
                    "can not set the both returnType != DEFAULT and mapper"
                );
            }
            if (mappers == null) {
                this.mappers = new ArrayList<>();
            }
            this.mappers.add(mapper);
            return this;
        }

        @Override
        public void blockingExecute() {
            blockingGet();
        }

        @Override
        public void blockingConsume(RxConsumer<RxValueMap> consumer) {
            blockingGet(it -> {
                consumer.accept(it);
                return null;
            });
        }

        @Override
        public <T> T blockingGet() {
            return blockingGet(
                DEFAULT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
            );
        }

        @Override
        public <T> T blockingGet(RxFunction<RxValueMap, T> mapper) {
            return blockingGet(
                mapper,
                DEFAULT_TIMEOUT_SECONDS,
                TimeUnit.SECONDS
            );
        }

        @Override
        public <T> T blockingGet(
            RxFunction<RxValueMap, T> mapper,
            int timeout,
            TimeUnit timeUnit
        ) {
            map(mapper);
            return blockingGet(timeout, timeUnit);
        }

        @Override
        public <T> T blockingGet(int timeout, TimeUnit timeUnit) {
            if (tasks.isEmpty()) {
                return new ResultValueMap(taskKeys, returnType, mappers)
                    .castGet();
            }
            ResultValueMap result = new ResultValueMap(
                taskKeys,
                returnType,
                mappers
            );
            List<InternalTask> flattenTasks = flatTasks(result);
            List<Exception> exceptions = new CopyOnWriteArrayList<>();
            CountDownLatch countDown = new CountDownLatch(flattenTasks.size());
            for (InternalTask task : flattenTasks) {
                executorService.execute(() -> {
                    try {
                        Object value = task.supplier.get();
                        if (value != null) {
                            task.resultMap.put(task.taskKey, value);
                            task.done.set(true);
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
                                getUndoneTaskKeys(flattenTasks)
                        )
                    );
                }
            } catch (Exception e) {
                exceptions.add(e);
            }
            if (exceptions.isEmpty()) {
                convertRxMapValues(result);
                return result.castGet();
            } else {
                throw new RxException(exceptions);
            }
        }

        private List<InternalTask> flatTasks(
            ResultValueMap rootValueMap
        ) {
            ResultValueMap parentResultMap = rootValueMap;
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
                        Multiple multiple = task instanceof Multiple
                            ? (Multiple) task
                            : ((Single) task).toMultiple();
                        ResultValueMap resultMap = new ResultValueMap(
                            multiple.taskKeys,
                            multiple.returnType,
                            multiple.mappers
                        );

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

        private List<Object> getUndoneTaskKeys(
            List<InternalTask> tasks
        ) {
            return tasks.stream()
                .filter(it -> !it.done.get())
                .map(it -> it.taskKey)
                .collect(Collectors.toList());
        }

        private void convertRxMapValues(ResultValueMap rootValueMap) {
            Stack<Object> stack = new Stack<>();
            ResultValueMap resultMap = rootValueMap;
            while (true) {
                int rxMapCount = 0;
                for (Entry<Object, Object> e : resultMap.map.entrySet()) {
                    Object value = e.getValue();
                    if (value instanceof RxValueMap) {
                        stack.push(resultMap);
                        stack.push(e);
                        ++rxMapCount;
                    }
                }
                if (rxMapCount == 0 && stack.size() > 0) {
                    Object taskKey = ((Entry) stack.pop()).getKey();
                    ResultValueMap parent = (ResultValueMap) stack.pop();
                    parent.put(taskKey, resultMap.castGet());
                }
                if (stack.isEmpty()) {
                    break;
                }
                resultMap = (ResultValueMap) ((Entry) stack.peek()).getValue();
            }
        }
    }

    private static class ResultValueMap implements RxValueMap {

        private final List<Object> taskKeys;
        private final List<RxFunction> mappers;
        private final Map<Object, Object> map;
        private final RxReturnType returnType;

        private ResultValueMap(
            List<Object> taskKeys,
            RxReturnType returnType,
            List<RxFunction> mappers
        ) {
            this.mappers = mappers;
            this.taskKeys = taskKeys;
            this.returnType = returnType;
            this.map = new ConcurrentHashMap<>(taskKeys.size());
        }

        private void put(Object key, Object value) {
            this.map.put(key, value);
        }

        @Override
        public <T> T get(Object key) {
            return (T) map.get(key);
        }

        @Override
        public <T> T get(Object key, T defaultValue) {
            return (T) map.getOrDefault(key, defaultValue);
        }

        @Override
        public <T> T firstValue() {
            return (T) map.get(taskKeys.get(0));
        }

        @Override
        public <T> T firstValueOrNull() {
            return isEmpty() ? null : firstValue();
        }

        @Override
        public <T> List<T> valueList() {
            List<T> answer = new ArrayList<>();
            for (Object taskKey : taskKeys) {
                answer.add((T) map.get(taskKey));
            }
            return answer;
        }

        @Override
        public <T> Set<T> valueSet() {
            return new HashSet<>((Collection<T>) map.values());
        }

        @Override
        public Map<String, Object> valueMap() {
            return (Map) map;
        }

        @Override
        public <K, V> Map<K, V> typedValueMap() {
            return (Map) map;
        }

        @Override
        public <T> T castGet() {
            if (returnType == RxReturnType.FIST) {
                return firstValue();
            }
            if (returnType == RxReturnType.FIST_OR_NULL) {
                return firstValueOrNull();
            }
            if (returnType == RxReturnType.LIST) {
                return (T) valueList();
            }
            if (returnType == RxReturnType.SET) {
                return (T) valueSet();
            }
            if (returnType == RxReturnType.MAP) {
                return (T) map;
            }
            Object finalResult = this;
            if (mappers != null) {
                for (RxFunction mapperItem : mappers) {
                    try {
                        finalResult = mapperItem.apply(finalResult);
                    } catch (Exception e) {
                        throw new RxException(e);
                    }
                }
            }
            return (T) finalResult;
        }

        @Override
        public int size() {
            return map.size();
        }

        @Override
        public boolean isEmpty() {
            return map.isEmpty();
        }
    }

    private static class MultipleTask {

        private final ResultValueMap resultMap;
        private final Set<Entry<Object, Object>> taskEntries;

        private MultipleTask(
            ResultValueMap resultMap,
            Set<Entry<Object, Object>> taskEntries
        ) {
            this.resultMap = resultMap;
            this.taskEntries = taskEntries;
        }
    }

    private static class InternalTask {

        private final Object taskKey;
        private final RxSupplier supplier;
        private final ResultValueMap resultMap;
        private final AtomicBoolean done;

        private InternalTask(
            ResultValueMap resultMap,
            Object taskKey,
            RxSupplier supplier
        ) {
            this.taskKey = taskKey;
            this.supplier = supplier;
            this.resultMap = resultMap;
            this.done = new AtomicBoolean();
        }
    }
}
