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

package org.youngmonkeys.ezyplatform.validator;

import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;

import java.util.Collection;
import java.util.Collections;

import static com.tvd12.ezyfox.io.EzyStrings.isNotEmpty;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_LENGTH_SEARCH_KEYWORD;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_SEARCH_KEYWORD_WORDS;
import static org.youngmonkeys.ezyplatform.validator.DefaultValidator.*;

public class CommonValidator {

    public void validatePageSize(int pageSize) {
        if (!isValidWebPageSize(pageSize)) {
            throw new HttpBadRequestException(
                Collections.singletonMap("pageSize", "invalid")
            );
        }
    }

    public void validateCollectionSize(
        String resourceName,
        Collection<?> collection
    ) {
        if (!isValidCollectionSize(collection)) {
            throw new HttpBadRequestException(
                Collections.singletonMap(resourceName, "invalid")
            );
        }
    }

    public void validateSearchKeyword(String keyword) {
        if (isNotEmpty(keyword) && !isValidSearchKeyword(keyword)) {
            throw new HttpBadRequestException(
                Collections.singletonMap("keyword", "invalid")
            );
        }
    }

    public boolean isValidSearchKeyword(String keyword) {
        return keyword.codePoints().count() <= MAX_LENGTH_SEARCH_KEYWORD
            && !maybeContainsSqlInjection(keyword)
            && keyword.split(" ").length <= MAX_SEARCH_KEYWORD_WORDS;
    }
}
