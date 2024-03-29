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

package org.youngmonkeys.ezyplatform.test.constant;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.constant.CommonConstants;
import org.youngmonkeys.ezyplatform.entity.TargetType;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.PATTERN_USERNAME;

public class CommonConstantsTest {

    @Test
    public void settingNameTargetRoleFeaturesTest() {
        // given
        // when
        String actual = CommonConstants
            .settingNameTargetRoleFeatures(TargetType.WEB);

        // then
        Asserts.assertEquals(actual, "web_role_features");
    }

    @Test
    public void usernamePatternTest() {
        Asserts.assertTrue("hello.world".matches(PATTERN_USERNAME));
        Asserts.assertTrue("hello_world".matches(PATTERN_USERNAME));
        Asserts.assertTrue("hello-world".matches(PATTERN_USERNAME));
        Asserts.assertFalse("hello+world".matches(PATTERN_USERNAME));
        Asserts.assertFalse("hello\\world".matches(PATTERN_USERNAME));
    }
}
