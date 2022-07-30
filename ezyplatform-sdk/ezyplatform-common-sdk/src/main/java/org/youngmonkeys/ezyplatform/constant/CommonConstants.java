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

package org.youngmonkeys.ezyplatform.constant;

import com.tvd12.ezyfox.collect.Sets;
import com.tvd12.ezyhttp.core.constant.ContentType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("LineLength")
public final class CommonConstants {

    public static final String PATTERN_COMMON_STRING = "[a-zA-Z0-9\\s]+";
    public static final String PATTERN_COMMON_NAME = "(([A-Z][a-zA-Z0-9]+[\\s])|([A-Z][a-zA-Z0-9]+))+";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_USERNAME = "[a-zA-Z0-9_]{3,64}";
    public static final String PATTERN_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String PATTERN_PHONE =
        "(^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$)|(^(\\+\\d{1,2})?[\\d]{6,15})";
    public static final String PATTERN_HTTP_URL =
        "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    public static final String PATTERN_MEDIA_NAME = "[a-zA-Z0-9]+\\.[a-zA-Z]+";
    public static final String PATTERN_VERSION = "[0-9]+[\\.][0-9]+[\\.][0-9]+";
    public static final String PATTERN_PROPERTY_NAME = "[\\d\\w_\\-\\.\\+\\$]+";
    public static final String PATTERN_SHA256_STRING = "[a-fA-F0-9\\s]{64}";
    public static final String PATTERN_PACKAGE_NAME = "[a-z0-9_\\.]+";
    public static final String PATTERN_TAG = "[a-z0-9\\-\\s]+";
    public static final String PATTERN_HIDDEN_PASSWORD = "[*]+";
    public static final String PATTERN_ENUM_NAME = "[A-Z0-9_\\s]+";
    public static final String PASSWORD_SALT = "$2a$10$48rTBuvOefj6u2o4IXJ3ve";
    public static final int MIN_LENGTH_PASSWORD = 6;
    public static final int MAX_LENGTH_PASSWORD = 128;
    public static final int MIN_LENGTH_PHONE = 6;
    public static final int MAX_LENGTH_PHONE = 20;
    public static final int MAX_LENGTH_EMAIL = 128;
    public static final int MIN_LENGTH_URL = 10;
    public static final int MAX_LENGTH_URL = 256;
    public static final int MAX_ENUM_NAME_LENGTH = 25;
    public static final int MAX_SEARCH_KEYWORD_LENGTH = 50;
    public static final int MAX_SEARCH_KEYWORD_WORDS = 6;

    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 300;
    public static final int WEB_MAX_PAGE_SIZE = 100;

    public static final String COOKIE_NAME_ACCESS_TOKEN = "accessToken";
    public static final String COOKIE_NAME_ACCESS_TOKEN_EXPIRED_AT = "accessTokenExpiredAt";
    public static final String COOKIE_NAME_ADMIN_ACCESS_TOKEN = "adminAccessToken";
    public static final String COOKIE_NAME_ADMIN_ACCESS_TOKEN_EXPIRED_AT = "adminAccessTokenExpiredAt";

    public static final String KEY_AUTHENTICATED = "authenticated";

    public static final String SETTING_NAME_ADMIN_MAX_UPLOAD_FILE_SIZE
        = "admin_max_upload_file_size";
    public static final String SETTING_NAME_ADMIN_TOKEN_EXPIRED_IN_DAY
        = "admin_token_expired_in_day";
    public static final String SETTING_NAME_ADMIN_ACCEPTED_MEDIA_MIME_TYPES
        = "admin_accepted_media_mime_types";
    public static final String SETTING_NAME_ADMIN_AUTO_PASS_MANAGEMENT_URIS
        = "admin_auto_pass_management_uris";
    public static final String SETTING_NAME_USER_TOKEN_EXPIRED_IN_DAY
        = "user_token_expired_in_day";
    public static final String SETTING_NAME_WEB_MAX_UPLOAD_FILE_SIZE
        = "web_max_upload_file_size";
    public static final String SETTING_NAME_WEB_ACCEPTED_MEDIA_MIME_TYPES
        = "web_accepted_media_mime_types";
    public static final String SETTING_NAME_WEB_SITE_TITLE
        = "web_site_title";
    public static final String SETTING_NAME_WEB_SITE_TAGLINE
        = "web_site_tagline";
    public static final String SETTING_NAME_WEB_PAGE_TITLE_SEPARATOR
        = "web_page_title_separator";
    public static final String SETTING_NAME_WEB_AUTO_PASS_MANAGEMENT_URIS
        = "web_auto_pass_management_uris";
    public static final String SETTING_NAME_WEB_SITE_ICON_URL = "web_site_icon_url";
    public static final String SETTING_NAME_ADMIN_URL = "admin_url";
    public static final String SETTING_NAME_WEB_URL = "web_url";
    public static final String SETTING_NAME_WEB_MANAGEMENT_URL = "web_management_url";
    public static final String SETTING_NAME_WEBSOCKET_URL = "websocket_url";
    public static final String SETTING_NAME_ENABLE_SUFFIX = "_enable";
    public static final String SETTING_NAME_ADMIN_ENABLE = "admin_enable";
    public static final String SETTING_NAME_SOCKET_ENABLE = "socket_enable";
    public static final String SETTING_NAME_WEB_ENABLE = "web_enable";

    public static final boolean DEFAULT_ADMIN_ENABLE = true;
    public static final boolean DEFAULT_SOCKET_ENABLE = true;
    public static final boolean DEFAULT_WEB_ENABLE = true;
    public static final String DEFAULT_WEB_SITE_ICON_URL = "/images/favicon.ico";
    public static final String DEFAULT_ADMIN_URL = "http://localhost:9090";
    public static final String DEFAULT_WEB_URL = "http://localhost:8080";
    public static final String DEFAULT_WEB_MANAGEMENT_URL = "http://localhost:18080";
    public static final String DEFAULT_WEBSOCKET_URL = "ws://localhost:2208/ws";
    public static final String DEFAULT_HIDDEN_PASSWORD = "************";
    public static final long DEFAULT_TOKEN_EXPIRED_IN_DAY = 7;
    public static final String DEFAULT_MAX_UPLOAD_FILE_SIZE = "5MB";
    public static final Set<String> DEFAULT_ACCEPTED_IMAGE_TYPES =
        Collections.unmodifiableSet(
            Sets.newHashSet(
                ContentType.IMAGE_PNG.getValue(),
                ContentType.IMAGE_JPG.getValue(),
                ContentType.IMAGE_JPEG.getValue()
            )
        );
    public static final Set<String> DEFAULT_AUTO_PASS_URIS =
        Collections.unmodifiableSet(
            Sets.newHashSet(
                "/health-check",
                "/management/health-check"
            )
        );
    public static final String DEFAULT_THEME_NAME = "welcome";
    public static final String DEFAULT_SOCKET_MONITOR_PLUGIN = "socket-monitor";

    public static final String VIEW_VARIABLE_SITE_TITLE = "siteTitle";
    public static final String VIEW_VARIABLE_PAGE_TITLE = "pageTitle";
    public static final String VIEW_VARIABLE_PAGE_SEPARATOR = "pageTitleSeparator";
    public static final String VIEW_VARIABLE_SITE_ICON_URL = "siteIconUrl";
    public static final String VIEW_VARIABLE_PAGE_CHARSET = "pageCharset";
    public static final String VIEW_VARIABLE_PAGE_CONTENT_TYPE = "pageContentType";
    public static final String VIEW_VARIABLE_PAGE_DESCRIPTION = "pageDescription";
    public static final String VIEW_VARIABLE_PAGE_VIEW_PORT = "pageViewPort";
    public static final String VIEW_VARIABLE_SITE_LOCAL = "siteLocal";
    public static final String VIEW_VARIABLE_PAGE_URL = "pageUrl";
    public static final String VIEW_VARIABLE_SITE_NAME = "siteName";
    public static final String VIEW_VARIABLE_SITE_PUBLISHER = "sitePublisher";
    public static final String VIEW_VARIABLE_PAGE_KEYWORDS = "pageKeywords";
    public static final String VIEW_VARIABLE_PAGE_IMAGE_URL = "pageImageUrl";
    public static final String VIEW_VARIABLE_MENU_BADGES = "menuBadges";
    public static final String VIEW_VARIABLE_MENUITEM_BADGES = "menuItemBadges";

    public static final String PROPERTY_NAME_EZYPLATFORM_HOME = "ezyplatform.home";

    public static final String ENVIRONMENT_DEVELOPMENT = "development";

    public static final String FILE_EXTENSION_ZIP = "zip";
    public static final String FILE_EXTENSION_SQL = "sql";

    public static final LocalDateTime MIN_SQL_DATETIME
        = LocalDateTime.of(1900, 1, 1, 0, 0, 0);

    private CommonConstants() {}
}
