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

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.LocaleUtils;

import java.util.Locale;

public class LocaleUtilsTest {

    @Test
    public void localeFromLanguageCodeOrDefaultTest() {
        // given
        // when
        // then
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrDefault("vi"),
            new Locale("vi")
        );
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrDefault(null),
            Locale.getDefault()
        );
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrDefault(""),
            Locale.getDefault()
        );
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrDefault(" "),
            Locale.getDefault()
        );
    }

    @Test
    public void localeFromLanguageCodeOrEnglishTest() {
        // given
        // when
        // then
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrEnglish("vi"),
            new Locale("vi")
        );
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrEnglish(null),
            Locale.ENGLISH
        );
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrEnglish(""),
            Locale.ENGLISH
        );
        Asserts.assertEquals(
            LocaleUtils.localeFromLanguageCodeOrEnglish(" "),
            Locale.ENGLISH
        );
    }
}
