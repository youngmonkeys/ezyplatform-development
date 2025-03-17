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

import javax.servlet.http.HttpServletRequest;

public final class ServletRequests {

    private static final String UNKNOWN_IP = "unknown";

    private ServletRequests() {}

    public static String getClientIp(
        HttpServletRequest request,
        boolean requestDirectly
    ) {
        String ip = request.getHeader("X-Forwarded-For");
        if (isUnknownIp(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (isUnknownIp(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (isUnknownIp(ip)) {
            ip = requestDirectly ? request.getRemoteAddr() : UNKNOWN_IP;
        }
        return ip;
    }
    
    public static boolean isUnknownIp(String ip) {
        return ip == null || ip.isEmpty() || UNKNOWN_IP.equalsIgnoreCase(ip);
    }
}
