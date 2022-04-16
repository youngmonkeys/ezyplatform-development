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

package org.youngmonkeys.ezyplatform.web.interceptor;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.constant.HttpMethod;
import com.tvd12.ezyhttp.server.core.interceptor.RequestInterceptor;
import com.tvd12.ezyhttp.server.core.manager.RequestURIManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import lombok.Setter;

import java.lang.reflect.Method;

@Setter
public abstract class WebRequestInterceptor
        extends EzyLoggable
        implements RequestInterceptor {

    @EzyAutoBind
    protected RequestURIManager requestUriManager;

    @Override
    public boolean preHandle(
        RequestArguments arguments,
        Method handler
    ) throws Exception {
        HttpMethod method = arguments.getMethod();
        String uriTemplate = arguments.getUriTemplate();
        if (requestUriManager.isManagementURI(method, uriTemplate)) {
            return true;
        }
        return validate(arguments, handler);
    }

    protected abstract boolean validate(
        RequestArguments arguments,
        Method handler
    ) throws Exception;
}
