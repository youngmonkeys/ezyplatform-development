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
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            return value != null ? Integer.parseInt(value) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    public static long toLongOrZero(String value) {
        try {
            return value != null ? Long.parseLong(value) : 0L;
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

    public static String toRandomText(long number, int minLength) {
        String suffix = String.valueOf(number);
        if (suffix.length() >= minLength - 1) {
            return "0" + suffix;
        }
        int randomLength = minLength - suffix.length() - 1;
        if (randomLength > 9) {
            randomLength = 9;
        }
        StringBuilder randomNumber = new StringBuilder();
        for (int i = 0; i < randomLength; ++i) {
            randomNumber.append(
                ThreadLocalRandom.current().nextInt(1, 9)
            );
        }
        int zeroLength = minLength - randomLength - suffix.length() - 1;
        if (zeroLength > 0) {
            for (int i = 0; i < zeroLength; ++i) {
                randomNumber.append("0");
            }
        }
        return String.valueOf(randomLength) + randomNumber + suffix;
    }

    public static long fromRandomText(String randomText) {
        int randomLength = Integer.parseInt(
            randomText.substring(0, 1)
        );
        if (randomLength == 0) {
            return Long.parseLong(randomText);
        }
        return Long.parseLong(
            randomText.substring(randomLength + 1)
        );
    }

    public static boolean isFloatingPointText(String input) {
        String regex = "^[+-]?\\d+\\.\\d+$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }
}
