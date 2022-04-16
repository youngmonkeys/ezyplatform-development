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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.sercurity.EzyAesCrypt;
import com.tvd12.ezyfox.sercurity.EzyBase64;
import com.tvd12.test.assertion.Asserts;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.concurrent.Scheduler;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.repo.SettingRepository;
import org.youngmonkeys.ezyplatform.service.DefaultSettingService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.mockito.Mockito.*;
import static org.youngmonkeys.ezyplatform.manager.FileSystemManager.FILE_ENCRYPTION_KEYS;
import static org.youngmonkeys.ezyplatform.manager.FileSystemManager.FOLDER_SETTINGS;

public class DefaultSettingServiceTest {

    @Mock
    private Scheduler scheduler;

    @Mock
    private ObjectMapper objectMapper;

    private FileSystemManager fileSystemManager;

    @Mock
    private SettingRepository settingRepository;

    @InjectMocks
    private InternalSettingService sut;

    private final Path encryptionKeysPath = Paths.get(
        FOLDER_SETTINGS,
        FILE_ENCRYPTION_KEYS
    );

    @BeforeMethod
    public void init() {
        fileSystemManager = mock(FileSystemManager.class);
        when(
            fileSystemManager.concatWithEzyHome(encryptionKeysPath)
        ).thenReturn(encryptionKeysPath.toFile());
        MockitoAnnotations.initMocks(this);
    }

    @AfterTest
    public void afterTest() {
        verify(fileSystemManager, times(1)).concatWithEzyHome(
            encryptionKeysPath
        );
    }

    @Test
    public void decryptValueTest() throws Exception {
        // given
        String text = "Hello World";
        String key = "KSYzjcc8nqrBk8jXtW4QaMpr2suBU9vY"; //sut.getEncryptionKey();
        String encryptedValue = EzyBase64.encode2utf(
            EzyAesCrypt.getDefault()
                .encrypt(text.getBytes(), key.getBytes())
        );
        // when
        String decryptedValue = sut.decryptValue(encryptedValue);

        // then
        Asserts.assertEquals(decryptedValue, text);
        verify(scheduler, times(0)).stop();
        verify(objectMapper, times(0)).writeValueAsString(any());
        verify(settingRepository, times(0)).findById(any());
    }

    private static class InternalSettingService extends DefaultSettingService {

        public InternalSettingService(
            Scheduler scheduler,
            ObjectMapper objectMapper,
            FileSystemManager fileSystemManager,
            SettingRepository settingRepository
        ) {
            super(scheduler, objectMapper, fileSystemManager, settingRepository);
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
        public String getEncryptionKey() {
            return super.getEncryptionKey();
        }
    }
}
