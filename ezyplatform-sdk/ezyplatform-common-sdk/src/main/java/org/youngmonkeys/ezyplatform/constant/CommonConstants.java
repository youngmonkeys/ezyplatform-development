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
import org.youngmonkeys.ezyplatform.entity.TargetType;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Set;

@SuppressWarnings("LineLength")
public final class CommonConstants {

    public static final String LATEST = "latest";

    public static final String TRUE_STRING = "true";
    public static final String FALSE_STRING = "false";
    public static final String UNKNOWN = "UNKNOWN";

    public static final String PARAMETER_TYPE_NAME_DATE = "Date";
    public static final String PARAMETER_TYPE_NAME_TIME = "Time";
    public static final String PARAMETER_TYPE_NAME_DATETIME = "DateTime";

    public static final String PATTERN_U_INT_NUMBER = "[0-9]+";
    public static final String PATTERN_COMMON_STRING = "[a-zA-Z0-9\\s]+";
    public static final String PATTERN_COMMON_NAME = "(([A-Z][a-zA-Z0-9]+[\\s])|([A-Z][a-zA-Z0-9]+))+";
    public static final String PATTERN_DATE = "yyyy-MM-dd";
    public static final String PATTERN_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String PATTERN_TIME = "HH:mm:ss";
    public static final String PATTERN_USERNAME = "[a-zA-Z0-9_.-]{3,64}";
    public static final String PATTERN_EMAIL = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    public static final String PATTERN_PHONE =
        "(^(\\+\\d{1,2}\\s)?\\(?\\d{3}\\)?[\\s.-]\\d{3}[\\s.-]\\d{4}$)|(^(\\+\\d{1,2})?[\\d]{6,15})";
    public static final String PATTERN_HTTP_URL =
        "https?:\\/\\/(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.?[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    public static final String PATTERN_WEBSOCKET_URL =
        "wss?:\\/\\/[-a-zA-Z0-9@:%._\\+~#=]{1,256}\\.?[a-zA-Z0-9()]{1,6}\\b([-a-zA-Z0-9()@:%_\\+.~#?&//=]*)";
    public static final String PATTERN_MEDIA_NAME = "^[a-zA-Z0-9_-]+(\\.[a-zA-Z0-9_-]+)*$";
    public static final String PATTERN_VERSION = "[0-9]+[\\.][0-9]+[\\.][0-9]+";
    public static final String PATTERN_PROPERTY_NAME = "[\\d\\w_\\-\\.\\+\\$]+";
    public static final String PATTERN_SHA256_STRING = "[a-fA-F0-9\\s]{64}";
    public static final String PATTERN_PACKAGE_NAME = "[a-z0-9_\\.]+";
    public static final String PATTERN_TAG = "[a-z0-9\\-\\s]+";
    public static final String PATTERN_HIDDEN_PASSWORD = "[*]+";
    public static final String PATTERN_ENUM_NAME = "[A-Z0-9_\\s]+";
    public static final String PATTERN_CONTRACT_ADDRESS = "[a-zA-Z0-9]{8,64}";
    public static final String PATTERN_WALLET_ADDRESS = "[a-zA-Z0-9]{8,64}";

    public static final String PASSWORD_SALT = "$2a$10$48rTBuvOefj6u2o4IXJ3ve";

    public static final String PREFIX_HTTP_URL = "http://";
    public static final String PREFIX_HTTPS_URL = "https://";

    public static final int MAX_LENGTH_UUID = 128;
    public static final int MIN_LENGTH_PASSWORD = 6;
    public static final int MAX_LENGTH_PASSWORD = 128;
    public static final int MIN_LENGTH_PHONE = 6;
    public static final int MAX_LENGTH_PHONE = 20;
    public static final int MAX_LENGTH_EMAIL = 128;
    public static final int MAX_LENGTH_DISPLAY_NAME = 64;
    public static final int MIN_LENGTH_URL = 8;
    public static final int MAX_LENGTH_URL = 256;
    public static final int MAX_LENGTH_WEBSITE_TITLE = 60;
    public static final int MAX_LENGTH_WEBSITE_TAGLINE = 120;
    public static final int MAX_LENGTH_PAGE_TITLE_SEPARATOR = 12;
    public static final int MAX_LENGTH_ENUM_NAME = 25;
    public static final int MAX_LENGTH_SEARCH_KEYWORD = 120;
    public static final int MAX_SEARCH_KEYWORD_WORDS = 30;
    public static final int MAX_LENGTH_SEARCH_TEXT = 128;
    public static final int MAX_ACTIVITY_HISTORY_PARAMETERS_LENGTH =
        600;

    public static final int LIMIT_30_RECORDS = 30;
    public static final int LIMIT_50_RECORDS = 50;
    public static final int LIMIT_100_RECORDS = 100;
    public static final int LIMIT_120_RECORDS = 120;
    public static final int LIMIT_150_RECORDS = 150;
    public static final int LIMIT_300_RECORDS = 300;
    public static final int LIMIT_500_RECORDS = 500;
    public static final int LIMIT_1500_RECORDS = 1500;
    public static final int LIMIT_3000_RECORDS = 3000;

    public static final int MIN_PAGE_SIZE = 1;
    public static final int MAX_PAGE_SIZE = 300;
    public static final int WEB_MAX_PAGE_SIZE = 100;

    public static final String COOKIE_NAME_ACCESS_TOKEN = "accessToken";
    public static final String COOKIE_NAME_ACCESS_TOKEN_EXPIRED_AT = "accessTokenExpiredAt";
    public static final String COOKIE_NAME_ADMIN_ACCESS_TOKEN = "adminAccessToken";
    public static final String COOKIE_NAME_ADMIN_ACCESS_TOKEN_EXPIRED_AT = "adminAccessTokenExpiredAt";
    public static final String COOKIE_NAME_ADMIN_SSO_CALL_BACK_URL = "adminSSOCallbackUrl";
    public static final String COOKIE_NAME_MARKET_ACCESS_TOKEN = "marketAccessToken";
    public static final String COOKIE_NAME_MARKET_ACCESS_TOKEN_EXPIRED_AT = "marketAccessTokenExpiredAt";

    public static final int COOKIE_ADMIN_SSO_CALL_BACK_URL_MAX_AGE = 5 * 60;

    public static final String KEY_AUTHENTICATED = "authenticated";

    public static final String META_KEY_SLUG = "slug";
    public static final String META_KEY_LOGIN_FAILURES = "login_failures";
    public static final String META_KEY_LOGIN_BLOCKED_UTIL = "login_blocked_util";
    public static final String META_KEY_VIEWS = "views";
    public static final String META_KEY_STARS = "stars";
    public static final String META_KEY_VOTES = "votes";
    public static final String META_KEY_HEARTS = "hearts";
    public static final String META_KEY_POINTS = "points";
    public static final String META_KEY_BADGE = "badge";
    public static final String META_KEY_THE_BEST_ANSWER = "theBestAnswer";
    public static final String META_KEY_STAR_POINT = "starPoints";
    public static final String META_KEY_ENABLE_2FA = "enable2FA";
    public static final String META_KEY_REGISTER_FROM = "registerFrom";
    public static final String META_KEY_FORGOT_PASSWORD_TOKEN = "forgotPasswordToken";
    public static final String META_KEY_RESET_PASSWORD_TOKEN = "resetPasswordToken";
    public static final String META_KEY_HAS_AUTO_GENERATED_NAME = "hasAutoGeneratedName";
    public static final String META_KEY_HAS_AUTO_GENERATED_PASSWORD = "hasAutoGeneratedPassword";

    public static final String SETTING_NODE_NAME = "ezyplatformNodeName";
    public static final String SETTING_NAME_VALUE_MAP = "valueMap";
    public static final String SETTING_NAME_EZYPLATFORM_DICTIONARY
        = "ezyplatform_dictionary";
    public static final String SETTING_NAME_ADMIN_DATE_FORMAT
        = "admin_date_format";
    public static final String SETTING_NAME_ADMIN_TIME_FORMAT
        = "admin_time_format";
    public static final String SETTING_NAME_ADMIN_DATETIME_FORMAT
        = "admin_datetime_format";
    public static final String SETTING_NAME_ADMIN_DATE_MINUTE_FORMAT
        = "admin_date_minute_format";
    public static final String SETTING_NAME_ADMIN_DEFAULT_LANGUAGE
        = "admin_default_language";
    public static final String SETTING_NAME_ADMIN_MAX_LOGIN_FAILURES
        = "admin_max_login_failures";
    public static final String SETTING_NAME_ADMIN_LOGIN_FAILURE_BLOCK_DURATIONS
        = "admin_login_failure_block_durations";
    public static final String SETTING_NAME_ADMIN_MAX_UPLOAD_FILE_SIZE
        = "admin_max_upload_file_size";
    public static final String SETTING_NAME_ADMIN_TOKEN_EXPIRED_IN_DAY
        = "admin_token_expired_in_day";
    public static final String SETTING_NAME_ADMIN_ACCEPTED_MEDIA_MIME_TYPES
        = "admin_accepted_media_mime_types";
    public static final String SETTING_NAME_ADMIN_AUTO_PASS_MANAGEMENT_URIS
        = "admin_auto_pass_management_uris";
    public static final String SETTING_NAME_ADMIN_ACCESS_TOKEN_ENCRYPTION_KEY
        = "admin_access_token_encryption_key";
    public static final String SETTING_NAME_ADMIN_RESET_PASSWORD_TOKEN_EXPIRED_IN_DAY
        = "admin_reset_password_token_expired_in_day";
    public static final String SETTING_NAME_ADMIN_SSO_ALLOWED_ORIGINS =
        "admin_sso_allowed_origins";
    public static final String SETTING_NAME_USER_TOKEN_EXPIRED_IN_DAY
        = "user_token_expired_in_day";
    public static final String SETTING_NAME_USER_MAX_LOGIN_FAILURES
        = "user_max_login_failures";
    public static final String SETTING_NAME_USER_LOGIN_FAILURE_BLOCK_DURATIONS
        = "user_login_failure_block_durations";
    public static final String SETTING_NAME_USER_ACCESS_TOKEN_ENCRYPTION_KEY
        = "user_access_token_encryption_key";
    public static final String SETTING_NAME_WEB_DATE_FORMAT
        = "web_date_format";
    public static final String SETTING_NAME_WEB_TIME_FORMAT
        = "web_time_format";
    public static final String SETTING_NAME_WEB_DATETIME_FORMAT
        = "web_datetime_format";
    public static final String SETTING_NAME_WEB_DATE_MINUTE_FORMAT
        = "web_date_minute_format";
    public static final String SETTING_NAME_WEB_DEFAULT_LANGUAGE
        = "web_default_language";
    public static final String SETTING_NAME_WEB_MAX_UPLOAD_FILE_SIZE
        = "web_max_upload_file_size";
    public static final String SETTING_NAME_WEB_ACCEPTED_MEDIA_MIME_TYPES
        = "web_accepted_media_mime_types";
    public static final String SETTING_NAME_WEB_SITE_NAME
        = "web_site_name";
    public static final String SETTING_NAME_WEB_SITE_TITLE
        = "web_site_title";
    public static final String SETTING_NAME_WEB_SITE_TAGLINE
        = "web_site_tagline";
    public static final String SETTING_NAME_WEB_SITE_IMAGE_URL
        = "web_site_image_url";
    public static final String SETTING_NAME_WEB_PAGE_TITLE_SEPARATOR
        = "web_page_title_separator";
    public static final String SETTING_NAME_WEB_AUTO_PASS_MANAGEMENT_URIS
        = "web_auto_pass_management_uris";
    public static final String SETTING_NAME_WEB_ALLOW_SEARCH_USER_BY_LIKE_OPERATOR =
        "web_allow_search_user_by_like_operator";
    public static final String SETTING_NAME_WEB_SITE_ICON_URL = "web_site_icon_url";
    public static final String SETTING_NAME_WEB_SITE_LOGO_URL = "web_site_logo_url";
    public static final String SETTING_NAME_ADMIN_URL = "admin_url";
    public static final String SETTING_NAME_WEB_URL = "web_url";
    public static final String SETTING_NAME_WEB_MANAGEMENT_URL = "web_management_url";
    public static final String SETTING_NAME_WEBSOCKET_URL = "websocket_url";
    public static final String SETTING_NAME_ENABLE_SUFFIX = "_enable";
    public static final String SETTING_NAME_ADMIN_ENABLE = "admin_enable";
    public static final String SETTING_NAME_SOCKET_ENABLE = "socket_enable";
    public static final String SETTING_NAME_WEB_ENABLE = "web_enable";
    public static final String SETTING_NAME_WEB_LANGUAGES = "web_languages";
    public static final String SETTING_NAME_ALLOW_SEND_STATISTICS_DATA =
        "allow_send_statistics_data";
    public static final String SETTING_NAME_TARGET_ROLE_FEATURES_SUFFIX =
        "_role_features";
    public static final String SETTING_NAME_WEB_ADDITIONAL_HEAD =
        "web_additional_head";
    public static final String SETTING_NAME_WEB_ADDITIONAL_STYLE_FILES =
        "web_additional_style_files";
    public static final String SETTING_NAME_WEB_ADDITIONAL_STYLE =
        "web_additional_style";
    public static final String SETTING_NAME_WEB_ADDITIONAL_SCRIPT_FILES =
        "web_additional_script_files";
    public static final String SETTING_NAME_WEB_ADDITIONAL_SCRIPT =
        "web_additional_script";
    public static final String SETTING_NAME_WEB_ADDITIONAL_FOOT =
        "web_additional_foot";

    public static final int DEFAULT_TIMEOUT_SECONDS = 15;
    public static final String DEFAULT_DATE_FORMAT = "YYYY-MM-DD";
    public static final String DEFAULT_TIME_FORMAT = "HH:mm:ss";
    public static final String DEFAULT_DATETIME_FORMAT = "YYYY-MM-DD HH:mm:ss";
    public static final String DEFAULT_DATE_MINUTE_FORMAT = "YYYY-MM-DD HH:mm";
    public static final int DEFAULT_CACHE_SETTINGS_PERIOD = 5;
    public static final boolean DEFAULT_ADMIN_ENABLE = true;
    public static final boolean DEFAULT_SOCKET_ENABLE = true;
    public static final boolean DEFAULT_WEB_ENABLE = true;
    public static final String DEFAULT_WEB_SITE_ICON_URL = "/images/favicon.ico";
    public static final String DEFAULT_WEB_SITE_LOGO_URL = "/images/logo.png";
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
                ContentType.IMAGE_JPEG.getValue(),
                ContentType.ICON.getValue()
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
    public static final String VIEW_VARIABLE_SITE_LOGO_URL = "siteLogoUrl";
    public static final String VIEW_VARIABLE_PAGE_CHARSET = "pageCharset";
    public static final String VIEW_VARIABLE_PAGE_CONTENT_TYPE = "pageContentType";
    public static final String VIEW_VARIABLE_PAGE_DESCRIPTION = "pageDescription";
    public static final String VIEW_VARIABLE_PAGE_VIEW_PORT = "pageViewPort";
    public static final String VIEW_VARIABLE_SITE_LOCAL = "siteLocale";
    public static final String VIEW_VARIABLE_PAGE_URL = "pageUrl";
    public static final String VIEW_VARIABLE_SITE_NAME = "siteName";
    public static final String VIEW_VARIABLE_SITE_PUBLISHER = "sitePublisher";
    public static final String VIEW_VARIABLE_PAGE_KEYWORDS = "pageKeywords";
    public static final String VIEW_VARIABLE_PAGE_IMAGE_URL = "pageImageUrl";
    public static final String VIEW_VARIABLE_MENU_BADGES = "menuBadges";
    public static final String VIEW_VARIABLE_MENUITEM_BADGES = "menuItemBadges";
    public static final String VIEW_VARIABLE_WEBSOCKET_URL = "websocketUrl";
    public static final String VIEW_VARIABLE_DEFAULT_DATE_FORMAT = "defaultDateFormat";
    public static final String VIEW_VARIABLE_DEFAULT_TIME_FORMAT = "defaultTimeFormat";
    public static final String VIEW_VARIABLE_DEFAULT_DATETIME_FORMAT = "defaultDateTimeFormat";
    public static final String VIEW_VARIABLE_DEFAULT_DATE_MINUTE_FORMAT = "defaultDateMinuteFormat";
    public static final String VIEW_VARIABLE_ADDITIONAL_MESSAGE_KEYS = "additionalMessageKeys";
    public static final String VIEW_VARIABLE_ADDITIONAL_MESSAGE_MAP = "additionalMessageMap";
    public static final String VIEW_VARIABLE_ADDITIONAL_VALUE_MAP = "additionalValueMap";
    public static final String VIEW_VARIABLE_ADDITIONAL_HEADS = "additionalHeads";
    public static final String VIEW_VARIABLE_ADDITIONAL_STYLE_FILES = "additionalStyleFiles";
    public static final String VIEW_VARIABLE_ADDITIONAL_STYLES = "additionalStyles";
    public static final String VIEW_VARIABLE_ADDITIONAL_SCRIPT_FILES = "additionalScriptFiles";
    public static final String VIEW_VARIABLE_ADDITIONAL_SCRIPTS = "additionalScripts";
    public static final String VIEW_VARIABLE_ADDITIONAL_FOOTS = "additionalFoots";
    public static final String VIEW_VARIABLE_MODULE_SCRIPT_FILES = "moduleScriptFiles";
    public static final String VIEW_VARIABLE_FINAL_SCRIPT_FILES = "finalScriptFiles";

    public static final String PROPERTY_NAME_EZYPLATFORM_HOME = "ezyplatform.home";

    public static final String ENVIRONMENT_DEVELOPMENT = "development";

    public static final String FILE_EXTENSION_ZIP = "zip";
    public static final String FILE_EXTENSION_SQL = "sql";

    public static final LocalDateTime MIN_SQL_DATETIME
        = LocalDateTime.of(1900, 1, 1, 0, 0, 0);

    private CommonConstants() {}

    public static String settingNameTargetRoleFeatures(TargetType targetType) {
        return settingNameTargetRoleFeatures(
            targetType.toString()
        );
    }

    public static String settingNameTargetRoleFeatures(String targetType) {
        return targetType.toLowerCase() +
            SETTING_NAME_TARGET_ROLE_FEATURES_SUFFIX;
    }
}
