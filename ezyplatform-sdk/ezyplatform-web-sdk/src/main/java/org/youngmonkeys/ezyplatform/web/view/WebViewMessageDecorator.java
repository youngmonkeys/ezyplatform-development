/*
 * Copyright 2023 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.web.view;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Map;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.VIEW_VARIABLE_ADDITIONAL_MESSAGE_MAP;

@Setter
public abstract class WebViewMessageDecorator
    extends WebViewLanguageDecorator {

    @EzyAutoBind
    private EzySingletonFactory singletonFactory;
    private final Collection<String> messageKeys;

    public WebViewMessageDecorator() {
        this.messageKeys = messageKeys();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public void decorate(HttpServletRequest request, View view) {
        super.decorate(request, view);
        ViewContext viewContext = this.singletonFactory.getSingletonCast(
            ViewContext.class
        );
        view.putKeyValuesToVariable(
            VIEW_VARIABLE_ADDITIONAL_MESSAGE_MAP,
            (Map) viewContext.resolveMessages(
                view.getLocale(),
                messageKeys
            )
        );
    }

    protected abstract Collection<String> messageKeys();

    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }
}
