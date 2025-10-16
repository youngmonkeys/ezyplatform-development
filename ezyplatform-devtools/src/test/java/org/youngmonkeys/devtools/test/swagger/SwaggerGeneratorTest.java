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

package org.youngmonkeys.devtools.test.swagger;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.devtools.swagger.SwaggerGenerator;

import java.io.File;

public class SwaggerGeneratorTest {

    @Test
    public void generateToDefaultFileTest() throws Exception {
        // given
        SwaggerGenerator sut = new SwaggerGenerator(
            "org.youngmonkeys.devtools.test"
        );

        // when
        sut.generateToDefaultFile();

        // then
        File swaggerFile = new File("swagger.yaml");
        Asserts.assertTrue(swaggerFile.exists());
    }
}
