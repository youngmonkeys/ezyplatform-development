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
