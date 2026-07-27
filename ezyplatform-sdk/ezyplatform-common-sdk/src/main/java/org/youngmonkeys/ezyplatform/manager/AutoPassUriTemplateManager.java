/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AutoPassUriTemplateManager {

    private final Set<String> autoPassUriTemplates =
        ConcurrentHashMap.newKeySet();

    public void addAutoPassUriTemplates(
        String... uris
    ) {
        addAutoPassUriTemplates(Arrays.asList(uris));
    }

    public void addAutoPassUriTemplates(
        Collection<String> uris
    ) {
        autoPassUriTemplates.addAll(uris);
    }

    public boolean containsUriTemplate(String uri) {
        return autoPassUriTemplates.contains(uri);
    }

    public List<String> getAutoPassUriTemplates() {
        return new ArrayList<>(autoPassUriTemplates);
    }
}
