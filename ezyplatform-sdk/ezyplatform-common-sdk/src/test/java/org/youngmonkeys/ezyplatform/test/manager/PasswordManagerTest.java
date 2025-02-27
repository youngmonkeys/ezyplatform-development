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

package org.youngmonkeys.ezyplatform.test.manager;

import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.manager.PasswordManager;

public class PasswordManagerTest {

    private final PasswordManager instance = new PasswordManager();

    @Test
    public void correctTest() {
        // given
        String password = RandomUtil.randomShortAlphabetString();
        String hashPassword = instance.hashPassword(
            password
        );

        // when
        boolean correct = instance.isMatchingPassword(
            password,
            hashPassword
        );

        // then
        Asserts.assertTrue(correct);
    }

    @Test
    public void incorrectTest() {
        // given
        String password = RandomUtil.randomShortAlphabetString();
        String hashPassword = instance.hashPassword(
            password
        );

        // when
        boolean correct = instance.isMatchingPassword(
            RandomUtil.randomShortAlphabetString(),
            hashPassword
        );

        // then
        Asserts.assertFalse(correct);
    }

    @Test
    public void verifyTest() {
        // given
        String password = "admin123";
        String hashPassword0 = instance.hashPassword(
            password
        );
        System.out.println(hashPassword0);
        String hashPassword = "$2a$10$48rTBuvOefj6u2o4IXJ3ves6ufStpXFMiH0W/mb0UKNYeQ5.Kf91.";
        System.out.println(hashPassword);

        // when
        boolean correct = instance.isMatchingPassword(
            password,
            hashPassword
        );

        // then
        Asserts.assertTrue(correct);
    }
}
