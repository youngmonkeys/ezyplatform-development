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

import org.youngmonkeys.ezyplatform.manager.EnvironmentManager;

import javax.servlet.http.HttpServletRequest;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;

public final class ServletRequests {

    private static ClientIpResolver clientIpResolver;
    private static EnvironmentManager environmentManager;

    private static final String UNKNOWN_IP = "unknown";

    private ServletRequests() {}

    /**
     * Keep the EzyPlatform core simple: complex client IP resolution
     * should be delegated to a plugin such as EzyDefence.
     * This method is always called on the main thread, so no synchronization is needed.
     *
     * @param resolver the client ip resolver.
     */
    public static void setClientIpResolver(
        ClientIpResolver resolver
    ) {
        if (clientIpResolver == null) {
            clientIpResolver = resolver;
        }
    }

    public static void setEnvironmentManager(
        EnvironmentManager manager
    ) {
        if (environmentManager == null) {
            environmentManager = manager;
        }
    }

    public static String getClientIp(
        HttpServletRequest request
    ) {
        return getClientIp(
            request,
            environmentManager != null
                && environmentManager.isWithoutProxy()
        );
    }

    public static String getClientIp(
        HttpServletRequest request,
        boolean requestDirectly
    ) {
        String ip = NULL_STRING;
        if (clientIpResolver != null) {
            ip = clientIpResolver.resolve(
                request,
                requestDirectly
            );
        }
        if (!isUnknownIp(ip)) {
            return ip;
        }
        if (requestDirectly) {
            return request.getRemoteAddr();
        }
        ip = request.getHeader("X-Forwarded-For");
        if (!isUnknownIp(ip)) {
            return ip;
        }
        ip = request.getHeader("Proxy-Client-IP");
        if (!isUnknownIp(ip)) {
            return ip;
        }
        ip = request.getHeader("WL-Proxy-Client-IP");
        if (!isUnknownIp(ip)) {
            return ip;
        }
        ip = request.getHeader("HTTP_CLIENT_IP");
        if (!isUnknownIp(ip)) {
            return ip;
        }
        ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        if (!isUnknownIp(ip)) {
            return ip;
        }
        ip = request.getHeader("X-Real-IP");
        if (!isUnknownIp(ip)) {
            return ip;
        }
        return UNKNOWN_IP;
    }
    
    public static boolean isUnknownIp(String ip) {
        return ip == null || ip.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(ip);
    }

    public static String getRequestedDomain(
        HttpServletRequest request
    ) {
        String host = request.getHeader("X-Forwarded-Host");
        if (isNotBlank(host)) {
            return host.split(",")[0].trim();
        }
        host = request.getHeader("Host");
        if (isNotBlank(host)) {
            return host;
        }
        return request.getServerName();
    }
}
