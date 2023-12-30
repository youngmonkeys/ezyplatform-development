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

package org.youngmonkeys.ezyplatform.util;

import java.math.BigDecimal;
import java.math.BigInteger;

public final class Numbers {

    private Numbers() {}

    public static int roundUpOrDownToInt(double value) {
        int starsInt = (int) value;
        return value - starsInt < 0.5D
            ? starsInt
            : starsInt + 1;
    }

    public static String formatToUnitString(
        Number number,
        int decimals
    ) {
        long num = number.longValue();
        String format = "%." + decimals + "f";
        if (num >= 1_000_000_000L) {
            if (num % 1_000_000_000L == 0) {
                return num / 1_000_000_000L + "B";
            }
            return String.format(format, num / 1_000_000_000D) + 'B';
        }
        if (num >= 1_000_000L) {
            if (num % 1_000_000L == 0) {
                return num / 1_000_000L + "M";
            }
            return String.format(format, num / 1_000_000D) + 'M';
        }
        if (num >= 1_000L) {
            if (num % 1_000L == 0) {
                return num / 1_000L + "K";
            }
            return String.format(format, num / 1_000D) + 'K';
        }
        return String.valueOf(num);
    }

    public static int toIntValue(BigInteger value) {
        return value != null ? value.intValue() : 0;
    }

    public static int toIntOrZero(String value) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return 0;
        }
    }

    public static long toLongOrZero(String value) {
        try {
            return Long.parseLong(value);
        } catch (Exception e) {
            return 0L;
        }
    }

    public static String toPaddedValueLong(
        BigInteger value
    ) {
        return toPaddedValueLong(value, BigInteger.ZERO);
    }

    public static String toPaddedValueLong(
        BigInteger value,
        BigInteger defaultValue
    ) {
        return toPaddedValue(
            value,
            26,
            defaultValue
        );
    }

    public static String toPaddedValueUint256(
        BigInteger value
    ) {
        return toPaddedValueUint256(value, BigInteger.ZERO);
    }

    public static String toPaddedValueUint256(
        BigInteger value,
        BigInteger defaultValue
    ) {
        return toPaddedValue(
            value,
            76,
            defaultValue
        );
    }

    public static String toPaddedValue(
        BigInteger value,
        int leadingZeros,
        BigInteger defaultValue
    ) {
        String format = "%0" + leadingZeros + "d";
        return value == null
            ? (defaultValue == null ? null : String.format(format, defaultValue))
            : String.format(format, value);
    }

    public static BigDecimal stripTrailingZeros(
        BigDecimal value
    ) {
        return value == null
            ? null
            : value.stripTrailingZeros();
    }

    public static String toNoTrailingZerosString(
        BigDecimal value
    ) {
        return value == null
            ? null
            : stripTrailingZeros(value).toPlainString();
    }
}
