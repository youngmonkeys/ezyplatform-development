/*
 * Copyright 2024 youngmonkeys.org
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

import javax.servlet.http.HttpServletRequest;

import static org.mockito.Mockito.*;
import static org.youngmonkeys.ezyplatform.util.HttpRequests.addLanguageToUri;

public class HttpRequestsTest {

    @Test
    public void addLanguageToUriNormalCase() {
        // given
        String uri = "/hello";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("lang")).thenReturn("vi");

        // when
        String actual = addLanguageToUri(
            request,
            uri
        );

        // then
        Asserts.assertEquals(actual, "/hello?lang=vi");
        verify(request, times(1)).getParameter("lang");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void addLanguageToUriHasParamCase() {
        // given
        String uri = "/hello?value=world";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getParameter("lang")).thenReturn("vi");

        // when
        String actual = addLanguageToUri(
            request,
            uri
        );

        // then
        Asserts.assertEquals(actual, "/hello?value=world&lang=vi");
        verify(request, times(1)).getParameter("lang");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void addLanguageToUriNoLangCase() {
        // given
        String uri = "/hello?value=world";
        HttpServletRequest request = mock(HttpServletRequest.class);

        // when
        String actual = addLanguageToUri(
            request,
            uri
        );

        // then
        Asserts.assertEquals(actual, "/hello?value=world");
        verify(request, times(1)).getAttribute("lang");
        verify(request, times(1)).getHeader("lang");
        verify(request, times(1)).getParameter("lang");
        verify(request, times(1)).getCookies();
        verifyNoMoreInteractions(request);
    }
}
