package org.youngmonkeys.ezyplatform.test.rx;

import com.tvd12.ezyfox.concurrent.EzyExecutors;
import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import com.tvd12.test.reflect.FieldUtil;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.rx.*;

import java.util.*;
import java.util.concurrent.ExecutorService;
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
        @SuppressWarnings({"rawtypes", "unchecked"})
        RxValueMap expectation = new RxValueMap(
            (List) values,
            RxReturnType.DEFAULT,
            null
        );
        Map<Integer, String> map = values
            .stream()
            .collect(Collectors.toMap(it -> it, String::valueOf));
        FieldUtil.setFieldValue(expectation, "map", map);
        Asserts.assertEquals(
            actual,
            expectation,
            false
        );
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
