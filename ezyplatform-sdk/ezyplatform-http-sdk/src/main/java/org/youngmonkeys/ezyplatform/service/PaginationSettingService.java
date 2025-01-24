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

import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.model.CommonSettingModel;
import org.youngmonkeys.ezyplatform.pagination.IdDescSettingPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.SettingFilter;
import org.youngmonkeys.ezyplatform.pagination.SettingPaginationParameter;
import org.youngmonkeys.ezyplatform.pagination.SettingPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationSettingRepository;

public abstract class PaginationSettingService extends CommonPaginationService<
    CommonSettingModel,
    SettingFilter,
    SettingPaginationParameter,
    Long,
    Setting> {

    public PaginationSettingService(
        PaginationSettingRepository repository,
        SettingPaginationParameterConverter paginationParameterConverter
    ) {
        super(repository, paginationParameterConverter);
    }

    @Override
    protected abstract CommonSettingModel convertEntity(Setting entity);

    @Override
    protected SettingPaginationParameter defaultPaginationParameter() {
        return new IdDescSettingPaginationParameter();
    }
}
