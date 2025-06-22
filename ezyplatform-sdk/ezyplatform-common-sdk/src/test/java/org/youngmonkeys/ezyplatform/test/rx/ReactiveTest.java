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

package org.youngmonkeys.ezyplatform.test.rx;

import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.ezyfox.util.EzyThreads;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.rx.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.tvd12.ezyfox.io.EzyLists.newArrayList;
import static com.tvd12.ezyfox.io.EzySets.newHashSet;
import static org.mockito.Mockito.*;

public class ReactiveTest extends BaseTest {

    @Test
    public void test() {
        // given
        Reactive.Multiple multiple = Reactive.multiple(
            RxReturnType.MAP
        )
            .register("hello1", () -> "world1")
            .registerRx(
                "multiple1",
                Reactive.multiple(RxReturnType.MAP)
                    .register("foo1", () -> "bar1")
                    .registerRx(
                        "single1",
                        Reactive.single(Arrays.asList(1, 2, 3))
                            .mapItem(Object::toString)
                    )
            )
            .register("hello2", () -> "world2")
            .registerRx(
                "multiple2",
                Reactive.multiple(RxReturnType.MAP)
                    .register("foo2", () -> "bar2")
                    .registerRx(
                        "single2",
                        Reactive.single(Arrays.asList(4, 5, 6))
                            .mapItem(Object::toString)
                    )
            )
            .register("hello3", () -> "world3")
            .registerRx(
                "multiple3",
                Reactive.multiple()
                    .register("foo3", () -> "bar3")
                    .registerRx(
                        "single3",
                        Reactive.single(Arrays.asList(7, 8, 9))
                            .mapItem(Object::toString)
                    )
                    .mapBegin(RxValueMap::typedValueMap)
            );

        // when
        Object actual = multiple.blockingGet();

        // then
        Asserts.assertEquals(
            actual,
            EzyMapBuilder.mapBuilder()
                .put("hello1", "world1")
                .put(
                    "multiple1",
                    EzyMapBuilder.mapBuilder()
                        .put("foo1", "bar1")
                        .put("single1", Arrays.asList("1", "2", "3"))
                        .build()
                )
                .put("hello2", "world2")
                .put(
                    "multiple2",
                    EzyMapBuilder.mapBuilder()
                        .put("foo2", "bar2")
                        .put("single2", Arrays.asList("4", "5", "6"))
                        .build()
                )
                .put("hello3", "world3")
                .put(
                    "multiple3",
                    EzyMapBuilder.mapBuilder()
                        .put("foo3", "bar3")
                        .put("single3", Arrays.asList("7", "8", "9"))
                        .build()
                )
                .build(),
            false
        );
    }

    @Test
    public void singleValueTest() {
        // given
        int value = RandomUtil.randomSmallInt();

        // when
        String actual = Reactive.single(value)
            .mapItem(it -> it + 1)
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(actual, String.valueOf(value + 1));
    }

    @Test
    public void blockingExecuteTest() throws Exception {
        // given
        int value = RandomUtil.randomSmallInt();

        @SuppressWarnings("unchecked")
        RxConsumer<Integer> consumer = mock(RxConsumer.class);

        // when
        Reactive.single(value)
            .operateItem(consumer)
            .blockingExecute();

        // then
        verify(consumer, times(1)).accept(value);
    }

    @Test
    public void blockingGetListTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        List<String> actual = Reactive.single(values)
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual,
            newArrayList(values, String::valueOf)
        );
    }

    @Test
    public void blockingGetFirstTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        String actual = Reactive.single(values)
            .returnTypeFirst()
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual,
            String.valueOf(values.get(0))
        );
    }

    @Test
    public void blockingGetFirstOrNullReturnNotNullTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        String actual = Reactive.single(values)
            .returnTypeFirstOrNull()
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual,
            String.valueOf(values.get(0))
        );
    }

    @Test
    public void blockingGetFirstOrNullReturnNullTest() {
        // given
        List<Integer> values = Collections.emptyList();

        // when
        String actual = Reactive.single(values)
            .returnTypeFirstOrNull()
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertNull(actual);
    }

    @Test
    public void blockingGetList2Test() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        List<String> actual = Reactive.single(values)
            .returnTypeList()
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual,
            newArrayList(values, String::valueOf)
        );
    }

    @Test
    public void blockingGetMapTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        Map<Integer, String> actual = Reactive.single(values)
            .returnTypeMap()
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual,
            values
                .stream()
                .collect(Collectors.toMap(it -> it, String::valueOf)),
            false
        );
    }

    @Test
    public void blockingGetDefaultTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        RxValueMap actual = Reactive.single(values)
            .returnType(RxReturnType.DEFAULT)
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Map<Integer, String> map = values
            .stream()
            .collect(Collectors.toMap(it -> it, String::valueOf));
        Asserts.assertEquals(actual.typedValueMap(), map, false);
    }

    @Test
    public void blockingGetDefaultLambdaTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        Object actual = Reactive.multiple()
            .registersRx(values, Reactive::single)
            .blockingGet(RxValueMap::valueMap);

        // then
        Map<Integer, Integer> map = values
            .stream()
            .collect(Collectors.toMap(it -> it, it -> it));
        Asserts.assertEquals(actual, map, false);
    }

    @Test
    public void blockingGetSetTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        Set<String> actual = Reactive.single(values)
            .returnTypeSet()
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual,
            newHashSet(values, String::valueOf)
        );
    }

    @Test
    public void mapItemFailedTest() {
        // given
        Integer value = RandomUtil.randomSmallInt();

        // when
        Throwable e = Asserts.assertThrows(() ->
            Reactive.single(value)
                .mapItemRx(Reactive::single)
                .mapItem(String::valueOf)
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @Test
    public void mapItemTest() {
        // given
        Integer value = RandomUtil.randomSmallInt();

        // when
        String actual = Reactive.single(value)
            .mapItemRx(it ->
                Reactive.single(it)
                    .mapItem(String::valueOf)
            ).blockingGet();

        // then
        Asserts.assertEquals(actual, String.valueOf(value));
    }

    @Test
    public void mapItemRxFailedDueToHasItemMapperTest() {
        // given
        Integer value = RandomUtil.randomSmallInt();

        // when
        Throwable e = Asserts.assertThrows(() ->
            Reactive.single(value)
                .mapItem(String::valueOf)
                .mapItemRx(Reactive::single)
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @Test
    public void mapItemRxFailedDueTo2TimesTest() {
        // given
        Integer value = RandomUtil.randomSmallInt();

        // when
        Throwable e = Asserts.assertThrows(() ->
            Reactive.single(value)
                .mapItemRx(Reactive::single)
                .mapItemRx(Reactive::single)
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @Test
    public void registerOperationTest() throws Exception {
        // given
        String name = RandomUtil.randomShortAlphabetString();
        RxRunnable operation = mock(RxRunnable.class);

        // when
        Reactive.multiple()
            .registerOperation(name, operation)
            .blockingExecute();

        // then
        verify(operation, times(1)).run();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void registerConsumersTest() throws Exception {
        // given
        String name1 = RandomUtil.randomShortAlphabetString();
        String name2 = RandomUtil.randomShortAlphabetString();

        RxConsumer<String> itemConsumer = mock(RxConsumer.class);

        // when
        Reactive.multiple()
            .registerConsumers(
                Arrays.asList(name1, name2),
                itemConsumer
            )
            .blockingExecute();

        // then
        verify(itemConsumer, times(1)).accept(name1);
        verify(itemConsumer, times(1)).accept(name2);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void multipleMap2TimesTest() throws Exception {
        // given
        int value = RandomUtil.randomSmallInt();
        RxFunction<RxValueMap, Integer> mapper1 = mock(RxFunction.class);
        when(mapper1.apply(any(RxValueMap.class))).thenReturn(value + 1);

        RxFunction<Integer, String> mapper2 = mock(RxFunction.class);
        when(mapper2.apply(value + 1)).thenReturn(
            String.valueOf(value + 1)
        );

        // when
        String actual = Reactive.single(value)
            .returnType(RxReturnType.DEFAULT)
            .toMultiple()
            .map(mapper1)
            .map(mapper2)
            .blockingGet();

        // then
        Asserts.assertEquals(actual, String.valueOf(value + 1));

        verify(mapper1, times(1)).apply(any(RxValueMap.class));
        verify(mapper2, times(1)).apply(value + 1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void multipleMapFailedTest() {
        // given
        int value = RandomUtil.randomSmallInt();
        RxFunction<RxValueMap, Integer> mapper = mock(RxFunction.class);

        // when
        Throwable e = Asserts.assertThrows(() ->
            Reactive.single(value)
                .returnType(RxReturnType.SET)
                .toMultiple()
                .map(mapper)
        );

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void blockingConsumeTest() throws Exception {
        // given
        int value = RandomUtil.randomSmallInt();
        RxConsumer<RxValueMap> consumer = mock(RxConsumer.class);

        // when
        Reactive.single(value)
            .returnType(RxReturnType.DEFAULT)
            .toMultiple()
            .blockingConsume(consumer);

        // then
        verify(consumer, times(1)).accept(any(RxValueMap.class));
    }

    @Test
    public void blockingGetFailedTest() {
        // given
        int value = RandomUtil.randomSmallInt();
        RuntimeException exception = new RuntimeException("just test");

        // when
        Throwable e = Asserts.assertThrows(() ->
            Reactive.single(value)
                .mapItem(it -> { throw exception; })
                .blockingGet()
        );

        // then
        Asserts.assertEqualsType(e, RxException.class);
        Asserts.assertEquals(
            ((RxException) e).getExceptions(),
            Collections.singletonList(exception),
            false
        );
    }

    @Test
    public void blockingGetTimeoutTest() {
        // given
        // when
        Throwable e = Asserts.assertThrows(() ->
            Reactive.multiple()
                .register("hello", () -> "world")
                .register("foo", () -> {
                    EzyThreads.sleep(200);
                    return "bar";
                })
                .register("1", () -> "2")
                .blockingGet(10, TimeUnit.MILLISECONDS)
        );

        // then
        Asserts.assertEqualsType(e, RxException.class);
        Asserts.assertEqualsType(
            ((RxException) e).getExceptions().get(0),
            TimeoutException.class
        );
    }

    @Test
    public void blockingGetInterruptTest() {
        // given
        // when
        AtomicReference<Throwable> ref = new AtomicReference<>();
        Thread newThread = new Thread(() -> {
            try {
                Reactive.multiple()
                    .register("hello", () -> "world")
                    .register("foo", () -> {
                        EzyThreads.sleep(200);
                        return "bar";
                    })
                    .register("1", () -> "2")
                    .blockingGet();
            } catch (Throwable e) {
                ref.set(e);
            }
        });
        newThread.start();
        EzyThreads.sleep(90);
        newThread.interrupt();
        EzyThreads.sleep(100);

        // then
        Throwable e = ref.get();
        Asserts.assertEqualsType(e, RxException.class);
        Asserts.assertEqualsType(
            ((RxException) e).getExceptions().get(0),
            InterruptedException.class
        );
    }

    @Test
    public void useCallingThreadTest() throws InterruptedException {
        // given
        Reactive.Multiple multiple = Reactive.multiple();
        int sum = 0;
        for (int i = 0 ; i < 100; ++i) {
            int finalI = i;
            multiple.register(
                String.valueOf(i),
                () -> {
                    Thread.sleep(50);
                    return finalI;
                }
            );
            sum += i;
        }

        // when
        Map<String, Integer> map = multiple
            .blockingGet(RxValueMap::typedValueMap);

        // then
        Asserts.assertEquals(map.size(), 100);
        Asserts.assertEquals(
            map.values().stream().reduce(0, Integer::sum),
            sum
        );
        AtomicInteger busyThreads = FieldUtil.getStaticFieldValue(
            Reactive.class,
            "NUMBER_OF_BUSY_THREADS"
        );
        Thread.sleep(100);
        Asserts.assertEquals(busyThreads.get(), 0);
    }

    @Test
    public void useCallingThreadExceptionsTest() {
        // given
        Reactive.Multiple multiple = Reactive.multiple();
        for (int i = 0 ; i < 100; ++i) {
            multiple.register(
                String.valueOf(i),
                () -> {
                    Thread.sleep(50);
                    throw new IllegalStateException("test");
                }
            );
        }

        // when
        Throwable throwable = Asserts.assertThrows(multiple::blockingGet);

        // then
        Asserts.assertEqualsType(
            throwable,
            RxException.class
        );
    }

    @Test
    public void registerSimpleTest() throws InterruptedException {
        // given
        Reactive.Multiple multiple = Reactive.multiple();
        int sum = 0;
        for (int i = 0 ; i < 100; ++i) {
            int finalI = i;
            multiple.register(
                () -> {
                    Thread.sleep(50);
                    return finalI;
                }
            );
            sum += i;
        }

        // when
        List<Integer> valueList = multiple
            .blockingGet(RxValueMap::valueList);

        // then
        Asserts.assertEquals(valueList.size(), 100);
        Asserts.assertEquals(
            valueList.stream().reduce(0, Integer::sum),
            sum
        );
        AtomicInteger busyThreads = FieldUtil.getStaticFieldValue(
            Reactive.class,
            "NUMBER_OF_BUSY_THREADS"
        );
        Thread.sleep(50);
        Asserts.assertEquals(busyThreads.get(), 0);
    }

    @Test
    public void registerOperationExceptionsTest() {
        // given
        Reactive.Multiple multiple = Reactive.multiple();
        for (int i = 0 ; i < 100; ++i) {
            multiple.registerOperation(
                () -> {
                    Thread.sleep(50);
                    throw new IllegalStateException("test");
                }
            );
        }

        // when
        Throwable throwable = Asserts.assertThrows(multiple::blockingGet);

        // then
        Asserts.assertEqualsType(
            throwable,
            RxException.class
        );
    }

    @Test
    public void registerRxNullTest() {
        // given
        Reactive.Multiple multiple = Reactive.multiple()
            .registerRx("test", null);

        // when
        // then
        multiple.blockingExecute();
    }

    @Test
    public void destroyTest() {
        // given
        ExecutorService executorService = EzyExecutors
            .newSingleThreadExecutor("reactive");

        // when
        Reactive.destroy();

        // then
        Reactive.setExecutorService(executorService);
    }
}
