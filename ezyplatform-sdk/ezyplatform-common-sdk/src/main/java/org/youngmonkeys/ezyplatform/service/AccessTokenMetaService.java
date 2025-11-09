/*
 * Copyright 2025 youngmonkeys.org
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

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.AccessTokenMeta;
import org.youngmonkeys.ezyplatform.model.AccessTokenMetaModel;
import org.youngmonkeys.ezyplatform.model.SaveAccessTokenMetaModel;
import org.youngmonkeys.ezyplatform.repo.AccessTokenMetaRepository;

@AllArgsConstructor
public class AccessTokenMetaService {

    private final AccessTokenMetaRepository accessTokenMetaRepository;
    private final DefaultEntityToModelConverter entityToModelConverter;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    public long saveAccessTokenMeta(
        SaveAccessTokenMetaModel model
    ) {
        AccessTokenMeta entity = accessTokenMetaRepository
            .findByTargetAndAccessToken(
                model.getTarget(),
                model.getAccessToken()
            );
        if (entity == null) {
            entity = modelToEntityConverter.toEntity(model);
        } else {
            modelToEntityConverter.mergeToEntity(model, entity);
        }
        accessTokenMetaRepository.save(entity);
        return entity.getId();
    }

    public void deleteAccessTokenMetaByTargetAndAccessToken(
        String target,
        String accessToken
    ) {
        accessTokenMetaRepository.deleteByTargetAndAccessToken(
            target,
            accessToken
        );
    }

    public AccessTokenMetaModel getAccessTokenMetaByTargetAndAccessToken(
        String target,
        String accessToken
    ) {
        return entityToModelConverter.toModel(
            accessTokenMetaRepository.findByTargetAndAccessToken(
                target,
                accessToken
            )
        );
    }
}
