/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.test.validator;

import com.tvd12.test.assertion.Asserts;
import org.apache.tika.config.TikaConfig;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.devtools.InstanceRandom;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.service.MediaService;
import org.youngmonkeys.ezyplatform.service.SettingService;
import org.youngmonkeys.ezyplatform.validator.MediaValidator;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class MediaValidatorTest {

    private TikaConfig tika;
    private MediaService mediaService;
    private SettingService settingService;
    private MediaValidator instance;

    @BeforeMethod
    public void setup() {
        tika = mock(TikaConfig.class);
        mediaService = mock(MediaService.class);
        settingService = mock(SettingService.class);
        instance = new MediaValidator(
            tika,
            mediaService,
            settingService
        );
    }

    @AfterMethod
    public void verifyAll() {
        verifyNoMoreInteractions(
            tika,
            mediaService,
            settingService
        );
    }

    @Test
    public void validateMediaIdTest() {
        // given
        long existingMediaId = 1L;
        MediaModel expected = MediaModel.builder()
            .id(existingMediaId)
            .name("media-1")
            .build();
        when(mediaService.getMediaById(existingMediaId))
            .thenReturn(expected);

        long missingMediaId = 2L;
        when(mediaService.getMediaById(missingMediaId))
            .thenReturn(null);

        // when
        MediaModel actual = instance.validateMediaId(existingMediaId);
        Throwable e = Asserts.assertThrows(() ->
            instance.validateMediaId(missingMediaId)
        );

        // then
        Asserts.assertEquals(actual, expected);
        Asserts.assertEqualsType(e, ResourceNotFoundException.class);
        verify(mediaService).getMediaById(existingMediaId);
        verify(mediaService).getMediaById(missingMediaId);

    }
}
