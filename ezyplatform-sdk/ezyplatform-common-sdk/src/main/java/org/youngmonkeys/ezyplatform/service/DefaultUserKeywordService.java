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

import com.tvd12.ezyfox.util.EzyLoggable;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.DefaultModelToEntityConverter;
import org.youngmonkeys.ezyplatform.entity.UserKeyword;
import org.youngmonkeys.ezyplatform.model.AddUserKeywordModel;
import org.youngmonkeys.ezyplatform.repo.UserKeywordRepository;

import java.util.Collection;

@AllArgsConstructor
public class DefaultUserKeywordService
    extends EzyLoggable
    implements UserKeywordService {

    private final UserKeywordRepository userKeywordRepository;
    private final DefaultModelToEntityConverter modelToEntityConverter;

    @Override
    public void addUserKeyword(
        AddUserKeywordModel model
    ) {
        long userId = model.getUserId();
        String keyword = model.getKeyword();
        UserKeyword entity = userKeywordRepository
            .findByUserIdAndKeyword(userId, keyword);
        if (entity == null) {
            entity = modelToEntityConverter.toEntity(model);
        } else {
            modelToEntityConverter.mergeToEntity(model, entity);
        }
        try {
            userKeywordRepository.save(entity);
        } catch (Exception e) {
            logger.info(
                "save keyword: {} of useId: {} failed",
                keyword,
                userId
            );
        }
    }

    @Override
    public void addUserKeywords(
        Collection<AddUserKeywordModel> userKeywords
    ) {
        for (AddUserKeywordModel model : userKeywords) {
            addUserKeyword(model);
        }
    }

    @Override
    public void deleteUserKeywordsByUserId(
        long userId
    ) {
        userKeywordRepository.deleteByUserId(userId);
    }

    @Override
    public void deleteUserKeywordsByUserIds(
        Collection<Long> userIds
    ) {
        if (!userIds.isEmpty()) {
            userKeywordRepository.deleteByUserIdIn(userIds);
        }
    }

    @Override
    public boolean containsUserKeyword(
        long userId,
        String keyword
    ) {
        return userKeywordRepository.findByUserIdAndKeyword(
            userId,
            keyword
        ) != null;
    }
}
