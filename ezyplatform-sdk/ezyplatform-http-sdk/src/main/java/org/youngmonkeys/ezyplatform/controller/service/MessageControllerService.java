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

package org.youngmonkeys.ezyplatform.controller.service;

import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyhttp.server.core.view.ViewContext;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.fetcher.RequestLocalFetcher;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@AllArgsConstructor
public class MessageControllerService {

    private final EzySingletonFactory singletonFactory;
    private final RequestLocalFetcher requestLocalFetcher;

    public Map<String, String> getMessageMapByKey(
        HttpServletRequest request,
        Collection<String> keys
    ) {
        Locale locale = requestLocalFetcher.getLocal(request);
        ViewContext viewContext = singletonFactory.getSingletonCast(
            ViewContext.class
        );
        Map<String, String> underscoreKeyByKey = new HashMap<>();
        for (String key : keys) {
            String underscoreKey = key
                .toLowerCase()
                .replace(" ", "_");
            underscoreKeyByKey.put(key, underscoreKey);
        }
        Map<String, String> messageMap = viewContext.resolveMessages(
            locale,
            underscoreKeyByKey.values()
        );
        Map<String, String> answer = new HashMap<>();
        for (String key : keys) {
            String underscoreKey = underscoreKeyByKey.get(key);
            String value = messageMap.get(underscoreKey);
            answer.put(key, value);
        }
        return answer;
    }
}
