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

package org.youngmonkeys.ezyplatform.repo;

import com.tvd12.ezydata.database.EzyDatabaseRepository;
import com.tvd12.ezyfox.database.annotation.EzyQuery;
import com.tvd12.ezyfox.util.Next;
import org.youngmonkeys.ezyplatform.entity.ContentTemplate;
import org.youngmonkeys.ezyplatform.result.ContentTypeResult;
import org.youngmonkeys.ezyplatform.result.SimpleContentTemplateResult;
import org.youngmonkeys.ezyplatform.result.TemplateTypeResult;

import java.util.Collection;
import java.util.List;

public interface ContentTemplateRepository
    extends EzyDatabaseRepository<Long, ContentTemplate> {

    @EzyQuery(
        "SELECT DISTINCT e.contentType FROM ContentTemplate e " +
            "ORDER BY e.contentType ASC"
    )
    List<ContentTypeResult> findAllContentTypes();

    @EzyQuery(
        "SELECT DISTINCT e.templateType " +
            "FROM ContentTemplate e " +
            "ORDER BY e.templateType ASC"
    )
    List<TemplateTypeResult> findAllTemplateTypes();

    @EzyQuery(
        "SELECT" +
            " e.id, e.templateName, e.titleTemplate," +
            " e.creatorId, e.status, e.createdAt, e.updatedAt" +
            " FROM ContentTemplate e" +
            " WHERE e.templateType = ?0" +
            " ORDER BY e.id DESC"
    )
    List<SimpleContentTemplateResult> findTemplatesByType(
        String templateType,
        Next next
    );

    @EzyQuery(
        "SELECT" +
            " e.id, e.templateName, e.titleTemplate," +
            " e.creatorId, e.status, e.createdAt, e.updatedAt" +
            " FROM ContentTemplate e" +
            " WHERE e.templateType IN ?0" +
            " ORDER BY e.id DESC"
    )
    List<SimpleContentTemplateResult> findTemplatesByTypeIn(
        Collection<String> templateTypes,
        Next next
    );

    ContentTemplate findByTemplateTypeAndTemplateName(
        String templateType,
        String templateName
    );

    ContentTemplate findByOwnerTypeAndOwnerIdAndTemplateTypeAndTemplateName(
        String ownerType,
        long ownerId,
        String templateType,
        String templateName
    );

    ContentTemplate findByCreatorTypeAndCreatorIdAndTemplateTypeAndTemplateName(
        String creatorType,
        long creatorId,
        String templateType,
        String templateName
    );

    long countByTemplateType(String templateType);

    int countByTemplateTypeAndTemplateName(
        String templateType,
        String templateName
    );
}
