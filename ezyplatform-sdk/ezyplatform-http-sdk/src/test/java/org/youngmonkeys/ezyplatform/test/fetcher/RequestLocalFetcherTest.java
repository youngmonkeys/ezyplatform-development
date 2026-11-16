/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.fetcher;

import com.tvd12.test.assertion.Asserts;
import org.eclipse.jetty.http.BadMessageException;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.fetcher.RequestLocalFetcher;
import org.youngmonkeys.ezyplatform.service.LanguageService;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static org.mockito.Mockito.*;

public class RequestLocalFetcherTest {

    @Test
    public void getLocalFromRequestLangTest() {
        // given
        LanguageService languageService = mock(LanguageService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("lang")).thenReturn("vi");

        RequestLocalFetcher instance = new RequestLocalFetcher(languageService);

        // when
        Locale actual = instance.getLocal(request);

        // then
        Asserts.assertEquals(actual, new Locale("vi"));
        verify(languageService, never()).getDefaultLanguageCode();
    }

    @Test
    public void getLocalFromHeaderLangTest() {
        // given
        LanguageService languageService = mock(LanguageService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("lang")).thenReturn("ar");

        RequestLocalFetcher instance = new RequestLocalFetcher(languageService);

        // when
        Locale actual = instance.getLocal(request);

        // then
        Asserts.assertEquals(actual, new Locale("ar"));
        verify(languageService, never()).getDefaultLanguageCode();
    }

    @Test
    public void getLocalFallbackToDefaultLanguageTest() {
        // given
        LanguageService languageService = mock(LanguageService.class);
        when(languageService.getDefaultLanguageCode()).thenReturn("en");
        HttpServletRequest request = mock(HttpServletRequest.class);

        RequestLocalFetcher instance = new RequestLocalFetcher(languageService);

        // when
        Locale actual = instance.getLocal(request);

        // then
        Asserts.assertEquals(actual, new Locale("en"));
        verify(languageService, times(1)).getDefaultLanguageCode();
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void getLocalBadRequestLangFallbackToDefaultLanguageTest() {
        // given
        LanguageService languageService = mock(LanguageService.class);
        when(languageService.getDefaultLanguageCode()).thenReturn("en");
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("lang")).thenThrow(
            new BadMessageException("bad")
        );

        RequestLocalFetcher instance = new RequestLocalFetcher(languageService);

        // when
        Locale actual = instance.getLocal(request);

        // then
        Asserts.assertEquals(actual, new Locale("en"));
        verify(languageService, times(1)).getDefaultLanguageCode();
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void getLocalDefaultLanguageNullTest() {
        // given
        LanguageService languageService = mock(LanguageService.class);
        HttpServletRequest request = mock(HttpServletRequest.class);

        RequestLocalFetcher instance = new RequestLocalFetcher(languageService);

        // when
        Locale actual = instance.getLocal(request);

        // then
        Asserts.assertEquals(actual, Locale.ROOT);
        verify(languageService, times(1)).getDefaultLanguageCode();
        verifyNoMoreInteractions(languageService);
    }

    @Test
    public void getLocalDefaultLanguageBlankTest() {
        // given
        LanguageService languageService = mock(LanguageService.class);
        when(languageService.getDefaultLanguageCode()).thenReturn(" ");
        HttpServletRequest request = mock(HttpServletRequest.class);

        RequestLocalFetcher instance = new RequestLocalFetcher(languageService);

        // when
        Locale actual = instance.getLocal(request);

        // then
        Asserts.assertEquals(actual, Locale.ROOT);
        verify(languageService, times(1)).getDefaultLanguageCode();
        verifyNoMoreInteractions(languageService);
    }
}
