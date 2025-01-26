/*
 * Copyright 2025 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.util;

import com.tvd12.ezyfox.io.EzyDates;

import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.DEFAULT_DATETIME_FORMAT;

public class DateTimeTest {

    public static void main(String[] args) {
        String value = EzyDates.format(
            LocalDateTime.now(),
            DEFAULT_DATETIME_FORMAT
        );
        System.out.println(value);
    }
}
