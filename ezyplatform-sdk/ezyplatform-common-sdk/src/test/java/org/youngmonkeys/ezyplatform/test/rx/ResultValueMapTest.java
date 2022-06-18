package org.youngmonkeys.ezyplatform.test.rx;

import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.rx.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ResultValueMapTest {

    @Test
    public void getOrDefaultReturnValueTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        RxValueMap actual = Reactive.single(values)
            .returnType(RxReturnType.DEFAULT)
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual.get(values.get(0), "world"),
            String.valueOf(values.get(0))
        );
    }

    @Test
    public void getOrDefaultReturnDefaultValueTest() {
        // given
        List<Integer> values = RandomUtil.randomList(10, int.class);

        // when
        RxValueMap actual = Reactive.single(values)
            .returnType(RxReturnType.DEFAULT)
            .mapItem(String::valueOf)
            .blockingGet();

        // then
        Asserts.assertEquals(
            actual.get("hello", "world"),
            "world"
        );
    }

    @Test
    public void valueMapTest() {
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
        Asserts.assertEquals(actual.valueMap(), map, false);
        Asserts.assertEquals(actual.size(), values.size());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void castGetFailedDueToMapper() throws Exception {
        // given
        RxFunction<RxValueMap, String> mapper = mock(RxFunction.class);
        RuntimeException exception = new RuntimeException("test");
        when(mapper.apply(any(RxValueMap.class))).thenThrow(exception);

        // when
        Throwable e = Asserts.assertThrows(() ->
            Reactive.multiple()
                .register("hello", () -> "world")
                .mapBegin(it -> { throw exception; })
                .blockingGet()
        );

        // then
        Asserts.assertEqualsType(e, RxException.class);
        Asserts.assertEquals(
            ((RxException) e).getExceptions(),
            Collections.singletonList(exception)
        );
    }
}
