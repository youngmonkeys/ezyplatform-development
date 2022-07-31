package org.youngmonkeys.ezyplatform.test.util;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.Durations;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DurationsTest {

    @Test
    public void test() {
        // given
        List<String> input = Arrays.asList(
            "1ns",
            "10ms",
            "100s",
            "1000m",
            "10000h",
            "100000d",
            "1000000w",
            "10000000M",
            "100000000y",
            "1",
            "ms",
            "1abc"
        );

        // when
        List<Long> actual = input
            .stream()
            .map(Durations::durationStringToMillis)
            .collect(Collectors.toList());

        // then
        List<Long> expectation = Arrays.asList(
            0L,
            10L,
            100 * 1000L,
            1000 * 60 * 1000L,
            10000 * 60 * 60 * 1000L,
            100000L * 24 * 60 * 60 * 1000L,
            1000000L * 7 * 24 * 60 * 60 * 1000L,
            10000000L * 30 * 24 * 60 * 60 * 1000L,
            100000000L * 365 * 24 * 60 * 60 * 1000L,
            -1L,
            -1L,
            -1L
        );
        Asserts.assertEquals(actual, expectation, false);
    }
}
