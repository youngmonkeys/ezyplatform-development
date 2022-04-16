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

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.core.constant.StatusCodes;
import com.tvd12.ezyhttp.core.response.ResponseEntity;
import com.tvd12.ezyhttp.server.core.handler.UnhandledErrorHandler;
import com.tvd12.ezyhttp.server.core.manager.RequestURIManager;
import com.tvd12.ezyhttp.server.core.view.Redirect;
import lombok.Setter;
import org.youngmonkeys.ezyplatform.manager.EnvironmentManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collections;

import static com.tvd12.ezyhttp.server.core.constant.CoreConstants.ATTRIBUTE_MATCHED_URI;
import static org.youngmonkeys.ezyplatform.util.Exceptions.exceptionToSimpleString;

/**
 * Handle all errors happen in when handle client's requests.
 * Because ezyplatform accepts only one <code>WebGlobalErrorHandler</code>,
 * so, you should not use it in a web-plugin, let's use it in a theme.
 * In a theme, you will need extend <code>WebGlobalErrorHandler</code> like this:
 * <pre>
 *     <code>@EzySingleton</code>
 *     public class MyWebGlobalErrorHandler extends WebGlobalErrorHandler {
 *         // do something
 *     }
 * </pre>
 */
@Setter
public class WebGlobalErrorHandler implements UnhandledErrorHandler {

    @EzyAutoBind
    protected RequestURIManager requestUriManager;

    @EzyAutoBind
    protected EnvironmentManager environmentManager;

    @Override
    public Object processError(
        HttpMethod method,
        HttpServletRequest request,
        HttpServletResponse response,
        int errorStatusCode,
        Exception exception
    ) {
        String matchedUri = (String) request.getAttribute(ATTRIBUTE_MATCHED_URI);
        if (matchedUri == null) {
            matchedUri = request.getRequestURI();
        }
        boolean isApiUri = method != HttpMethod.GET
            || requestUriManager.isApiURI(method, matchedUri);
        if (isApiUri) {
            return toResponseEntity(errorStatusCode, exception);
        }
        return postProcessError(
            method,
            request,
            response,
            errorStatusCode,
            exception
        );
    }

    protected Object processError(int errorStatusCode, Exception exception) {
        if (environmentManager.isDebugMode()) {
            return toResponseEntity(errorStatusCode, exception);
        }
        if (errorStatusCode == StatusCodes.INTERNAL_SERVER_ERROR) {
            return Redirect.builder()
                .uri("/server-error")
                .addAttribute(
                    "exceptionMessage",
                    exceptionToSimpleString(exception)
                )
                .build();
        } else if (errorStatusCode == StatusCodes.BAD_REQUEST) {
            return Redirect.to("/bad-request");
        }
        return Redirect.to("/not-found");
    }

    protected Object postProcessError(
        HttpMethod method,
        HttpServletRequest request,
        HttpServletResponse response,
        int errorStatusCode,
        Exception exception
    ) {
        return processError(errorStatusCode, exception);
    }

    private ResponseEntity toResponseEntity(
        int errorStatusCode,
        Exception exception
    ) {
        return ResponseEntity.status(errorStatusCode)
            .body(
                Collections.singletonMap(
                    "error",
                    exception != null
                        ? exception.getClass().getName()
                        : String.valueOf(errorStatusCode)
                )
            )
            .build();
    }
}
