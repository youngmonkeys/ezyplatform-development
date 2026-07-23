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
import com.tvd12.ezyfox.security.EzyAesCrypt;
import com.tvd12.ezyfox.security.EzyBase64;
import com.tvd12.test.assertion.Asserts;
import com.tvd12.test.util.RandomUtil;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.concurrent.Scheduler;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.entity.Setting;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.repo.SettingRepository;
import org.youngmonkeys.ezyplatform.service.DefaultSettingService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
        // force @InjectMocks to build a brand new sut via the constructor
        // instead of reflecting fresh mocks into the instance from a
        // previous test method (TestNG reuses one test class instance)
        sut = null;
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

    @Test
    public void watchLastUpdatedTimeWithCallImmediatelyNotifiesExistingChangeTest() {
        // given
        String settingName = RandomUtil.randomShortAlphabetString();
        mockPersistedLastUpdatedTime(settingName, 100L);
        Runnable onLastUpdatedTimeChange = mock(Runnable.class);
        ArgumentCaptor<Runnable> onStartedCaptor =
            ArgumentCaptor.forClass(Runnable.class);

        // when
        sut.watchLastUpdatedTime(
            settingName,
            5,
            onLastUpdatedTimeChange,
            true
        );
        verify(scheduler, times(1)).onStarted(onStartedCaptor.capture());
        // simulates the scheduler firing the listener right at startup
        onStartedCaptor.getValue().run();

        // then: a change that already happened before watching is reported
        // immediately, as the caller asked to be notified right away
        verify(onLastUpdatedTimeChange, times(1)).run();
    }

    @Test
    public void watchLastUpdatedTimeWithoutCallImmediatelyDoesNotReplayExistingChangeTest() {
        // given
        String settingName = RandomUtil.randomShortAlphabetString();
        mockPersistedLastUpdatedTime(settingName, 100L);
        Runnable onLastUpdatedTimeChange = mock(Runnable.class);
        ArgumentCaptor<Runnable> scheduledCaptor =
            ArgumentCaptor.forClass(Runnable.class);

        // when
        sut.watchLastUpdatedTime(
            settingName,
            5,
            onLastUpdatedTimeChange,
            false
        );
        verify(scheduler, times(0)).onStarted(any());
        verify(scheduler, times(1)).scheduleAtFixRate(
            scheduledCaptor.capture(),
            eq(5L),
            eq(5L),
            eq(TimeUnit.SECONDS)
        );
        // simulates the first periodic tick, value is unchanged since watching started
        scheduledCaptor.getValue().run();

        // then: the pre-existing change must not be replayed, only changes
        // happening after the watch was registered should be reported
        verify(onLastUpdatedTimeChange, times(0)).run();
    }

    @Test
    public void watchLastUpdatedTimeWithoutCallImmediatelyStillReportsFutureChangeTest() {
        // given
        String settingName = RandomUtil.randomShortAlphabetString();
        mockPersistedLastUpdatedTime(settingName, 100L);
        Runnable onLastUpdatedTimeChange = mock(Runnable.class);
        ArgumentCaptor<Runnable> scheduledCaptor =
            ArgumentCaptor.forClass(Runnable.class);
        sut.watchLastUpdatedTime(
            settingName,
            5,
            onLastUpdatedTimeChange,
            false
        );
        verify(scheduler).scheduleAtFixRate(
            scheduledCaptor.capture(),
            eq(5L),
            eq(5L),
            eq(TimeUnit.SECONDS)
        );

        // when: the setting genuinely changes after the watch was registered
        mockPersistedLastUpdatedTime(settingName, 200L);
        scheduledCaptor.getValue().run();

        // then
        verify(onLastUpdatedTimeChange, times(1)).run();
    }

    @Test
    public void watchLastUpdatedTimeWithoutCallImmediatelyReportsFirstEverChangeTest() {
        // given: the setting has never been changed before (fresh install,
        // no "_last_updated_time" row exists yet)
        String settingName = RandomUtil.randomShortAlphabetString();
        when(
            settingRepository.findByFieldOptional(
                "name",
                settingName + DefaultSettingService.LAST_UPDATE_TIME_SUFFIX
            )
        ).thenReturn(Optional.empty());
        Runnable onLastUpdatedTimeChange = mock(Runnable.class);
        ArgumentCaptor<Runnable> scheduledCaptor =
            ArgumentCaptor.forClass(Runnable.class);
        sut.watchLastUpdatedTime(
            settingName,
            5,
            onLastUpdatedTimeChange,
            false
        );
        verify(scheduler).scheduleAtFixRate(
            scheduledCaptor.capture(),
            eq(5L),
            eq(5L),
            eq(TimeUnit.SECONDS)
        );

        // when: the very first real change happens after the watch was registered
        mockPersistedLastUpdatedTime(settingName, 50L);
        scheduledCaptor.getValue().run();

        // then: it must still be reported, even though the baseline was zero
        verify(onLastUpdatedTimeChange, times(1)).run();
    }

    private void mockPersistedLastUpdatedTime(
        String settingName,
        long lastUpdatedTime
    ) {
        Setting setting = new Setting();
        setting.setValue(Long.toString(lastUpdatedTime));
        when(
            settingRepository.findByFieldOptional(
                "name",
                settingName + DefaultSettingService.LAST_UPDATE_TIME_SUFFIX
            )
        ).thenReturn(Optional.of(setting));
    }

    private static class InternalSettingService extends DefaultSettingService {

        public InternalSettingService(
            Scheduler scheduler,
            ObjectMapper objectMapper,
            FileSystemManager fileSystemManager,
            SettingRepository settingRepository,
            DefaultEntityToModelConverter entityToModelConverter
        ) {
            super(
                scheduler,
                objectMapper,
                fileSystemManager,
                settingRepository,
                entityToModelConverter
            );
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
