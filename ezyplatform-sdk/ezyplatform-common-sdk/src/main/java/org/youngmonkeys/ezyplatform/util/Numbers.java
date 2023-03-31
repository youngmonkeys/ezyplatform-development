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
}
