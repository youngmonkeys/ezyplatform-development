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

package org.youngmonkeys.ezyplatform.test.validator;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyhttp.core.json.ObjectMapperBuilder;
import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.validator.DataValidator;

public class DataValidatorTest {

    private final ObjectMapper objectMapper = new ObjectMapperBuilder().build();

    @Test
    public void isValidJsonWithValidObjectTest() throws Exception {
        // given
        DataValidator validator = new DataValidator(objectMapper);

        // when
        boolean actual = validator.isValidJson("{\"name\":\"ezyplatform\",\"active\":true}");

        // then
        Asserts.assertTrue(actual);
    }

    @Test
    public void isValidJsonWithValidArrayTest() throws Exception {
        // given
        DataValidator validator = new DataValidator(objectMapper);

        // when
        boolean actual = validator.isValidJson("[1,2,3]");

        // then
        Asserts.assertTrue(actual);
    }

    @Test
    public void isValidJsonWithInvalidJsonTest() throws Exception {
        // given
        DataValidator validator = new DataValidator(objectMapper);

        // when
        boolean actual = validator.isValidJson("{name:\"ezyplatform\"}");

        // then
        Asserts.assertFalse(actual);
    }

    @Test
    public void isValidJsonWithBlankValueTest() throws Exception {
        // given
        DataValidator validator = new DataValidator(objectMapper);

        // when
        boolean actual = validator.isValidJson("   ");

        // then
        Asserts.assertFalse(actual);
    }
}
