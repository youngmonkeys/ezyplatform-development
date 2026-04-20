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

import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.test.assertion.Asserts;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.request.AddMediaFromUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaIncludeUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaRequest;
import org.youngmonkeys.ezyplatform.service.MediaService;
import org.youngmonkeys.ezyplatform.service.SettingService;
import org.youngmonkeys.ezyplatform.validator.MediaValidator;

import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_MEDIA_ALT_TEXT_LENGTH;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_MEDIA_CAPTION_LENGTH;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_MEDIA_DESCRIPTION_LENGTH;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_MEDIA_ORIGINAL_NAME_LENGTH;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_MEDIA_TITLE_LENGTH;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.MAX_MEDIA_URL_LENGTH;

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

    @Test
    public void validateMediaNameAndGetTest() {
        // given
        String existingMediaName = "media-1";
        MediaModel expected = MediaModel.builder()
            .id(1L)
            .name(existingMediaName)
            .build();
        when(mediaService.getMediaByName(existingMediaName))
            .thenReturn(expected);

        String missingMediaName = "media-2";
        when(mediaService.getMediaByName(missingMediaName))
            .thenReturn(null);

        // when
        MediaModel actual = instance.validateMediaNameAndGet(
            existingMediaName
        );
        Throwable e = Asserts.assertThrows(() ->
            instance.validateMediaNameAndGet(missingMediaName)
        );

        // then
        Asserts.assertEquals(actual, expected);
        Asserts.assertEqualsType(e, ResourceNotFoundException.class);
        verify(mediaService).getMediaByName(existingMediaName);
        verify(mediaService).getMediaByName(missingMediaName);
    }

    @Test
    public void validateFilePartTest() throws Exception {
        // given
        Detector detector = mock(Detector.class);
        when(tika.getDetector()).thenReturn(detector);
        when(settingService.getMaxUploadFileSize()).thenReturn(100L);

        Part validFilePart = mock(Part.class);
        when(validFilePart.getSubmittedFileName()).thenReturn("image.png");
        when(validFilePart.getSize()).thenReturn(80L);
        when(validFilePart.getInputStream()).thenReturn(
            new ByteArrayInputStream(new byte[]{1, 2, 3})
        );

        Part wildcardFilePart = mock(Part.class);
        when(wildcardFilePart.getSubmittedFileName()).thenReturn("video.mp4");
        when(wildcardFilePart.getSize()).thenReturn(90L);
        when(wildcardFilePart.getInputStream()).thenReturn(
            new ByteArrayInputStream(new byte[]{4, 5, 6})
        );

        Part blankFileNamePart = mock(Part.class);
        when(blankFileNamePart.getSubmittedFileName()).thenReturn(" ");
        when(blankFileNamePart.getSize()).thenReturn(80L);
        when(blankFileNamePart.getInputStream()).thenReturn(
            new ByteArrayInputStream(new byte[]{7, 8, 9})
        );

        Part noExtensionPart = mock(Part.class);
        when(noExtensionPart.getSubmittedFileName()).thenReturn("image");
        when(noExtensionPart.getSize()).thenReturn(80L);
        when(noExtensionPart.getInputStream()).thenReturn(
            new ByteArrayInputStream(new byte[]{10, 11, 12})
        );

        Part oversizeFilePart = mock(Part.class);
        when(oversizeFilePart.getSubmittedFileName()).thenReturn("large.png");
        when(oversizeFilePart.getSize()).thenReturn(101L);
        when(oversizeFilePart.getInputStream()).thenReturn(
            new ByteArrayInputStream(new byte[]{13, 14, 15})
        );

        Part unsupportedMimeTypePart = mock(Part.class);
        when(unsupportedMimeTypePart.getSubmittedFileName())
            .thenReturn("file.txt");
        when(unsupportedMimeTypePart.getSize()).thenReturn(80L);
        when(unsupportedMimeTypePart.getInputStream()).thenReturn(
            new ByteArrayInputStream(new byte[]{16, 17, 18})
        );

        when(detector.detect(
            any(),
            any()
        ))
            .thenReturn(org.apache.tika.mime.MediaType.image("png"))
            .thenReturn(org.apache.tika.mime.MediaType.video("mp4"))
            .thenReturn(org.apache.tika.mime.MediaType.image("png"))
            .thenReturn(org.apache.tika.mime.MediaType.image("png"))
            .thenReturn(org.apache.tika.mime.MediaType.image("png"))
            .thenReturn(org.apache.tika.mime.MediaType.text("plain"));

        when(settingService.getAcceptedMediaMimeTypes())
            .thenReturn(Collections.singleton("image/png"))
            .thenReturn(Collections.singleton("*"))
            .thenReturn(Collections.singleton("image/png"))
            .thenReturn(Collections.singleton("image/png"))
            .thenReturn(Collections.singleton("image/png"))
            .thenReturn(Collections.singleton("image/png"));

        // when
        FileMetadata actual = instance.validateFilePart(validFilePart, false);
        FileMetadata wildcardActual = instance.validateFilePart(
            wildcardFilePart,
            true
        );
        Throwable nullFileError = Asserts.assertThrows(() ->
            instance.validateFilePart(null, false)
        );
        Throwable blankFileNameError = Asserts.assertThrows(() ->
            instance.validateFilePart(blankFileNamePart, false)
        );
        Throwable noExtensionError = Asserts.assertThrows(() ->
            instance.validateFilePart(noExtensionPart, false)
        );
        Throwable oversizeError = Asserts.assertThrows(() ->
            instance.validateFilePart(oversizeFilePart, false)
        );
        Throwable unsupportedMimeTypeError = Asserts.assertThrows(() ->
            instance.validateFilePart(unsupportedMimeTypePart, false)
        );

        // then
        Asserts.assertEquals(actual.getMimeType(), "image/png");
        Asserts.assertEquals(actual.getExtension(), "png");
        Asserts.assertEquals(actual.getMediaType(), MediaType.IMAGE);
        Asserts.assertEquals(actual.getFileSize(), 80L);

        Asserts.assertEquals(wildcardActual.getMimeType(), "video/mp4");
        Asserts.assertEquals(wildcardActual.getExtension(), "mp4");
        Asserts.assertEquals(wildcardActual.getMediaType(), MediaType.AVATAR);
        Asserts.assertEquals(wildcardActual.getFileSize(), 90L);

        Asserts.assertEqualsType(nullFileError, HttpBadRequestException.class);
        assertErrorData(
            ((HttpBadRequestException) nullFileError).getData(),
            "file",
            "invalid"
        );

        Asserts.assertEqualsType(
            blankFileNameError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) blankFileNameError).getData(),
            "fileName",
            "invalid"
        );

        Asserts.assertEqualsType(
            noExtensionError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) noExtensionError).getData(),
            "fileType",
            "invalid"
        );

        Asserts.assertEqualsType(oversizeError, HttpBadRequestException.class);
        assertErrorData(
            ((HttpBadRequestException) oversizeError).getData(),
            "uploadSize",
            "over"
        );

        Asserts.assertEqualsType(
            unsupportedMimeTypeError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) unsupportedMimeTypeError).getData(),
            "fileType",
            "invalid"
        );

        verify(tika, times(6)).getDetector();
        verify(detector, times(6)).detect(
            any(),
            any()
        );
        verify(settingService, times(6))
            .getMaxUploadFileSize();
        verify(settingService, times(6))
            .getAcceptedMediaMimeTypes();
    }

    @Test
    public void validateAddMediaFromUrlRequestTest() {
        // given
        AddMediaFromUrlRequest validRequest = new AddMediaFromUrlRequest();
        validRequest.setType(MediaType.IMAGE);
        validRequest.setOriginalName("media-original-name");
        validRequest.setUrl("https://youngmonkeys.org/media/image.png");
        validRequest.setDurationInMinutes(BigDecimal.ONE);

        AddMediaFromUrlRequest missingTypeRequest = new AddMediaFromUrlRequest();
        missingTypeRequest.setOriginalName("media-original-name");
        missingTypeRequest.setUrl("https://youngmonkeys.org/media/image.png");

        AddMediaFromUrlRequest missingOriginalNameRequest =
            new AddMediaFromUrlRequest();
        missingOriginalNameRequest.setType(MediaType.IMAGE);
        missingOriginalNameRequest.setUrl(
            "https://youngmonkeys.org/media/image.png"
        );

        AddMediaFromUrlRequest overLengthOriginalNameRequest =
            new AddMediaFromUrlRequest();
        overLengthOriginalNameRequest.setType(MediaType.IMAGE);
        overLengthOriginalNameRequest.setOriginalName(
            randomString(MAX_MEDIA_ORIGINAL_NAME_LENGTH + 1)
        );
        overLengthOriginalNameRequest.setUrl(
            "https://youngmonkeys.org/media/image.png"
        );

        AddMediaFromUrlRequest missingUrlRequest = new AddMediaFromUrlRequest();
        missingUrlRequest.setType(MediaType.IMAGE);
        missingUrlRequest.setOriginalName("media-original-name");

        AddMediaFromUrlRequest invalidUrlRequest = new AddMediaFromUrlRequest();
        invalidUrlRequest.setType(MediaType.IMAGE);
        invalidUrlRequest.setOriginalName("media-original-name");
        invalidUrlRequest.setUrl("https://young monkeys.org/media/image.png");

        AddMediaFromUrlRequest overLengthUrlRequest =
            new AddMediaFromUrlRequest();
        overLengthUrlRequest.setType(MediaType.IMAGE);
        overLengthUrlRequest.setOriginalName("media-original-name");
        overLengthUrlRequest.setUrl(
            "https://youngmonkeys.org/" + randomString(MAX_MEDIA_URL_LENGTH)
        );

        AddMediaFromUrlRequest invalidDurationRequest =
            new AddMediaFromUrlRequest();
        invalidDurationRequest.setType(MediaType.IMAGE);
        invalidDurationRequest.setOriginalName("media-original-name");
        invalidDurationRequest.setUrl(
            "https://youngmonkeys.org/media/image.png"
        );
        invalidDurationRequest.setDurationInMinutes(
            BigDecimal.valueOf(-1)
        );

        // when
        instance.validate(validRequest);
        Throwable missingTypeError = Asserts.assertThrows(() ->
            instance.validate(missingTypeRequest)
        );
        Throwable missingOriginalNameError = Asserts.assertThrows(() ->
            instance.validate(missingOriginalNameRequest)
        );
        Throwable overLengthOriginalNameError = Asserts.assertThrows(() ->
            instance.validate(overLengthOriginalNameRequest)
        );
        Throwable missingUrlError = Asserts.assertThrows(() ->
            instance.validate(missingUrlRequest)
        );
        Throwable invalidUrlError = Asserts.assertThrows(() ->
            instance.validate(invalidUrlRequest)
        );
        Throwable overLengthUrlError = Asserts.assertThrows(() ->
            instance.validate(overLengthUrlRequest)
        );
        Throwable invalidDurationError = Asserts.assertThrows(() ->
            instance.validate(invalidDurationRequest)
        );

        // then
        Asserts.assertEqualsType(missingTypeError, HttpBadRequestException.class);
        assertErrorData(
            ((HttpBadRequestException) missingTypeError).getData(),
            "type",
            "required"
        );

        Asserts.assertEqualsType(
            missingOriginalNameError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) missingOriginalNameError).getData(),
            "originalName",
            "required"
        );

        Asserts.assertEqualsType(
            overLengthOriginalNameError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthOriginalNameError).getData(),
            "originalName",
            "overLength"
        );

        Asserts.assertEqualsType(missingUrlError, HttpBadRequestException.class);
        assertErrorData(
            ((HttpBadRequestException) missingUrlError).getData(),
            "url",
            "required"
        );

        Asserts.assertEqualsType(invalidUrlError, HttpBadRequestException.class);
        assertErrorData(
            ((HttpBadRequestException) invalidUrlError).getData(),
            "url",
            "invalid"
        );

        Asserts.assertEqualsType(
            overLengthUrlError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthUrlError).getData(),
            "url",
            "overLength"
        );

        Asserts.assertEqualsType(
            invalidDurationError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) invalidDurationError).getData(),
            "durationInMinutes",
            "invalid"
        );
    }

    @Test
    public void validateUpdateMediaRequestTest() {
        // given
        UpdateMediaRequest validRequest = new UpdateMediaRequest();
        validRequest.setAlternativeText("alt-text");
        validRequest.setTitle("title");
        validRequest.setCaption("caption");
        validRequest.setDescription("description");

        UpdateMediaRequest overLengthAlternativeTextRequest =
            new UpdateMediaRequest();
        overLengthAlternativeTextRequest.setAlternativeText(
            randomString(MAX_MEDIA_ALT_TEXT_LENGTH + 1)
        );

        UpdateMediaRequest overLengthTitleRequest =
            new UpdateMediaRequest();
        overLengthTitleRequest.setTitle(
            randomString(MAX_MEDIA_TITLE_LENGTH + 1)
        );

        UpdateMediaRequest overLengthCaptionRequest =
            new UpdateMediaRequest();
        overLengthCaptionRequest.setCaption(
            randomString(MAX_MEDIA_CAPTION_LENGTH + 1)
        );

        UpdateMediaRequest overLengthDescriptionRequest =
            new UpdateMediaRequest();
        overLengthDescriptionRequest.setDescription(
            randomString(MAX_MEDIA_DESCRIPTION_LENGTH + 1)
        );

        // when
        instance.validate(validRequest);
        Throwable overLengthAlternativeTextError = Asserts.assertThrows(() ->
            instance.validate(overLengthAlternativeTextRequest)
        );
        Throwable overLengthTitleError = Asserts.assertThrows(() ->
            instance.validate(overLengthTitleRequest)
        );
        Throwable overLengthCaptionError = Asserts.assertThrows(() ->
            instance.validate(overLengthCaptionRequest)
        );
        Throwable overLengthDescriptionError = Asserts.assertThrows(() ->
            instance.validate(overLengthDescriptionRequest)
        );

        // then
        Asserts.assertEqualsType(
            overLengthAlternativeTextError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthAlternativeTextError)
                .getData(),
            "alternativeText",
            "overLength"
        );

        Asserts.assertEqualsType(
            overLengthTitleError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthTitleError).getData(),
            "title",
            "overLength"
        );

        Asserts.assertEqualsType(
            overLengthCaptionError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthCaptionError).getData(),
            "caption",
            "overLength"
        );

        Asserts.assertEqualsType(
            overLengthDescriptionError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthDescriptionError).getData(),
            "description",
            "overLength"
        );
    }

    @Test
    public void validateUpdateMediaIncludeUrlRequestTest() {
        // given
        UpdateMediaIncludeUrlRequest validRequest =
            new UpdateMediaIncludeUrlRequest();
        validRequest.setAlternativeText("alt-text");
        validRequest.setTitle("title");
        validRequest.setCaption("caption");
        validRequest.setDescription("description");
        validRequest.setUrl("https://youngmonkeys.org/media/image.png");
        validRequest.setUpdateDuration(true);
        validRequest.setDurationInMinutes(BigDecimal.ZERO);

        UpdateMediaIncludeUrlRequest overLengthAlternativeTextRequest =
            new UpdateMediaIncludeUrlRequest();
        overLengthAlternativeTextRequest.setAlternativeText(
            randomString(MAX_MEDIA_ALT_TEXT_LENGTH + 1)
        );

        UpdateMediaIncludeUrlRequest overLengthTitleRequest =
            new UpdateMediaIncludeUrlRequest();
        overLengthTitleRequest.setTitle(
            randomString(MAX_MEDIA_TITLE_LENGTH + 1)
        );

        UpdateMediaIncludeUrlRequest overLengthCaptionRequest =
            new UpdateMediaIncludeUrlRequest();
        overLengthCaptionRequest.setCaption(
            randomString(MAX_MEDIA_CAPTION_LENGTH + 1)
        );

        UpdateMediaIncludeUrlRequest overLengthDescriptionRequest =
            new UpdateMediaIncludeUrlRequest();
        overLengthDescriptionRequest.setDescription(
            randomString(MAX_MEDIA_DESCRIPTION_LENGTH + 1)
        );

        UpdateMediaIncludeUrlRequest invalidUrlRequest =
            new UpdateMediaIncludeUrlRequest();
        invalidUrlRequest.setUrl(
            "https://young monkeys.org/media/image.png"
        );

        UpdateMediaIncludeUrlRequest overLengthUrlRequest =
            new UpdateMediaIncludeUrlRequest();
        overLengthUrlRequest.setUrl(
            "https://youngmonkeys.org/" + randomString(MAX_MEDIA_URL_LENGTH)
        );

        UpdateMediaIncludeUrlRequest invalidDurationRequest =
            new UpdateMediaIncludeUrlRequest();
        invalidDurationRequest.setUpdateDuration(true);
        invalidDurationRequest.setDurationInMinutes(BigDecimal.valueOf(-1));

        // when
        instance.validate(validRequest);
        Throwable overLengthAlternativeTextError = Asserts.assertThrows(() ->
            instance.validate(overLengthAlternativeTextRequest)
        );
        Throwable overLengthTitleError = Asserts.assertThrows(() ->
            instance.validate(overLengthTitleRequest)
        );
        Throwable overLengthCaptionError = Asserts.assertThrows(() ->
            instance.validate(overLengthCaptionRequest)
        );
        Throwable overLengthDescriptionError = Asserts.assertThrows(() ->
            instance.validate(overLengthDescriptionRequest)
        );
        Throwable invalidUrlError = Asserts.assertThrows(() ->
            instance.validate(invalidUrlRequest)
        );
        Throwable overLengthUrlError = Asserts.assertThrows(() ->
            instance.validate(overLengthUrlRequest)
        );
        Throwable invalidDurationError = Asserts.assertThrows(() ->
            instance.validate(invalidDurationRequest)
        );

        // then
        Asserts.assertEqualsType(
            overLengthAlternativeTextError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthAlternativeTextError)
                .getData(),
            "alternativeText",
            "overLength"
        );

        Asserts.assertEqualsType(
            overLengthTitleError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthTitleError).getData(),
            "title",
            "overLength"
        );

        Asserts.assertEqualsType(
            overLengthCaptionError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthCaptionError).getData(),
            "caption",
            "overLength"
        );

        Asserts.assertEqualsType(
            overLengthDescriptionError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthDescriptionError).getData(),
            "description",
            "overLength"
        );

        Asserts.assertEqualsType(invalidUrlError, HttpBadRequestException.class);
        assertErrorData(
            ((HttpBadRequestException) invalidUrlError).getData(),
            "url",
            "invalid"
        );

        Asserts.assertEqualsType(
            overLengthUrlError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) overLengthUrlError).getData(),
            "url",
            "overLength"
        );

        Asserts.assertEqualsType(
            invalidDurationError,
            HttpBadRequestException.class
        );
        assertErrorData(
            ((HttpBadRequestException) invalidDurationError).getData(),
            "durationInMinutes",
            "invalid"
        );
    }

    @SuppressWarnings("unchecked")
    private void assertErrorData(
        Object actual,
        String expectedKey,
        String expectedValue
    ) {
        Map<String, String> errors = (Map<String, String>) actual;
        Asserts.assertEquals(errors.get(expectedKey), expectedValue);
    }

    private String randomString(int length) {
        StringBuilder builder = new StringBuilder(length);
        while (builder.length() < length) {
            builder.append('a');
        }
        return builder.substring(0, length);
    }

    @Test
    public void validateOwnerAdminMediaTest() {
        // given
        long adminId = 1L;
        long ownedMediaId = 2L;
        long notOwnedMediaId = 3L;
        when(mediaService.getOwnerAdminIdByMediaId(ownedMediaId))
            .thenReturn(adminId);
        when(mediaService.getOwnerAdminIdByMediaId(notOwnedMediaId))
            .thenReturn(adminId + 1);

        // when
        instance.validateOwnerAdminMedia(adminId, ownedMediaId);
        Throwable e = Asserts.assertThrows(() ->
            instance.validateOwnerAdminMedia(adminId, notOwnedMediaId)
        );

        // then
        Asserts.assertEqualsType(e, MediaNotFoundException.class);
        verify(mediaService).getOwnerAdminIdByMediaId(ownedMediaId);
        verify(mediaService).getOwnerAdminIdByMediaId(notOwnedMediaId);
    }

    @Test
    public void validateOwnerAdminMediaNameTest() {
        // given
        long adminId = 1L;
        String ownedMediaName = "media-1";
        String notOwnedMediaName = "media-2";
        when(mediaService.getOwnerAdminIdByMediaName(ownedMediaName))
            .thenReturn(adminId);
        when(mediaService.getOwnerAdminIdByMediaName(notOwnedMediaName))
            .thenReturn(adminId + 1);

        // when
        instance.validateOwnerAdminMedia(adminId, ownedMediaName);
        Throwable e = Asserts.assertThrows(() ->
            instance.validateOwnerAdminMedia(adminId, notOwnedMediaName)
        );

        // then
        Asserts.assertEqualsType(e, MediaNotFoundException.class);
        verify(mediaService).getOwnerAdminIdByMediaName(ownedMediaName);
        verify(mediaService).getOwnerAdminIdByMediaName(notOwnedMediaName);
    }

    @Test
    public void validateOwnerUserMediaIdTest() {
        // given
        long userId = 1L;
        long ownedMediaId = 2L;
        long notOwnedMediaId = 3L;
        when(mediaService.getOwnerUserIdByMediaId(ownedMediaId))
            .thenReturn(userId);
        when(mediaService.getOwnerUserIdByMediaId(notOwnedMediaId))
            .thenReturn(userId + 1);

        // when
        instance.validateOwnerUserMedia(userId, ownedMediaId);
        Throwable e = Asserts.assertThrows(() ->
            instance.validateOwnerUserMedia(userId, notOwnedMediaId)
        );

        // then
        Asserts.assertEqualsType(e, MediaNotFoundException.class);
        verify(mediaService).getOwnerUserIdByMediaId(ownedMediaId);
        verify(mediaService).getOwnerUserIdByMediaId(notOwnedMediaId);
    }

    @Test
    public void validateOwnerUserMediaNameTest() {
        // given
        long userId = 1L;
        String ownedMediaName = "media-1";
        String notOwnedMediaName = "media-2";
        when(mediaService.getOwnerUserIdByMediaName(ownedMediaName))
            .thenReturn(userId);
        when(mediaService.getOwnerUserIdByMediaName(notOwnedMediaName))
            .thenReturn(userId + 1);

        // when
        instance.validateOwnerUserMedia(userId, ownedMediaName);
        Throwable e = Asserts.assertThrows(() ->
            instance.validateOwnerUserMedia(userId, notOwnedMediaName)
        );

        // then
        Asserts.assertEqualsType(e, MediaNotFoundException.class);
        verify(mediaService).getOwnerUserIdByMediaName(ownedMediaName);
        verify(mediaService).getOwnerUserIdByMediaName(notOwnedMediaName);
    }

    @Test
    public void validateUserMediaTest() {
        // given
        long userId = 1L;
        long ownedMediaId = 2L;
        long notOwnedMediaId = 3L;
        MediaModel ownedMedia = MediaModel.builder()
            .id(ownedMediaId)
            .name("media-1")
            .ownerUserId(userId)
            .build();
        MediaModel notOwnedMedia = MediaModel.builder()
            .id(notOwnedMediaId)
            .name("media-2")
            .ownerUserId(userId + 1)
            .build();
        when(mediaService.getMediaById(ownedMediaId))
            .thenReturn(ownedMedia);
        when(mediaService.getMediaById(notOwnedMediaId))
            .thenReturn(notOwnedMedia);

        // when
        MediaModel actual = instance.validateUserMedia(userId, ownedMediaId);
        Throwable e = Asserts.assertThrows(() ->
            instance.validateUserMedia(userId, notOwnedMediaId)
        );

        // then
        Asserts.assertEquals(actual, ownedMedia);
        Asserts.assertEqualsType(e, ResourceNotFoundException.class);
        verify(mediaService).getMediaById(ownedMediaId);
        verify(mediaService).getMediaById(notOwnedMediaId);
    }
}
