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

package org.youngmonkeys.ezyplatform.validator;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collection;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static com.tvd12.properties.file.util.PropertiesUtil.getKeysFromVariableName;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

public final class DefaultValidator {

    private DefaultValidator() {}

    @SuppressWarnings("AbbreviationAsWordInName")
    public static boolean isValidUIntNumber(String value) {
        return value != null
            && value.matches(PATTERN_U_INT_NUMBER);
    }

    public static boolean isValidUuid(String uuid) {
        return uuid != null
            && uuid.length() <= MAX_LENGTH_UUID;
    }

    public static boolean isValidEmail(String email) {
        return email != null
            && email.length() <= MAX_LENGTH_EMAIL
            && email.matches(PATTERN_EMAIL);
    }

    public static boolean isValidUsername(String username) {
        return username != null
            && username.matches(PATTERN_USERNAME);
    }

    public static boolean isValidPassword(String password) {
        return password != null
            && password.length() >= MIN_LENGTH_PASSWORD
            && password.length() <= MAX_LENGTH_PASSWORD;
    }

    public static boolean isValidPhone(String phone) {
        return phone != null
            && phone.length() >= MIN_LENGTH_PHONE
            && phone.length() <= MAX_LENGTH_PHONE
            && phone.matches(PATTERN_PHONE);
    }

    public static boolean isValidUrl(String url) {
        try {
            new URI(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isValidExternalUrl(String url) {
        try {
            URI uri = new URI(url);
            String scheme = uri.getScheme();
            String host = uri.getHost();
            return scheme.equals(HTTPS)
                && isNotBlank(host)
                && !host.equals(LOCALHOST)
                && isPublicHost(host)
                && !host.matches(PATTERN_IP)
                && uri.getUserInfo() == null;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isPublicHost(String host) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(host);
            for (InetAddress address : addresses) {
                if (isInternalNetworkAddress(address)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean isInternalNetworkAddress(
        InetAddress address
    ) {
        if (address.isAnyLocalAddress()
            || address.isLoopbackAddress()
            || address.isLinkLocalAddress()
        ) {
            return true;
        }
        if (address instanceof Inet4Address) {
            byte[] b = address.getAddress();
            int first = Byte.toUnsignedInt(b[0]);
            int second = Byte.toUnsignedInt(b[1]);

            // 10.0.0.0/8
            if (first == 10) {
                return true;
            }

            // 172.16.0.0/12
            if (first == 172 && (second >= 16 && second <= 31)) {
                return true;
            }

            // 192.168.0.0/16
            if (first == 192 && second == 168) {
                return true;
            }

            // 169.254.0.0/16 (link-local)
            if (first == 169 && second == 254) {
                return true;
            }
        }
        if (address instanceof Inet6Address) {
            if (address.isLoopbackAddress()) {
                return true;
            }
            byte[] b = address.getAddress();
            int firstByte = Byte.toUnsignedInt(b[0]);
            return (firstByte & 0xfe) == 0xfc;
        }
        return false;
    }

    public static boolean isValidHttpUrl(String url) {
        return url != null
            && url.length() >= MIN_LENGTH_URL
            && url.length() <= MAX_LENGTH_URL
            && url.matches(PATTERN_HTTP_URL);
    }

    public static boolean isValidWebsocketUrl(String url) {
        return url != null
            && url.length() >= MIN_LENGTH_URL
            && url.length() <= MAX_LENGTH_URL
            && url.matches(PATTERN_WEBSOCKET_URL);
    }

    public static boolean isValidMediaName(String mediaName) {
        return mediaName != null
            && mediaName.matches(PATTERN_MEDIA_NAME);
    }

    public static boolean isValidVersion(String version) {
        return version != null
            && version.matches(PATTERN_VERSION);
    }

    public static boolean isSha256String(String str) {
        return str != null && str.matches(PATTERN_SHA256_STRING);
    }

    public static boolean isCommonString(String str, int maxLength) {
        return isCommonString(str, 1, maxLength);
    }

    public static boolean isCommonString(String str, int minLength, int maxLength) {
        return isNotBlank(str)
            && str.length() >= minLength
            && str.length() <= maxLength
            && str.matches(PATTERN_COMMON_STRING);
    }

    public static boolean isPackageName(String str, int minLength, int maxLength) {
        return isNotBlank(str)
            && str.length() >= minLength
            && str.length() <= maxLength
            && str.matches(PATTERN_PACKAGE_NAME);
    }

    public static boolean isIntegerNumber(String value) {
        try {
            Long.parseLong(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isFloatNumber(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isBoolean(String value) {
        try {
            return value.equals("true") || value.equals("false");
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean containsSqlComment(String str) {
        return str.contains("--");
    }

    public static boolean containsOperation(String str) {
        return str.contains("=")
            || str.contains(">")
            || str.contains("<");
    }

    public static boolean containsParentheses(String str) {
        return str.contains("'");
    }

    public static boolean maybeContainsSqlInjection(String str) {
        return containsSqlComment(str)
            || containsOperation(str)
            || containsParentheses(str);
    }

    public static boolean isValidPageSize(int pageSize) {
        return pageSize >= MIN_PAGE_SIZE && pageSize <= MAX_PAGE_SIZE;
    }

    public static boolean isValidCollectionSize(Collection<?> collection) {
        return collection.size() >= MIN_PAGE_SIZE
            && collection.size() <= MAX_PAGE_SIZE;
    }

    public static boolean isValidWebPageSize(int pageSize) {
        return pageSize >= MIN_PAGE_SIZE && pageSize <= WEB_MAX_PAGE_SIZE;
    }

    public static boolean containsVariable(String text) {
        return getKeysFromVariableName(text).size() > 0;
    }
}
