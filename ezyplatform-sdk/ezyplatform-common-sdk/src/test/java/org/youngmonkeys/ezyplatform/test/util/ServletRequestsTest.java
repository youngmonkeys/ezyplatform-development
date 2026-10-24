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

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.manager.EnvironmentManager;
import org.youngmonkeys.ezyplatform.util.ClientIpResolver;
import org.youngmonkeys.ezyplatform.util.ServletRequests;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;

import static org.mockito.Mockito.*;

public class ServletRequestsTest {

    @BeforeMethod
    public void setUp() throws Exception {
        resetStaticFields();
    }

    @AfterMethod
    public void tearDown() throws Exception {
        resetStaticFields();
    }

    @Test
    public void getClientIpByResolverTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        ClientIpResolver resolver = mock(ClientIpResolver.class);
        when(resolver.resolve(request, false)).thenReturn("1.1.1.1");
        ServletRequests.setClientIpResolver(resolver);

        // when
        String actual = ServletRequests.getClientIp(request);

        // then
        Asserts.assertEquals(actual, "1.1.1.1");
        verify(resolver, times(1)).resolve(request, false);
        verifyNoMoreInteractions(resolver);
        verifyZeroInteractions(request);
    }

    @Test
    public void getClientIpByResolverRequestDirectlyTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        ClientIpResolver resolver = mock(ClientIpResolver.class);
        when(resolver.resolve(request, true)).thenReturn("1.1.1.1");
        ServletRequests.setClientIpResolver(resolver);

        EnvironmentManager environmentManager = mock(EnvironmentManager.class);
        when(environmentManager.isWithoutProxy()).thenReturn(true);
        ServletRequests.setEnvironmentManager(environmentManager);

        // when
        String actual = ServletRequests.getClientIp(request);

        // then
        Asserts.assertEquals(actual, "1.1.1.1");
        verify(resolver, times(1)).resolve(request, true);
        verify(environmentManager, times(1)).isWithoutProxy();
        verifyNoMoreInteractions(resolver, environmentManager);
        verifyZeroInteractions(request);
    }

    @Test
    public void getClientIpResolverReturnUnknownFallbackToHeaderTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("2.2.2.2");

        ClientIpResolver resolver = mock(ClientIpResolver.class);
        when(resolver.resolve(request, false)).thenReturn("Unknown");
        ServletRequests.setClientIpResolver(resolver);

        // when
        String actual = ServletRequests.getClientIp(request);

        // then
        Asserts.assertEquals(actual, "2.2.2.2");
        verify(resolver, times(1)).resolve(request, false);
        verify(request, times(1)).getHeader("X-Forwarded-For");
        verifyNoMoreInteractions(resolver, request);
    }

    @Test
    public void getClientIpResolverReturnNullFallbackToRemoteAddrTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("3.3.3.3");

        ClientIpResolver resolver = mock(ClientIpResolver.class);
        ServletRequests.setClientIpResolver(resolver);

        // when
        String actual = ServletRequests.getClientIp(request, true);

        // then
        Asserts.assertEquals(actual, "3.3.3.3");
        verify(resolver, times(1)).resolve(request, true);
        verify(request, times(1)).getRemoteAddr();
        verifyNoMoreInteractions(resolver, request);
    }

    @Test
    public void getClientIpWithoutProxyIgnoreHeadersTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("3.3.3.3");
        when(request.getHeader("X-Forwarded-For")).thenReturn("2.2.2.2");

        EnvironmentManager environmentManager = mock(EnvironmentManager.class);
        when(environmentManager.isWithoutProxy()).thenReturn(true);
        ServletRequests.setEnvironmentManager(environmentManager);

        // when
        String actual = ServletRequests.getClientIp(request);

        // then
        Asserts.assertEquals(actual, "3.3.3.3");
        verify(environmentManager, times(1)).isWithoutProxy();
        verify(request, times(1)).getRemoteAddr();
        verifyNoMoreInteractions(environmentManager, request);
    }

    @Test
    public void getClientIpBehindProxyUseHeaderTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("3.3.3.3");
        when(request.getHeader("X-Forwarded-For")).thenReturn("2.2.2.2");

        EnvironmentManager environmentManager = mock(EnvironmentManager.class);
        when(environmentManager.isWithoutProxy()).thenReturn(false);
        ServletRequests.setEnvironmentManager(environmentManager);

        // when
        String actual = ServletRequests.getClientIp(request);

        // then
        Asserts.assertEquals(actual, "2.2.2.2");
        verify(environmentManager, times(1)).isWithoutProxy();
        verify(request, times(1)).getHeader("X-Forwarded-For");
        verify(request, never()).getRemoteAddr();
        verifyNoMoreInteractions(environmentManager, request);
    }

    @Test
    public void getClientIpWithoutEnvironmentManagerUseHeaderTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("3.3.3.3");
        when(request.getHeader("X-Forwarded-For")).thenReturn("2.2.2.2");

        // when
        String actual = ServletRequests.getClientIp(request);

        // then
        Asserts.assertEquals(actual, "2.2.2.2");
        verify(request, times(1)).getHeader("X-Forwarded-For");
        verify(request, never()).getRemoteAddr();
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getClientIpHeaderPriorityTest() {
        String[] headers = new String[] {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_CLIENT_IP",
            "HTTP_X_FORWARDED_FOR",
            "X-Real-IP"
        };
        for (int i = 0; i < headers.length; ++i) {
            // given
            HttpServletRequest request = mock(HttpServletRequest.class);
            for (int k = 0; k < i; ++k) {
                when(request.getHeader(headers[k]))
                    .thenReturn(k % 2 == 0 ? "" : "unknown");
            }
            for (int k = i; k < headers.length; ++k) {
                when(request.getHeader(headers[k])).thenReturn("10.0.0." + k);
            }

            // when
            String actual = ServletRequests.getClientIp(request, false);

            // then
            Asserts.assertEquals(actual, "10.0.0." + i);
            for (int k = 0; k <= i; ++k) {
                verify(request, times(1)).getHeader(headers[k]);
            }
            verifyNoMoreInteractions(request);
        }
    }

    @Test
    public void getClientIpAllHeadersUnknownTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(request.getHeader("X-Real-IP")).thenReturn("");

        // when
        String actual = ServletRequests.getClientIp(request, false);

        // then
        Asserts.assertEquals(actual, "unknown");
        verify(request, times(1)).getHeader("X-Forwarded-For");
        verify(request, times(1)).getHeader("Proxy-Client-IP");
        verify(request, times(1)).getHeader("WL-Proxy-Client-IP");
        verify(request, times(1)).getHeader("HTTP_CLIENT_IP");
        verify(request, times(1)).getHeader("HTTP_X_FORWARDED_FOR");
        verify(request, times(1)).getHeader("X-Real-IP");
        verify(request, never()).getRemoteAddr();
        verifyNoMoreInteractions(request);
    }

    @Test
    public void setClientIpResolverOnlyOnceTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        ClientIpResolver first = mock(ClientIpResolver.class);
        when(first.resolve(request, false)).thenReturn("1.1.1.1");
        ClientIpResolver second = mock(ClientIpResolver.class);
        when(second.resolve(request, false)).thenReturn("2.2.2.2");

        // when
        ServletRequests.setClientIpResolver(first);
        ServletRequests.setClientIpResolver(second);
        String actual = ServletRequests.getClientIp(request, false);

        // then
        Asserts.assertEquals(actual, "1.1.1.1");
        verify(first, times(1)).resolve(request, false);
        verifyNoMoreInteractions(first);
        verifyZeroInteractions(second);
    }

    @Test
    public void setEnvironmentManagerOnlyOnceTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRemoteAddr()).thenReturn("3.3.3.3");

        EnvironmentManager first = mock(EnvironmentManager.class);
        when(first.isWithoutProxy()).thenReturn(true);
        EnvironmentManager second = mock(EnvironmentManager.class);
        when(second.isWithoutProxy()).thenReturn(false);

        // when
        ServletRequests.setEnvironmentManager(first);
        ServletRequests.setEnvironmentManager(second);
        String actual = ServletRequests.getClientIp(request);

        // then
        Asserts.assertEquals(actual, "3.3.3.3");
        verify(first, times(1)).isWithoutProxy();
        verifyNoMoreInteractions(first);
        verifyZeroInteractions(second);
    }

    @Test
    public void isUnknownIpTest() {
        Asserts.assertTrue(ServletRequests.isUnknownIp(null));
        Asserts.assertTrue(ServletRequests.isUnknownIp(""));
        Asserts.assertTrue(ServletRequests.isUnknownIp("unknown"));
        Asserts.assertTrue(ServletRequests.isUnknownIp("UNKNOWN"));
        Asserts.assertTrue(ServletRequests.isUnknownIp("Unknown"));
        Asserts.assertFalse(ServletRequests.isUnknownIp("127.0.0.1"));
        Asserts.assertFalse(ServletRequests.isUnknownIp(" "));
    }

    @Test
    public void getRequestedDomainByForwardedHostTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-For")).thenReturn("1.1.1.1");
        when(request.getHeader("X-Forwarded-Host"))
            .thenReturn(" youngmonkeys.org , proxy.youngmonkeys.org");
        when(request.getHeader("Host")).thenReturn("localhost:8080");

        // when
        String actual = ServletRequests.getRequestedDomain(request);

        // then
        Asserts.assertEquals(actual, "youngmonkeys.org");
        verify(request, times(1)).getHeader("X-Forwarded-Host");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestedDomainByHostTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("X-Forwarded-Host")).thenReturn(" ");
        when(request.getHeader("Host")).thenReturn("localhost:8080");
        when(request.getServerName()).thenReturn("localhost");

        // when
        String actual = ServletRequests.getRequestedDomain(request);

        // then
        Asserts.assertEquals(actual, "localhost:8080");
        verify(request, times(1)).getHeader("X-Forwarded-Host");
        verify(request, times(1)).getHeader("Host");
        verifyNoMoreInteractions(request);
    }

    @Test
    public void getRequestedDomainByServerNameTest() {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Host")).thenReturn("");
        when(request.getServerName()).thenReturn("localhost");

        // when
        String actual = ServletRequests.getRequestedDomain(request);

        // then
        Asserts.assertEquals(actual, "localhost");
        verify(request, times(1)).getHeader("X-Forwarded-Host");
        verify(request, times(1)).getHeader("Host");
        verify(request, times(1)).getServerName();
        verifyNoMoreInteractions(request);
    }

    @Test
    public void newInstanceTest() throws Exception {
        // given
        Constructor<ServletRequests> constructor =
            ServletRequests.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        // when
        ServletRequests actual = constructor.newInstance();

        // then
        Asserts.assertNotNull(actual);
    }

    private static void resetStaticFields() throws Exception {
        for (String fieldName : new String[] {
            "clientIpResolver",
            "environmentManager"
        }) {
            Field field = ServletRequests.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(null, null);
        }
    }
}
