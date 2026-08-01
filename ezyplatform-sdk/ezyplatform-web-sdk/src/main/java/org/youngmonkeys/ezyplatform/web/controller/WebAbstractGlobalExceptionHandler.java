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

import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.server.core.view.View;
import lombok.Setter;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.VIEW_VARIABLE_PAGE_TITLE;

@Setter
public class WebAbstractGlobalExceptionHandler extends EzyLoggable {

    protected View.Builder newBadRequestViewBuilder() {
        return newErrorViewBuilder(
            getBadRequestViewTemplate(),
            "bad_request"
        );
    }

    protected String getBadRequestViewTemplate() {
        return "ezycommon/errors/400";
    }

    protected View.Builder newNotFoundViewBuilder() {
        return newErrorViewBuilder(
            getNotFoundViewTemplate(),
            "not_found"
        );
    }

    protected String getNotFoundViewTemplate() {
        return "ezycommon/errors/404";
    }

    protected View.Builder newPermissionDeniedViewBuilder() {
        return newErrorViewBuilder(
            getPermissionDeniedViewTemplate(),
            "permission_denied"
        );
    }

    protected String getPermissionDeniedViewTemplate() {
        return "ezycommon/errors/403";
    }

    protected View.Builder newServerErrorViewBuilder() {
        return newErrorViewBuilder(
            getServerErrorViewTemplate(),
            "server_error"
        );
    }

    protected String getServerErrorViewTemplate() {
        return "ezycommon/errors/500";
    }

    protected View.Builder newErrorViewBuilder(
        String template,
        String pageTitleKey
    ) {
        return View.builder()
            .template(template)
            .addVariable(VIEW_VARIABLE_PAGE_TITLE, pageTitleKey);
    }
}
