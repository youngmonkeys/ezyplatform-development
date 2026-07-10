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

package org.youngmonkeys.ezyplatform.test.util;

import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.performance.Performance;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.util.AccessTokens;

import java.util.concurrent.ThreadLocalRandom;

public class AccessTokensTest {

    private static final int ACCESS_TOKEN_HEADER_LENGTH = 64;
    private static final int ACCESS_TOKEN_LENGTH = 128;

    public static void main(String[] args) {
        byte[] key = EzyAesCrypt.randomKey();
        long time = Performance.create()
            .test(() -> {
                long sourceId = ThreadLocalRandom
                    .current()
                    .nextLong(0, Long.MAX_VALUE);
                String token = AccessTokens.generateAccessToken(
                    "admin",
                    sourceId,
                    key
                );
                assert token.length() == ACCESS_TOKEN_LENGTH;
                long extractedSourceId = AccessTokens.extractSourceId(
                    token,
                    key
                );
                assert sourceId == extractedSourceId;
            })
            .getTime();
        System.out.println(time);
    }

    @Test
    public void test() {
        // given
        byte[] key = EzyAesCrypt.randomKey();
        long sourceId = ThreadLocalRandom
            .current()
            .nextLong(0, Long.MAX_VALUE);

        // when
        String token = AccessTokens.generateAccessToken(
            "admin",
            sourceId,
            key
        );
        String secondToken = AccessTokens.generateAccessToken(
            "admin",
            sourceId,
            key
        );
        long extractedSourceId = AccessTokens.extractSourceId(
            token,
            key
        );

        // then
        assert token.length() == ACCESS_TOKEN_LENGTH;
        assert token.matches("[0-9a-f]{128}");
        assert token
            .substring(0, ACCESS_TOKEN_HEADER_LENGTH)
            .matches("[0-9a-f]{64}");
        assert token
            .substring(ACCESS_TOKEN_HEADER_LENGTH)
            .matches("[0-9a-f]{64}");
        assert !token.equals(secondToken);
        assert sourceId == extractedSourceId;
    }

    @Test
    public void extractSourceIdZero() {
        // given
        byte[] key = EzyAesCrypt.randomKey();

        // when
        // then
        Asserts.assertZero(AccessTokens.extractSourceId("", key));
    }

    @Test
    public void extractBearerTokenTest() {
        // given
        String accessToken = "123456";

        // when
        String tokenFromNull = AccessTokens.extractBearerToken(null);
        String tokenFromBlank = AccessTokens.extractBearerToken("");
        String tokenFromRawToken = AccessTokens.extractBearerToken(
            accessToken
        );
        String tokenFromBearerToken = AccessTokens.extractBearerToken(
            "Bearer " + accessToken
        );
        String tokenFromInvalidBearerValue =
            AccessTokens.extractBearerToken(
                "Authorization: Bearer " + accessToken
            );

        // then
        Asserts.assertNull(tokenFromNull);
        Asserts.assertNull(tokenFromBlank);
        Asserts.assertEquals(tokenFromRawToken, accessToken);
        Asserts.assertEquals(tokenFromBearerToken, accessToken);
        Asserts.assertEquals(
            tokenFromInvalidBearerValue,
            "Authorization: Bearer " + accessToken
        );
    }
}
