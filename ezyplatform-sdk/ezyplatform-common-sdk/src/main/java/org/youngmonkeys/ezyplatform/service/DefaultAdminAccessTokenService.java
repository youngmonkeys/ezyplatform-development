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

package org.youngmonkeys.ezyplatform.service;

import com.tvd12.ezyfox.security.EzyBase64;
import org.youngmonkeys.ezyplatform.util.AccessTokens;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.SETTING_NAME_ADMIN_ACCESS_TOKEN_ENCRYPTION_KEY;

public class DefaultAdminAccessTokenService
    implements AdminAccessTokenService {

    private static final AtomicBoolean WATCHED =
        new AtomicBoolean();
    private static final AtomicReference<byte[]> ENCRYPTION_KEY =
        new AtomicReference<>();

    public DefaultAdminAccessTokenService(
        SettingService settingService
    ) {
        if (WATCHED.compareAndSet(false, true)) {
            watchLastEncryptionKeyUpdatedTime(settingService);
        }
    }

    private void watchLastEncryptionKeyUpdatedTime(
        SettingService settingService
    ) {
        settingService.watchLastUpdatedTime(
            SETTING_NAME_ADMIN_ACCESS_TOKEN_ENCRYPTION_KEY,
            () -> ENCRYPTION_KEY.set(
                EzyBase64.decode(
                    settingService.getDecryptionValue(
                        SETTING_NAME_ADMIN_ACCESS_TOKEN_ENCRYPTION_KEY
                    )
                )
            )
        );
    }

    @Override
    public String generateAccessToken(long adminId) {
        return AccessTokens.generateAccessToken(
            "admin",
            adminId,
            ENCRYPTION_KEY.get()
        );
    }

    @Override
    public long extractAdminId(String accessToken) {
        return AccessTokens.extractSourceId(
            accessToken,
            ENCRYPTION_KEY.get()
        );
    }
}
