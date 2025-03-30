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

package org.youngmonkeys.ezyplatform.service;

import org.youngmonkeys.ezyplatform.model.LinkModel;
import org.youngmonkeys.ezyplatform.model.SaveLinkModel;

public interface LinkService {

    void saveLink(SaveLinkModel model);

    void updateLink(
        long linkId,
        SaveLinkModel model
    );

    void removeLinkById(long linkId);

    void removeLinkByUri(String uri);

    boolean containsLinkByUri(String uri);

    LinkModel getLinkById(long linkId);

    LinkModel getLinkByIdIncludeImage(long linkId);

    LinkModel getLinkByUri(String uri);

    LinkModel getLinkByUriIncludeImage(String uri);

    default String getLinkUriById(long linkId) {
        LinkModel model = getLinkById(linkId);
        return  model != null ? model.getLinkUri() : null;
    }
}
