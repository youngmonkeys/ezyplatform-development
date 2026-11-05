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

package org.youngmonkeys.ezyplatform.web.view;

import com.tvd12.ezyfox.bean.annotation.EzyAutoBind;
import com.tvd12.ezyhttp.server.core.view.View;
import com.tvd12.ezyhttp.server.core.view.ViewDecorator;
import lombok.Setter;
import org.youngmonkeys.ezyplatform.model.WebLanguageModel;
import org.youngmonkeys.ezyplatform.service.WebLanguageService;
import org.youngmonkeys.ezyplatform.util.HttpRequests;
import org.youngmonkeys.ezyplatform.util.TextDirections;
import org.youngmonkeys.ezyplatform.view.ViewVariables;

import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.PARAMETER_NAME_LANG;

@Setter
public class WebViewLanguageDecorator implements ViewDecorator {

    @EzyAutoBind
    protected WebLanguageService webLanguageService;

    @Override
    public void decorate(HttpServletRequest request, View view) {
        setLanguage(request, view);
        setTextDirection(view);
        setLanguages(request, view);
    }

    protected void setLanguage(HttpServletRequest request, View view) {
        String lang = HttpRequests.getLanguage(request);
        if (isBlank(lang)) {
            if (view.containsVariable("ezyDefaultLang")) {
                return;
            }
            lang = webLanguageService.getDefaultLanguageCode();
            if (isNotBlank(lang)) {
                request.setAttribute(PARAMETER_NAME_LANG, lang);
                view.setLocale(new Locale(lang));
                view.setVariable("ezyDefaultLang", lang);
            }
        } else {
            if (view.containsVariable("ezyLang")) {
                return;
            }
            view.setLocale(new Locale(lang));
            view.setVariable("ezyLang", lang);
        }
    }

    protected void setTextDirection(View view) {
        if (view.containsVariable("ezyDir")) {
            return;
        }
        String lang = ViewVariables.getEzyLanguage(view);
        if (isBlank(lang)) {
            return;
        }
        WebLanguageModel language = webLanguageService
            .getLanguageByCode(lang);
        view.setVariable(
            "ezyDir",
            language != null
                ? language.resolveTextDirection()
                : TextDirections.getTextDirection(lang)
        );
    }

    protected void setLanguages(HttpServletRequest request, View view) {
        if (!view.containsVariable("webLanguages")
            && includeWebLanguages()
        ) {
            view.setVariable(
                "webLanguages",
                webLanguageService.getActiveLanguages()
            );
        }
    }

    protected boolean includeWebLanguages() {
        return false;
    }
}
