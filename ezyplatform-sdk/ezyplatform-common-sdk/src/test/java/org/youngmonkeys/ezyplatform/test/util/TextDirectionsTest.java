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

package org.youngmonkeys.ezyplatform.test.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.model.WebLanguageModel;
import org.youngmonkeys.ezyplatform.util.TextDirections;

public class TextDirectionsTest {

    @Test
    public void rtlLanguagesReadRightToLeftTest() {
        String[] codes = new String[] {
            "ar", "he", "iw", "fa", "ur", "ps", "sd", "ug", "yi", "dv", "ku", "ckb", "arc"
        };
        for (String code : codes) {
            Asserts.assertTrue(TextDirections.isRtlLanguage(code));
            Asserts.assertEquals(
                TextDirections.getTextDirection(code),
                TextDirections.RTL
            );
        }
    }

    @Test
    public void rtlLanguagesWithRegionReadRightToLeftTest() {
        Asserts.assertEquals(TextDirections.getTextDirection("ar-SA"), TextDirections.RTL);
        Asserts.assertEquals(TextDirections.getTextDirection("fa_IR"), TextDirections.RTL);
        Asserts.assertEquals(TextDirections.getTextDirection(" HE "), TextDirections.RTL);
    }

    @Test
    public void ltrLanguagesReadLeftToRightTest() {
        String[] codes = new String[] {
            "en", "vi", "ja", "ko", "zh", "zh-TW", "en_US", "fr", "arm", "hea"
        };
        for (String code : codes) {
            Asserts.assertFalse(TextDirections.isRtlLanguage(code));
            Asserts.assertEquals(
                TextDirections.getTextDirection(code),
                TextDirections.LTR
            );
        }
    }

    @Test
    public void missingLanguageReadsLeftToRightTest() {
        Asserts.assertEquals(TextDirections.getTextDirection(null), TextDirections.LTR);
        Asserts.assertEquals(TextDirections.getTextDirection(""), TextDirections.LTR);
        Asserts.assertEquals(TextDirections.getTextDirection("-ar"), TextDirections.LTR);
    }

    @Test
    public void configuredDirectionOverridesLanguageTest() {
        Asserts.assertEquals(TextDirections.getTextDirection("ar", "ltr"), TextDirections.LTR);
        Asserts.assertEquals(TextDirections.getTextDirection("en", "RTL"), TextDirections.RTL);
    }

    @Test
    public void blankOrInvalidConfiguredDirectionFallsBackToLanguageTest() {
        Asserts.assertEquals(TextDirections.getTextDirection("ar", null), TextDirections.RTL);
        Asserts.assertEquals(TextDirections.getTextDirection("ar", " "), TextDirections.RTL);
        Asserts.assertEquals(TextDirections.getTextDirection("ar", "up"), TextDirections.RTL);
        Asserts.assertEquals(TextDirections.getTextDirection("en", "up"), TextDirections.LTR);
    }

    @Test
    public void onlyLtrAndRtlAreValidDirectionsTest() {
        Asserts.assertTrue(TextDirections.isValidTextDirection("ltr"));
        Asserts.assertTrue(TextDirections.isValidTextDirection("rtl"));
        Asserts.assertFalse(TextDirections.isValidTextDirection("auto"));
        Asserts.assertFalse(TextDirections.isValidTextDirection("RTL"));
        Asserts.assertFalse(TextDirections.isValidTextDirection(null));
    }

    @Test
    public void webLanguageResolvesTextDirectionTest() {
        WebLanguageModel autoArabic = WebLanguageModel.builder()
            .code("ar")
            .build();
        WebLanguageModel forcedLtrArabic = WebLanguageModel.builder()
            .code("ar")
            .direction("ltr")
            .build();
        WebLanguageModel forcedRtlCustom = WebLanguageModel.builder()
            .code("xx")
            .direction("rtl")
            .build();
        Asserts.assertEquals(autoArabic.resolveTextDirection(), TextDirections.RTL);
        Asserts.assertEquals(forcedLtrArabic.resolveTextDirection(), TextDirections.LTR);
        Asserts.assertEquals(forcedRtlCustom.resolveTextDirection(), TextDirections.RTL);
    }

    @Test
    public void autoDirectionIsNotSavedToSettingTest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        WebLanguageModel autoLanguage = WebLanguageModel.builder()
            .code("ar")
            .name("Arabic")
            .build();
        WebLanguageModel rtlLanguage = WebLanguageModel.builder()
            .code("xx")
            .name("Custom")
            .direction("rtl")
            .build();

        String autoJson = objectMapper.writeValueAsString(autoLanguage);
        String rtlJson = objectMapper.writeValueAsString(rtlLanguage);

        Asserts.assertFalse(autoJson.contains("direction"));
        Asserts.assertFalse(autoJson.contains("textDirection"));
        Asserts.assertTrue(rtlJson.contains("\"direction\":\"rtl\""));
        WebLanguageModel savedRtlLanguage = objectMapper.readValue(
            rtlJson,
            WebLanguageModel.Mutable.class
        );
        Asserts.assertEquals(savedRtlLanguage.getDirection(), "rtl");
        Asserts.assertEquals(savedRtlLanguage.resolveTextDirection(), TextDirections.RTL);
    }
}
