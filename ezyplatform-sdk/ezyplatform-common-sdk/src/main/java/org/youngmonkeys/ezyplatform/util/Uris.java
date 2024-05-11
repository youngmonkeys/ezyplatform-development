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

import static com.tvd12.ezyfox.io.EzyStrings.EMPTY_STRING;
import static com.tvd12.ezyfox.io.EzyStrings.isBlank;

public final class Uris {

    private Uris() {}

    public static String resolveUrl(String root, String path) {
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

    public static boolean uriStartsWith(String uri, String prefix) {
        if (uri.startsWith(prefix)) {
            return true;
        }
        return uri.startsWith("/" + prefix);
    }

    public static String getSiteName(String siteUrl) {
        return getSiteName(siteUrl, EMPTY_STRING);
    }

    public static String getSiteName(String siteUrl, String defaultValue) {
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

    public static String getFileExtensionInUrl(String url) {
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
}
