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

package org.youngmonkeys.ezyplatform.web.controller;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.exception.*;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.annotation.TryCatch;
import com.tvd12.ezyhttp.server.core.manager.RequestURIManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import lombok.Setter;
import org.eclipse.jetty.http.BadMessageException;
import org.eclipse.jetty.util.Utf8Appendable;
import org.youngmonkeys.ezyplatform.exception.*;
import org.youngmonkeys.ezyplatform.service.SettingService;
import org.youngmonkeys.ezyplatform.web.view.WebViews;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.file.InvalidPathException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static java.util.Collections.singletonMap;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ADMIN_ACCESS_TOKEN;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.COOKIE_NAME_ADMIN_ACCESS_TOKEN_EXPIRED_AT;
import static org.youngmonkeys.ezyplatform.util.HttpRequests.addLanguageToUri;

@Setter
public class WebGlobalExceptionHandler extends EzyLoggable {

    @EzyAutoBind
    protected SettingService settingService;

    @EzyAutoBind
    protected RequestURIManager requestUriManager;

    @TryCatch(AdminInvalidAccessTokenException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        AdminInvalidAccessTokenException e
    ) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.builder()
                .status(StatusCodes.UNAUTHORIZED)
                .body(Collections.singletonMap("token", "invalid"))
                .build();
        }
        return redirectToAdminLogin(request);
    }

    @TryCatch(AdminAccessTokenExpiredException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        AdminAccessTokenExpiredException e
    ) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.builder()
                .status(StatusCodes.UNAUTHORIZED)
                .body(Collections.singletonMap("adminToken", "expired"))
                .build();
        }
        return redirectToAdminLogin(request);
    }

    @TryCatch(UserInvalidAccessTokenException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        UserInvalidAccessTokenException e
    ) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.builder()
                .status(StatusCodes.UNAUTHORIZED)
                .body(Collections.singletonMap("token", "invalid"))
                .build();
        }
        return WebViews.redirectToLogin(request);
    }

    @TryCatch(UserAccessTokenExpiredException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        UserAccessTokenExpiredException e
    ) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.builder()
                .status(StatusCodes.UNAUTHORIZED)
                .body(Collections.singletonMap("token", "expired"))
                .build();
        }
        return WebViews.redirectToLogin(request);
    }

    @TryCatch(IncorrectPasswordException.class)
    public ResponseEntity handle(IncorrectPasswordException e) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        return ResponseEntity.badRequest(
            Collections.singletonMap("password", "incorrect")
        );
    }

    @TryCatch(BadRequestException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        BadRequestException e
    ) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.badRequest(e.getErrors());
        }
        return Redirect.to(addLanguageToUri(request, "/bad-request"));
    }

    @TryCatch(BadMessageException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        BadMessageException e
    ) {
        return handleCommonBadRequestException(
            request,
            arguments,
            e
        );
    }

    @TryCatch(InvalidPathException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        InvalidPathException e
    ) {
        return handleCommonBadRequestException(
            request,
            arguments,
            e
        );
    }

    @TryCatch(Utf8Appendable.NotUtf8Exception.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        Utf8Appendable.NotUtf8Exception e
    ) {
        return handleCommonBadRequestException(
            request,
            arguments,
            e
        );
    }

    @TryCatch(IllegalArgumentException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        IllegalArgumentException e
    ) {
        return handleCommonBadRequestException(
            request,
            arguments,
            e
        );
    }

    @TryCatch(ResourceNotFoundException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        ResourceNotFoundException e
    ) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.notFound(
                e.getResponseData()
            );
        }
        return Redirect.to(addLanguageToUri(request, "/not-found"));
    }

    @TryCatch(HttpUnauthorizedException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        HttpUnauthorizedException e
    ) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.builder()
                .status(StatusCodes.UNAUTHORIZED)
                .body(e.getData())
                .build();
        }
        return Redirect.builder()
            .uri(addLanguageToUri(request, "/login"))
            .addCookie(COOKIE_NAME_ADMIN_ACCESS_TOKEN, "")
            .build();
    }

    @TryCatch(HttpNotFoundException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        HttpNotFoundException e
    ) {
        logger.debug("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.notFound(e.getData());
        }
        return Redirect.to(addLanguageToUri(request, "/not-found"));
    }

    @TryCatch(ForbiddenActionException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        ForbiddenActionException e
    ) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.status(
                    StatusCodes.FORBIDDEN
                )
                .body(e.getResponseData())
                .build();
        }
        return Redirect.to(addLanguageToUri(request, "/not-found"));
    }

    @TryCatch(PermissionDeniedException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        PermissionDeniedException e
    ) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.status(
                    StatusCodes.FORBIDDEN
                )
                .body(singletonMap("permission", "denied"))
                .build();
        }
        return Redirect.to(addLanguageToUri(request, "/permission-denied"));
    }

    @TryCatch(HttpForbiddenException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        HttpForbiddenException e
    ) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage());
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.status(
                    StatusCodes.FORBIDDEN
                )
                .body(e.getData())
                .build();
        }
        return Redirect.to(addLanguageToUri(request, "/permission-denied"));
    }

    @TryCatch(DeserializeBodyException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        DeserializeBodyException e
    ) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage(), e);
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            Map<String, String> errors = new HashMap<>();
            Throwable cause = e.getCause();
            if (cause instanceof InvalidFormatException) {
                InvalidFormatException ex = (InvalidFormatException) cause;
                for (JsonMappingException.Reference ref : ex.getPath()) {
                    errors.put(ref.getFieldName(), "invalid");
                }
            } else {
                errors.put("fields", "invalid");
            }
            return ResponseEntity.badRequest(
                errors
            );
        }
        return Redirect.to(addLanguageToUri(request, "/bad-request"));
    }

    @TryCatch(DeserializeValueException.class)
    public Object handle(
        HttpServletRequest request,
        RequestArguments arguments,
        DeserializeValueException e
    ) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage(), e);
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.badRequest(
                singletonMap(
                    e.getValueName(),
                    "invalid"
                )
            );
        }
        return Redirect.to(addLanguageToUri(request, "/bad-request"));
    }

    protected Object handleCommonBadRequestException(
        HttpServletRequest request,
        RequestArguments arguments,
        Exception e
    ) {
        logger.info("{}({})", e.getClass().getSimpleName(), e.getMessage(), e);
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isApiURI(method, uriTemplate)) {
            return ResponseEntity.badRequest(
                Collections.singletonMap("request", "invalid")
            );
        }
        return Redirect.to(addLanguageToUri(request, "/bad-request"));
    }

    protected Redirect redirectToAdminLogin(
        HttpServletRequest request
    ) {
        Cookie tokenCookie = new Cookie(
            COOKIE_NAME_ADMIN_ACCESS_TOKEN,
            ""
        );
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(0);
        Cookie tokenCookieExpiredAt = new Cookie(
            COOKIE_NAME_ADMIN_ACCESS_TOKEN_EXPIRED_AT,
            "0"
        );
        tokenCookieExpiredAt.setMaxAge(0);
        tokenCookieExpiredAt.setPath("/");
        String adminLoginUri = settingService.getAdminUrl() +
            "/sso-web" +
            "?callbackUrl=" + request.getRequestURI();
        String queryString = request.getQueryString();
        if (isNotBlank(queryString)) {
            adminLoginUri += "?" + queryString;
        }
        return Redirect.builder()
            .uri(adminLoginUri)
            .addCookie(tokenCookie)
            .addCookie(tokenCookieExpiredAt)
            .build();
    }
}
