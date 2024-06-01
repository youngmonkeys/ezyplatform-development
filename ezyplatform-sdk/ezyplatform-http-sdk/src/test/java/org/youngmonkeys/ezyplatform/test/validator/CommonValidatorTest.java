/*
 * Copyright 2024 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.validator;

import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.validator.CommonValidator;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_LENGTH_SEARCH_TEXT;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_LENGTH_UUID;

public class CommonValidatorTest {

    private final CommonValidator instance = new CommonValidator();

    @Test
    public void validateSearchTextTest() {
        // given
        String textName = RandomUtil.randomShortAlphabetString();
        String textValue = RandomUtil.randomShortAlphabetString();

        // when
        // then
        instance.validateSearchText(
            textName,
            textValue
        );
    }

    @Test
    public void validateSearchTextTestFailed() {
        // given
        String textName = RandomUtil.randomShortAlphabetString();
        String textValue = RandomUtil.randomAlphabetString(
            MAX_LENGTH_SEARCH_TEXT + 1
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.validateSearchText(
                textName,
                textValue
            )
        );

        // then
        Asserts.assertEqualsType(e, HttpBadRequestException.class);
    }

    @Test
    public void validateSearchUuidTest() {
        // given
        String uuid = RandomUtil.randomShortAlphabetString();

        // when
        // then
        instance.validateSearchUuid(uuid);
    }

    @Test
    public void validateSearchUuidTestFailed() {
        // given
        String uuid = RandomUtil.randomAlphabetString(
            MAX_LENGTH_UUID + 1
        );

        // when
        Throwable e = Asserts.assertThrows(() ->
            instance.validateSearchUuid(uuid)
        );

        // then
        Asserts.assertEqualsType(e, HttpBadRequestException.class);
    }
}
