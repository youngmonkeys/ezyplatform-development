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

package org.youngmonkeys.ezyplatform.util;

import com.tvd12.ezyfox.io.EzyBytes;
import com.tvd12.ezyfox.io.EzyLongs;
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyBase64;
import com.tvd12.ezyfox.security.EzySHA256;

import java.util.UUID;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;

public final class AccessTokens {

    private AccessTokens() {}

    public static String generateAccessToken(
        String source,
        long sourceId,
        byte[] encryptionKey
    ) {
        if (encryptionKey == null) {
            throw new IllegalStateException("server's not ready yet");
        }
        byte[] encryptedSourceIdBytes;
        try {
            byte[] sourceIdBytes = EzyBytes.getBytes(sourceId);
            encryptedSourceIdBytes = EzyAesCrypt
                .getDefault()
                .encrypt(sourceIdBytes, encryptionKey);
        } catch (Exception e) {
            throw new IllegalStateException(
                "can not generate access token for " + source +
                    " id: " + sourceId,
                e
            );
        }
        String header = EzyBase64.encode2utf(encryptedSourceIdBytes);
        String body = EzySHA256.cryptUtfToLowercase(
            String.valueOf(sourceId) + '-'
                + UUID.randomUUID() + '-'
                + System.currentTimeMillis()
        );
        return header + body;
    }

    public static long extractSourceId(
        String accessToken,
        byte[] encryptionKey
    ) {
        if (encryptionKey == null) {
            throw new IllegalStateException("server's not ready yet");
        }
        try {
            String base64Header = accessToken.substring(0, 44);
            byte[] encryptedSourceIdBytes = EzyBase64.decode(base64Header);
            byte[] sourceIdBytes = EzyAesCrypt.getDefault()
                .decrypt(
                    encryptedSourceIdBytes,
                    encryptionKey
                );
            return EzyLongs.bin2long(sourceIdBytes);
        } catch (Exception e) {
            return ZERO_LONG;
        }
    }

    public static String extractBearerToken(String text) {
        if (isBlank(text)) {
            return NULL_STRING;
        }
        int index = text.indexOf(PREFIX_BEARER_TOKEN);
        String accessToken = text;
        if (index >= 0) {
            accessToken = text.substring(PREFIX_BEARER_TOKEN.length());
        }
        return accessToken;
    }
}
