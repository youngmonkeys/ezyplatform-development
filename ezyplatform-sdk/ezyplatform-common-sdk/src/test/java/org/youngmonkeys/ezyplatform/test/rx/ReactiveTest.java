package org.youngmonkeys.ezyplatform.test.rx;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.base.BaseTest;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.rx.Reactive;
import org.youngmonkeys.ezyplatform.rx.RxValueMap;

import java.util.Arrays;

public class ReactiveTest extends BaseTest {

    @Test
    public void test() {
        // given
        Reactive.Multiple multiple = Reactive.multiple(
            Reactive.ReturnType.MAP
        )
            .register("hello1", () -> "world1")
            .registerRx(
                "multiple1",
                Reactive.multiple(Reactive.ReturnType.MAP)
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
                Reactive.multiple(Reactive.ReturnType.MAP)
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
        Object actual = multiple.blockingCastGet();

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
}
