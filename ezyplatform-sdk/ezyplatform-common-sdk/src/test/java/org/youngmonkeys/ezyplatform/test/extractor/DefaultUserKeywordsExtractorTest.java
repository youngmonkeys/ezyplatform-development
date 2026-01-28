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

package org.youngmonkeys.ezyplatform.test.extractor;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.testng.collections.Sets;
import org.youngmonkeys.ezyplatform.entity.User;
import org.youngmonkeys.ezyplatform.extractor.DefaultUserKeywordsExtractor;

import java.util.Set;

public class DefaultUserKeywordsExtractorTest {

    @Test
    public void getKeywordsTest() {
        // given
        User user = new User();
        user.setId(10);
        user.setUsername("tvd12");
        user.setEmail("dung@youngmonkeys.org");
        user.setDisplayName("Ta Van Dung");
        user.setPhone("12345678");

        DefaultUserKeywordsExtractor sut = new DefaultUserKeywordsExtractor();

        // when
        Set<String> actual = sut.extract(user);

        // then
        Set<String> expectation = Sets.newHashSet(
            "12345678",
            "2345678",
            "345678",
            "45678",
            "5678",
            "678",
            "123",
            "12",
            "tvd12",
            "vd12",
            "d12",
            "tvd",
            "tv",
            "ta van dung",
            "ta van",
            "ta",
            "van",
            "dung",
            "ung",
            "dun",
            "va",
            "du",
            "dung@youngmonkeys.org",
            "10"
        );
        Asserts.assertEquals(actual, expectation, false);
    }
}
