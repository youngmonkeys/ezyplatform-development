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

package org.youngmonkeys.ezyplatform.test.service;

import com.tvd12.ezyfox.function.EzyExceptionFunction;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.constant.CommonConstants;
import org.youngmonkeys.ezyplatform.service.SettingService;

import java.util.Optional;
import java.util.Set;

public class SettingServiceTest {

    @Test
    public void resolveWebUriTest() {
        // given
        String uri = RandomUtil.randomShortAlphabetString();
        InternalSettingService internalSettingService = new InternalSettingService();

        // when
        String actual = internalSettingService.resolveWebUri(uri);

        // then
        String expectation = CommonConstants.DEFAULT_WEB_URL + "/" + uri;
        Asserts.assertEquals(actual, expectation);
    }

    private static class InternalSettingService implements SettingService {

        @Override
        public void watchLastUpdatedTime(
            String settingName,
            int periodInSecond,
            Runnable onLastUpdatedTimeChange
        ) {}

        @Override
        public void addValueConverter(
            String settingName,
            EzyExceptionFunction<String, Object> converter
        ) {}

        @Override
        public void scheduleCacheValue(String settingName, int periodInSecond) {}

        @Override
        public String getDecryptionValue(String settingName) {
            return null;
        }

        @Override
        public <T> T getCachedValue(String settingName, T defaultValue) {
            return null;
        }

        @Override
        public Optional<String> getSettingValue(String settingName) {
            return Optional.empty();
        }

        @Override
        public long getMaxUploadFileSize() {
            return 0;
        }

        @Override
        public Set<String> getAcceptedMediaMimeTypes() {
            return null;
        }

        @Override
        public String decryptValue(String encryptedSettingValue) {
            return null;
        }

        @Override
        public <T> T getObjectValue(String settingName, Class<T> objectType) {
            return null;
        }
    }
}
