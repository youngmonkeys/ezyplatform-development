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

package org.youngmonkeys.ezyplatform.test.security;

import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyBase64;
import org.testng.annotations.Test;

import java.security.SecureRandom;

public class SettingPasswordEncryptionTest {

    @Test
    public void test() throws Exception {
        // given
        String text = "hello";
        String password = "KSYzjcc8nqrBk8jXtW4QaMpr2suBU9vY";

        // when
        String encryption = EzyBase64.encode2utf(
            EzyAesCrypt.getDefault().encrypt(text.getBytes(), password.getBytes())
        );
        System.out.println(encryption);
    }
}
