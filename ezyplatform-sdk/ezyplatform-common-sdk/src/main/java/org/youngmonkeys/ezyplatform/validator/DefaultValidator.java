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
