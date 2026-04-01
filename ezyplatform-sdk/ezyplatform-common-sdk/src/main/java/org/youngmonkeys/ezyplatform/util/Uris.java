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

import com.tvd12.ezyfox.io.EzyStrings;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.PREFIX_HTTPS_URL;

public final class Uris {

    private Uris() {}

    public static String resolveUrl(
        String root,
        String path
    ) {
        if (root.endsWith("/")) {
            if (path.startsWith("/")) {
                if (path.length() == 1) {
                    return root;
                }
                return  root + path.substring(1);
            } else {
                return root + path;
            }
        } else {
            if (path.startsWith("/")) {
                return root + path;
            } else {
                return root + '/' + path;
            }
        }
    }

    public static boolean uriStartsWith(
        String uri,
        String prefix
    ) {
        String compareUri = prefix.startsWith("/")
            ? prefix
            : "/" + prefix;
        if (uri.equals(compareUri)) {
            return true;
        }
        String compareUriPrefix = compareUri + "/";
        return uri.startsWith(compareUriPrefix);
    }

    public static String getSiteName(
        String siteUrl
    ) {
        return getSiteName(siteUrl, EMPTY_STRING);
    }

    public static String getSiteName(
        String siteUrl,
        String defaultValue
    ) {
        if (isBlank(siteUrl)) {
            return defaultValue;
        }
        int lastDotIndex = siteUrl.lastIndexOf('.');
        String host = siteUrl;
        if (lastDotIndex > 0) {
            host = host.substring(0, lastDotIndex);
        }
        int dotIndex = host.lastIndexOf('.');
        if (dotIndex >= 0) {
            host = host.substring(dotIndex + 1);
        }
        int slashIndex = host.lastIndexOf('/');
        if (slashIndex >= 0) {
            host = host.substring(slashIndex + 1);
        }
        int colonIndex = host.lastIndexOf(':');
        if (colonIndex >= 0) {
            host = host.substring(0, colonIndex);
        }
        return EzyStrings.toDisplayName(host);
    }

    public static String getFileExtensionInUrl(
        String url
    ) {
        if (isBlank(url)) {
            return null;
        }
        int index = -1;
        int length = url.length();
        for (int i = length - 1; i >= 0; --i) {
            char ch = url.charAt(i);
            if (ch == '.') {
                index = i + 1;
                break;
            }
            if (ch == '/') {
                break;
            }
        }
        return (index >= 0 && index < length)
            ? url.substring(index)
            : null;
    }

    public static boolean isSslDomain(
        String domain
    ) {
        if (isBlank(domain) || !domain.startsWith(PREFIX_HTTPS_URL)) {
            return false;
        }
        try {
            String host = new URI(domain).getHost();
            return !isBlank(host)
                && !isIpHost(host);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isIpHost(
        String host
    ) {
        return isIpv4Host(host)
            || isIpv6Host(host);
    }

    private static boolean isIpv4Host(
        String host
    ) {
        String[] items = host.split("\\.");
        if (items.length != 4) {
            return false;
        }
        for (String item : items) {
            if (isBlank(item) || !item.matches("\\d{1,3}")) {
                return false;
            }
            int value = Integer.parseInt(item);
            if (value < 0 || value > 255) {
                return false;
            }
        }
        return true;
    }

    private static boolean isIpv6Host(
        String host
    ) {
        String normalizedHost = host;
        if (host.startsWith("[") && host.endsWith("]")) {
            normalizedHost = host.substring(1, host.length() - 1);
        }
        if (isBlank(normalizedHost)) {
            return false;
        }
        for (int i = 0; i < normalizedHost.length(); ++i) {
            char ch = normalizedHost.charAt(i);
            if (Character.digit(ch, 16) >= 0
                || ch == ':'
                || ch == '.'
                || ch == '%'
            ) {
                continue;
            }
            return false;
        }
        try {
            return InetAddress.getByName(normalizedHost)
                instanceof Inet6Address;
        } catch (Exception e) {
            return false;
        }
    }
}
