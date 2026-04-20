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

package org.youngmonkeys.ezyplatform.test.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;
import com.tvd12.ezyfox.concurrent.callback.EzyResultCallback;
import com.tvd12.ezyfox.function.EzyExceptionVoid;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.data.DownloadFileResult;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.resources.FileUploader;
import com.tvd12.test.assertion.Asserts;
import org.apache.tika.config.TikaConfig;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.slf4j.Logger;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.controller.service.MediaControllerService;
import org.youngmonkeys.ezyplatform.converter.DefaultEntityToModelConverter;
import org.youngmonkeys.ezyplatform.converter.HttpModelToResponseConverter;
import org.youngmonkeys.ezyplatform.converter.HttpRequestToModelConverter;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.entity.UploadAction;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;
import org.youngmonkeys.ezyplatform.event.EventHandlerManager;
import org.youngmonkeys.ezyplatform.event.GetMediaFilePathEvent;
import org.youngmonkeys.ezyplatform.event.MediaDownloadEvent;
import org.youngmonkeys.ezyplatform.event.MediaRemovedEvent;
import org.youngmonkeys.ezyplatform.event.MediaUpdatedEvent;
import org.youngmonkeys.ezyplatform.event.MediaUploadEvent;
import org.youngmonkeys.ezyplatform.event.MediaUploadedEvent;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.media.MediaDownloadArguments;
import org.youngmonkeys.ezyplatform.media.MediaUpDownloader;
import org.youngmonkeys.ezyplatform.media.MediaUpDownloaderManager;
import org.youngmonkeys.ezyplatform.media.MediaUploadArguments;
import org.youngmonkeys.ezyplatform.media.MediaUploadFromUrlArguments;
import org.youngmonkeys.ezyplatform.model.AddMediaModel;
import org.youngmonkeys.ezyplatform.model.MediaDetailsModel;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.model.ReplaceMediaModel;
import org.youngmonkeys.ezyplatform.model.UpdateMediaModel;
import org.youngmonkeys.ezyplatform.pagination.MediaFilter;
import org.youngmonkeys.ezyplatform.pagination.MediaPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.repo.PaginationMediaRepository;
import org.youngmonkeys.ezyplatform.request.UpdateMediaIncludeUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaRequest;
import org.youngmonkeys.ezyplatform.response.MediaResponse;
import org.youngmonkeys.ezyplatform.service.MediaService;
import org.youngmonkeys.ezyplatform.service.PaginationMediaService;
import org.youngmonkeys.ezyplatform.service.SettingService;
import org.youngmonkeys.ezyplatform.validator.CommonValidator;
import org.youngmonkeys.ezyplatform.validator.MediaValidator;

import javax.servlet.AsyncContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class MediaControllerServiceTest {
    private HttpClient httpClient;
    private EventHandlerManager eventHandlerManager;
    private FileSystemManager fileSystemManager;
    private EzyInputStreamLoader inputStreamLoader;
    private MediaUpDownloaderManager mediaUpDownloaderManager;
    private ObjectMapper objectMapper;
    private ResourceDownloadManager resourceDownloadManager;
    private EzySingletonFactory singletonFactory;
    private MediaService mediaService;
    private PaginationMediaService paginationMediaService;
    private SettingService settingService;
    private CommonValidator commonValidator;
    private MediaValidator mediaValidator;
    private MediaPaginationParameterConverter mediaPaginationParameterConverter;
    private HttpModelToResponseConverter modelToResponseConverter;
    private HttpRequestToModelConverter requestToModelConverter;
    private EzyLazyInitializer<FileUploader> fileUploaderWrapper;
    private EzyLazyInitializer<TikaConfig> tika;
    private Logger logger;
    private MediaControllerService instance;

    @SuppressWarnings("unchecked")
    @BeforeMethod
    public void setup() {
        httpClient = mock(HttpClient.class);
        eventHandlerManager = mock(EventHandlerManager.class);
        fileSystemManager = mock(FileSystemManager.class);
        inputStreamLoader = mock(EzyInputStreamLoader.class);
        mediaUpDownloaderManager = mock(MediaUpDownloaderManager.class);
        objectMapper = mock(ObjectMapper.class);
        resourceDownloadManager = mock(ResourceDownloadManager.class);
        singletonFactory = mock(EzySingletonFactory.class);
        mediaService = mock(MediaService.class);
        paginationMediaService = mock(PaginationMediaService.class);
        settingService = mock(SettingService.class);
        commonValidator = mock(CommonValidator.class);
        mediaValidator = mock(MediaValidator.class);
        mediaPaginationParameterConverter = mock(MediaPaginationParameterConverter.class);
        modelToResponseConverter = mock(HttpModelToResponseConverter.class);
        requestToModelConverter = mock(HttpRequestToModelConverter.class);
        fileUploaderWrapper = mock(EzyLazyInitializer.class);
        tika = mock(EzyLazyInitializer.class);
        logger = mock(Logger.class);
        instance = new MediaControllerService(
            httpClient,
            eventHandlerManager,
            fileSystemManager,
            inputStreamLoader,
            mediaUpDownloaderManager,
            objectMapper,
            resourceDownloadManager,
            singletonFactory,
            mediaService,
            paginationMediaService,
            settingService,
            commonValidator,
            mediaValidator,
            mediaPaginationParameterConverter,
            modelToResponseConverter,
            requestToModelConverter
        );
    }

    @AfterMethod
    public void verifyAll() {
        verifyNoMoreInteractions(
            httpClient,
            eventHandlerManager,
            fileSystemManager,
            inputStreamLoader,
            mediaUpDownloaderManager,
            objectMapper,
            resourceDownloadManager,
            mediaService,
            paginationMediaService,
            settingService,
            commonValidator,
            mediaValidator,
            mediaPaginationParameterConverter,
            modelToResponseConverter,
            requestToModelConverter,
            fileUploaderWrapper,
            tika,
            logger
        );
    }

    @Test
    public void addMediaFromRequestUseMediaUpDownloaderWhenSupportedTest()
        throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FileUploader fileUploader = mock(FileUploader.class);
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        when(settingService.getMaxUploadFileSize()).thenReturn(512L);
        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(singletonFactory.getSingletonCast(FileUploader.class))
            .thenReturn(fileUploader);
        when(mediaUpDownloader.isUploadSupported()).thenReturn(true);

        // when
        instance.addMedia(
            request,
            response,
            "s3",
            11L,
            22L,
            true,
            false
        );

        // then
        ArgumentCaptor<MediaUploadArguments> argumentsCaptor =
            ArgumentCaptor.forClass(MediaUploadArguments.class);
        verify(settingService).getMaxUploadFileSize();
        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(singletonFactory).getSingletonCast(FileUploader.class);
        verify(mediaUpDownloader).isUploadSupported();
        verify(mediaUpDownloader).upload(argumentsCaptor.capture());

        MediaUploadArguments actual = argumentsCaptor.getValue();
        Asserts.assertEquals(actual.getRequest(), request);
        Asserts.assertEquals(actual.getResponse(), response);
        Asserts.assertEquals(actual.getUploadFrom(), "s3");
        Asserts.assertEquals(actual.getAction(), UploadAction.ADD);
        Asserts.assertEquals(actual.getOwnerAdminId(), 11L);
        Asserts.assertEquals(actual.getOwnerUserId(), 22L);
        Asserts.assertTrue(actual.isAvatar());
        Asserts.assertTrue(!actual.isNotPublic());
        Asserts.assertEquals(actual.getFileUploader(), fileUploader);
        Asserts.assertTrue(actual.getTika() != null);

        verifyNoMoreInteractions(
            request,
            response,
            mediaUpDownloader,
            fileUploader,
            singletonFactory
        );
    }

    @Test
    public void addMediaFromRequestTest() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AsyncContext asyncContext = mock(AsyncContext.class);
        Part filePart = mock(Part.class);
        FileUploader fileUploader = mock(FileUploader.class);
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        CapturingServletOutputStream outputStream =
            new CapturingServletOutputStream();
        FileMetadata fileMetadata = FileMetadata.builder()
            .mimeType("image/png")
            .extension("png")
            .mediaType(MediaType.IMAGE)
            .fileSize(123L)
            .build();
        MediaModel mediaModel = MediaModel.builder()
            .id(99L)
            .name("stored-file.png")
            .build();
        File mediaFilePath = File.createTempFile("media-controller-", ".png");
        long maxFileSize = 2048L;
        String json = "{\"id\":99}";
        mediaFilePath.deleteOnExit();

        when(settingService.getMaxUploadFileSize()).thenReturn(maxFileSize);
        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(singletonFactory.getSingletonCast(FileUploader.class))
            .thenReturn(fileUploader);
        when(mediaUpDownloader.isUploadSupported()).thenReturn(false);
        when(request.getPart("file")).thenReturn(filePart);
        when(mediaValidator.validateFilePart(filePart, false))
            .thenReturn(fileMetadata);
        when(filePart.getSubmittedFileName()).thenReturn("banner.png");
        when(mediaService.generateMediaFileName("banner.png", "png"))
            .thenReturn("stored-file.png");
        when(request.getAsyncContext()).thenReturn(asyncContext);
        when(
            fileSystemManager.getMediaFilePath(
                MediaType.IMAGE.getFolder(),
                "stored-file.png"
            )
        ).thenReturn(mediaFilePath);
        when(mediaService.addMedia(eq("local"), any(AddMediaModel.class)))
            .thenReturn(mediaModel);
        when(objectMapper.writeValueAsString(mediaModel)).thenReturn(json);
        when(response.getOutputStream()).thenReturn(outputStream);
        doAnswer(answer -> {
            Object[] arguments = answer.getArguments();
            File outputFile = (File) arguments[2];
            EzyExceptionVoid callback = (EzyExceptionVoid) arguments[4];
            Files.write(
                outputFile.toPath(),
                "content".getBytes(StandardCharsets.UTF_8)
            );
            callback.apply();
            return null;
        })
            .when(fileUploader)
            .accept(
                same(asyncContext),
                same(filePart),
                same(mediaFilePath),
                eq(maxFileSize),
                any(EzyExceptionVoid.class)
            );

        // when
        instance.addMedia(
            request,
            response,
            "local",
            101L,
            202L,
            false,
            true
        );

        // then
        ArgumentCaptor<AddMediaModel> addMediaCaptor =
            ArgumentCaptor.forClass(AddMediaModel.class);
        ArgumentCaptor<Object> eventCaptor =
            ArgumentCaptor.forClass(Object.class);

        verify(settingService).getMaxUploadFileSize();
        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(singletonFactory).getSingletonCast(FileUploader.class);
        verify(mediaUpDownloader).isUploadSupported();
        verify(request).getPart("file");
        verify(mediaValidator).validateFilePart(filePart, false);
        verify(eventHandlerManager, times(2)).handleEvent(eventCaptor.capture());
        verify(filePart).getSubmittedFileName();
        verify(mediaService).generateMediaFileName("banner.png", "png");
        verify(request).getAsyncContext();
        verify(fileSystemManager).getMediaFilePath(
            MediaType.IMAGE.getFolder(),
            "stored-file.png"
        );
        verify(fileUploader).accept(
            same(asyncContext),
            same(filePart),
            same(mediaFilePath),
            eq(maxFileSize),
            any(EzyExceptionVoid.class)
        );
        verify(mediaService).addMedia(eq("local"), addMediaCaptor.capture());
        verify(objectMapper).writeValueAsString(mediaModel);
        verify(response).getOutputStream();

        List<Object> events = eventCaptor.getAllValues();
        Asserts.assertEquals(events.size(), 2);
        Asserts.assertTrue(events.get(0) instanceof MediaUploadEvent);
        Asserts.assertTrue(events.get(1) instanceof MediaUploadedEvent);

        MediaUploadEvent uploadEvent = (MediaUploadEvent) events.get(0);
        Asserts.assertEquals(uploadEvent.getUploadFrom(), "local");
        Asserts.assertEquals(uploadEvent.getOwnerAdminId(), 101L);
        Asserts.assertEquals(uploadEvent.getOwnerUserId(), 202L);
        Asserts.assertEquals(uploadEvent.getFileMetadata(), fileMetadata);

        AddMediaModel addMediaModel = addMediaCaptor.getValue();
        Asserts.assertEquals(addMediaModel.getOwnerAdminId(), 101L);
        Asserts.assertEquals(addMediaModel.getOwnerUserId(), 202L);
        Asserts.assertEquals(addMediaModel.getFileName(), "stored-file.png");
        Asserts.assertEquals(addMediaModel.getOriginalFileName(), "banner.png");
        Asserts.assertEquals(addMediaModel.getMediaType(), "IMAGE");
        Asserts.assertEquals(addMediaModel.getMimeType(), "image/png");
        Asserts.assertEquals(addMediaModel.getFileSize(), 7L);
        Asserts.assertTrue(addMediaModel.isNotPublic());

        MediaUploadedEvent uploadedEvent = (MediaUploadedEvent) events.get(1);
        Asserts.assertEquals(uploadedEvent.getMedia(), mediaModel);
        Asserts.assertEquals(uploadedEvent.getMediaFilePath(), mediaFilePath);
        Asserts.assertEquals(outputStream.asString(), json);

        InOrder inOrder = inOrder(
            mediaValidator,
            eventHandlerManager,
            fileUploader,
            mediaService
        );
        inOrder.verify(mediaValidator).validateFilePart(filePart, false);
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUploadEvent.class));
        inOrder.verify(fileUploader).accept(
            same(asyncContext),
            same(filePart),
            same(mediaFilePath),
            eq(maxFileSize),
            any(EzyExceptionVoid.class)
        );
        inOrder.verify(mediaService).addMedia(eq("local"), any(AddMediaModel.class));
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUploadedEvent.class));

        verifyNoMoreInteractions(
            request,
            response,
            asyncContext,
            filePart,
            mediaUpDownloader,
            fileUploader,
            singletonFactory
        );
    }

    @Test
    public void replaceMediaFromRequestUseMediaUpDownloaderWhenSupportedTest()
        throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FileUploader fileUploader = mock(FileUploader.class);
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        MediaModel media = MediaModel.builder()
            .id(456L)
            .name("avatar.png")
            .uploadFrom("s3")
            .type(MediaType.AVATAR)
            .ownerAdminId(11L)
            .ownerUserId(22L)
            .publicMedia(false)
            .build();
        when(mediaService.getMediaById(456L)).thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(singletonFactory.getSingletonCast(FileUploader.class))
            .thenReturn(fileUploader);
        when(mediaUpDownloader.isUploadSupported()).thenReturn(true);

        // when
        instance.replaceMedia(
            request,
            response,
            456L,
            validMediaCondition
        );

        // then
        ArgumentCaptor<MediaUploadArguments> argumentsCaptor =
            ArgumentCaptor.forClass(MediaUploadArguments.class);
        verify(mediaService).getMediaById(456L);
        verify(validMediaCondition).test(media);
        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(singletonFactory).getSingletonCast(FileUploader.class);
        verify(mediaUpDownloader).isUploadSupported();
        verify(mediaUpDownloader).upload(argumentsCaptor.capture());

        MediaUploadArguments actual = argumentsCaptor.getValue();
        Asserts.assertEquals(actual.getRequest(), request);
        Asserts.assertEquals(actual.getResponse(), response);
        Asserts.assertEquals(actual.getUploadFrom(), "s3");
        Asserts.assertEquals(actual.getAction(), UploadAction.REPLACE);
        Asserts.assertEquals(actual.getMediaId(), 456L);
        Asserts.assertEquals(actual.getOwnerAdminId(), 11L);
        Asserts.assertEquals(actual.getOwnerUserId(), 22L);
        Asserts.assertTrue(actual.isAvatar());
        Asserts.assertTrue(actual.isNotPublic());
        Asserts.assertEquals(actual.getFileUploader(), fileUploader);
        Asserts.assertTrue(actual.getTika() != null);

        verifyNoMoreInteractions(
            request,
            response,
            mediaUpDownloader,
            fileUploader,
            validMediaCondition,
            singletonFactory
        );
    }

    @Test
    public void replaceMediaFromRequestTest() throws Exception {
        // given
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        AsyncContext asyncContext = mock(AsyncContext.class);
        Part filePart = mock(Part.class);
        FileUploader fileUploader = mock(FileUploader.class);
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        CapturingServletOutputStream outputStream =
            new CapturingServletOutputStream();
        MediaModel media = MediaModel.builder()
            .id(654L)
            .name("cover.png")
            .uploadFrom("local")
            .type(MediaType.IMAGE)
            .ownerAdminId(111L)
            .ownerUserId(222L)
            .publicMedia(false)
            .build();
        FileMetadata fileMetadata = FileMetadata.builder()
            .mimeType("image/webp")
            .extension("webp")
            .mediaType(MediaType.IMAGE)
            .fileSize(333L)
            .build();
        MediaModel replacedMedia = MediaModel.builder()
            .id(654L)
            .name("cover.png")
            .build();
        File mediaFilePath = File.createTempFile("replace-media-", ".webp");
        String json = "{\"id\":654}";
        mediaFilePath.deleteOnExit();

        when(mediaService.getMediaById(654L)).thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(singletonFactory.getSingletonCast(FileUploader.class))
            .thenReturn(fileUploader);
        when(mediaUpDownloader.isUploadSupported()).thenReturn(false);
        when(request.getPart("file")).thenReturn(filePart);
        when(mediaValidator.validateFilePart(filePart, false))
            .thenReturn(fileMetadata);
        when(filePart.getSubmittedFileName()).thenReturn("replaced.webp");
        when(request.getAsyncContext()).thenReturn(asyncContext);
        when(
            fileSystemManager.getMediaFilePath(
                MediaType.IMAGE.getFolder(),
                "cover.png"
            )
        ).thenReturn(mediaFilePath);
        when(settingService.getMaxUploadFileSize()).thenReturn(4096L);
        when(mediaService.replaceMedia(any(ReplaceMediaModel.class)))
            .thenReturn(replacedMedia);
        when(objectMapper.writeValueAsString(replacedMedia)).thenReturn(json);
        when(response.getOutputStream()).thenReturn(outputStream);
        doAnswer(answer -> {
            Object[] arguments = answer.getArguments();
            File outputFile = (File) arguments[2];
            EzyExceptionVoid callback = (EzyExceptionVoid) arguments[4];
            Files.write(
                outputFile.toPath(),
                "new-content".getBytes(StandardCharsets.UTF_8)
            );
            callback.apply();
            return null;
        })
            .when(fileUploader)
            .accept(
                same(asyncContext),
                same(filePart),
                same(mediaFilePath),
                eq(4096L),
                any(EzyExceptionVoid.class)
            );

        // when
        instance.replaceMedia(
            request,
            response,
            654L,
            validMediaCondition
        );

        // then
        ArgumentCaptor<ReplaceMediaModel> replaceMediaCaptor =
            ArgumentCaptor.forClass(ReplaceMediaModel.class);
        ArgumentCaptor<Object> eventCaptor =
            ArgumentCaptor.forClass(Object.class);

        verify(mediaService).getMediaById(654L);
        verify(validMediaCondition).test(media);
        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(singletonFactory).getSingletonCast(FileUploader.class);
        verify(mediaUpDownloader).isUploadSupported();
        verify(request).getPart("file");
        verify(mediaValidator).validateFilePart(filePart, false);
        verify(eventHandlerManager, times(2)).handleEvent(eventCaptor.capture());
        verify(filePart).getSubmittedFileName();
        verify(request).getAsyncContext();
        verify(fileSystemManager).getMediaFilePath(
            MediaType.IMAGE.getFolder(),
            "cover.png"
        );
        verify(settingService).getMaxUploadFileSize();
        verify(fileUploader).accept(
            same(asyncContext),
            same(filePart),
            same(mediaFilePath),
            eq(4096L),
            any(EzyExceptionVoid.class)
        );
        verify(mediaService).replaceMedia(replaceMediaCaptor.capture());
        verify(objectMapper).writeValueAsString(replacedMedia);
        verify(response).getOutputStream();

        List<Object> events = eventCaptor.getAllValues();
        Asserts.assertEquals(events.size(), 2);
        Asserts.assertTrue(events.get(0) instanceof MediaUploadEvent);
        Asserts.assertTrue(events.get(1) instanceof MediaUploadedEvent);

        MediaUploadEvent uploadEvent = (MediaUploadEvent) events.get(0);
        Asserts.assertEquals(uploadEvent.getUploadFrom(), "local");
        Asserts.assertEquals(uploadEvent.getOwnerAdminId(), 111L);
        Asserts.assertEquals(uploadEvent.getOwnerUserId(), 222L);
        Asserts.assertEquals(uploadEvent.getMedia(), media);
        Asserts.assertEquals(uploadEvent.getFileMetadata(), fileMetadata);

        ReplaceMediaModel replaceMediaModel = replaceMediaCaptor.getValue();
        Asserts.assertEquals(replaceMediaModel.getMediaId(), 654L);
        Asserts.assertEquals(
            replaceMediaModel.getOriginalFileName(),
            "replaced.webp"
        );
        Asserts.assertEquals(replaceMediaModel.getMediaType(), "IMAGE");
        Asserts.assertEquals(replaceMediaModel.getMimeType(), "image/webp");
        Asserts.assertEquals(replaceMediaModel.getFileSize(), 11L);

        MediaUploadedEvent uploadedEvent = (MediaUploadedEvent) events.get(1);
        Asserts.assertEquals(uploadedEvent.getMedia(), replacedMedia);
        Asserts.assertEquals(uploadedEvent.getMediaFilePath(), mediaFilePath);
        Asserts.assertEquals(outputStream.asString(), json);

        InOrder inOrder = inOrder(
            mediaValidator,
            eventHandlerManager,
            fileUploader,
            mediaService,
            settingService
        );
        inOrder.verify(mediaValidator).validateFilePart(filePart, false);
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUploadEvent.class));
        inOrder.verify(settingService).getMaxUploadFileSize();
        inOrder.verify(fileUploader).accept(
            same(asyncContext),
            same(filePart),
            same(mediaFilePath),
            eq(4096L),
            any(EzyExceptionVoid.class)
        );
        inOrder.verify(mediaService).replaceMedia(any(ReplaceMediaModel.class));
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUploadedEvent.class));

        verifyNoMoreInteractions(
            request,
            response,
            asyncContext,
            filePart,
            mediaUpDownloader,
            fileUploader,
            validMediaCondition,
            singletonFactory
        );
    }

    @Test
    public void saveMediaFileUseMediaUpDownloaderWhenSupportedTest() {
        // given
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(mediaUpDownloader.isUploadFromUrlSupported()).thenReturn(true);
        when(
            mediaUpDownloader.uploadFromUrl(any(MediaUploadFromUrlArguments.class))
        ).thenReturn(9988L);

        // when
        long actual = instance.saveMediaFile(
            MediaType.IMAGE,
            "https://cdn.example.com/banner.png",
            UploadFrom.ADMIN,
            77L,
            88L
        );

        // then
        ArgumentCaptor<MediaUploadFromUrlArguments> argumentsCaptor =
            ArgumentCaptor.forClass(MediaUploadFromUrlArguments.class);
        Asserts.assertEquals(actual, 9988L);
        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(mediaUpDownloader).isUploadFromUrlSupported();
        verify(mediaUpDownloader).uploadFromUrl(argumentsCaptor.capture());

        MediaUploadFromUrlArguments arguments = argumentsCaptor.getValue();
        Asserts.assertEquals(arguments.getUploadFrom(), "ADMIN");
        Asserts.assertEquals(arguments.getAction(), UploadAction.ADD);
        Asserts.assertEquals(arguments.getMediaType(), "IMAGE");
        Asserts.assertEquals(
            arguments.getMediaUrl(),
            "https://cdn.example.com/banner.png"
        );
        Asserts.assertEquals(arguments.getOwnerAdminId(), 77L);
        Asserts.assertEquals(arguments.getOwnerUserId(), 88L);
        Asserts.assertTrue(!arguments.isNotPublic());
        Asserts.assertEquals(arguments.getHttpClient(), httpClient);
        Asserts.assertTrue(arguments.getTika() != null);

        verifyNoMoreInteractions(mediaUpDownloader);
    }

    @Test
    public void saveMediaFileTest() throws Exception {
        // given
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        File outFolder = Files.createTempDirectory("save-media-file-test")
            .toFile();
        String mediaUrl = "https://cdn.example.com/banner.png";
        DownloadFileResult downloadResult = new DownloadFileResult(
            "banner-original.png",
            "banner-saved.png"
        );
        File downloadedFile = outFolder.toPath()
            .resolve("banner-saved.png")
            .toFile();
        MediaModel media = MediaModel.builder()
            .id(12345L)
            .name("banner-saved.png")
            .build();

        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(mediaUpDownloader.isUploadFromUrlSupported()).thenReturn(false);
        when(fileSystemManager.getMediaFolderPath(MediaType.IMAGE))
            .thenReturn(outFolder);
        when(mediaService.generateMediaFileName(mediaUrl, "png"))
            .thenReturn("banner-saved.png");
        when(httpClient.download(mediaUrl, outFolder, "banner-saved.png"))
            .thenAnswer(answer -> {
                byte[] pngBytes = new byte[] {
                    (byte) 0x89, 0x50, 0x4E, 0x47,
                    0x0D, 0x0A, 0x1A, 0x0A
                };
                Files.write(downloadedFile.toPath(), pngBytes);
                return downloadResult;
            });
        when(mediaService.addMedia(eq("ADMIN"), any(AddMediaModel.class)))
            .thenReturn(media);

        // when
        long actual = instance.saveMediaFile(
            MediaType.IMAGE,
            mediaUrl,
            UploadFrom.ADMIN,
            101L,
            202L
        );

        // then
        ArgumentCaptor<AddMediaModel> addMediaCaptor =
            ArgumentCaptor.forClass(AddMediaModel.class);
        ArgumentCaptor<MediaUploadedEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaUploadedEvent.class);

        Asserts.assertEquals(actual, 12345L);
        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(mediaUpDownloader).isUploadFromUrlSupported();
        verify(fileSystemManager).getMediaFolderPath(MediaType.IMAGE);
        verify(mediaService).generateMediaFileName(mediaUrl, "png");
        verify(httpClient).download(mediaUrl, outFolder, "banner-saved.png");
        verify(mediaService).addMedia(eq("ADMIN"), addMediaCaptor.capture());
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());

        AddMediaModel addMediaModel = addMediaCaptor.getValue();
        Asserts.assertEquals(addMediaModel.getFileName(), "banner-saved.png");
        Asserts.assertEquals(
            addMediaModel.getOriginalFileName(),
            "banner-original.png"
        );
        Asserts.assertEquals(addMediaModel.getMediaType(), "IMAGE");
        Asserts.assertTrue(addMediaModel.getMimeType() != null);
        Asserts.assertEquals(addMediaModel.getFileSize(), downloadedFile.length());
        Asserts.assertEquals(addMediaModel.getOwnerAdminId(), 101L);
        Asserts.assertEquals(addMediaModel.getOwnerUserId(), 202L);
        Asserts.assertTrue(!addMediaModel.isNotPublic());

        MediaUploadedEvent uploadedEvent = eventCaptor.getValue();
        Asserts.assertEquals(uploadedEvent.getMedia(), media);
        Asserts.assertEquals(uploadedEvent.getMediaFilePath(), downloadedFile);

        InOrder inOrder = inOrder(
            fileSystemManager,
            mediaService,
            httpClient,
            eventHandlerManager
        );
        inOrder.verify(fileSystemManager).getMediaFolderPath(MediaType.IMAGE);
        inOrder.verify(mediaService).generateMediaFileName(mediaUrl, "png");
        inOrder.verify(httpClient).download(mediaUrl, outFolder, "banner-saved.png");
        inOrder.verify(mediaService).addMedia(eq("ADMIN"), any(AddMediaModel.class));
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUploadedEvent.class));

        verifyNoMoreInteractions(mediaUpDownloader);
    }

    @Test
    public void updateMediaIdTest() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        UpdateMediaRequest request = new UpdateMediaRequest();
        request.setTitle("new title");
        MediaModel media = MediaModel.builder()
            .id(789L)
            .name("poster.png")
            .type(MediaType.IMAGE)
            .build();
        MediaModel updatedMedia = MediaModel.builder()
            .id(789L)
            .name("poster.png")
            .title("new title")
            .build();
        UpdateMediaModel updateMediaModel = UpdateMediaModel.builder()
            .mediaId(789L)
            .title("new title")
            .fileSize(12L)
            .build();
        File mediaFilePath = File.createTempFile("update-media-", ".png");
        mediaFilePath.deleteOnExit();
        Files.write(
            mediaFilePath.toPath(),
            "hello world!".getBytes(StandardCharsets.UTF_8)
        );

        when(mediaValidator.validateMediaId(789L)).thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(
            fileSystemManager.getMediaFilePath(
                MediaType.IMAGE.getFolder(),
                "poster.png"
            )
        ).thenReturn(mediaFilePath);
        when(requestToModelConverter.toModel(789L, request))
            .thenReturn(updateMediaModel);
        when(mediaService.updateMedia(updateMediaModel)).thenReturn(updatedMedia);

        // when
        instance.updateMedia(
            789L,
            request,
            validMediaCondition
        );

        // then
        ArgumentCaptor<MediaUpdatedEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaUpdatedEvent.class);

        verify(mediaValidator).validate(request);
        verify(mediaValidator).validateMediaId(789L);
        verify(validMediaCondition).test(media);
        verify(fileSystemManager).getMediaFilePath(
            MediaType.IMAGE.getFolder(),
            "poster.png"
        );
        verify(requestToModelConverter).toModel(789L, request);
        verify(mediaService).updateMedia(updateMediaModel);
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());

        Asserts.assertEquals(request.getFileSize(), mediaFilePath.length());
        Asserts.assertEquals(eventCaptor.getValue().getMedia(), updatedMedia);

        InOrder inOrder = inOrder(
            mediaValidator,
            validMediaCondition,
            fileSystemManager,
            requestToModelConverter,
            mediaService,
            eventHandlerManager
        );
        inOrder.verify(mediaValidator).validate(request);
        inOrder.verify(mediaValidator).validateMediaId(789L);
        inOrder.verify(validMediaCondition).test(media);
        inOrder.verify(fileSystemManager).getMediaFilePath(
            MediaType.IMAGE.getFolder(),
            "poster.png"
        );
        inOrder.verify(requestToModelConverter).toModel(789L, request);
        inOrder.verify(mediaService).updateMedia(updateMediaModel);
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUpdatedEvent.class));

        verifyNoMoreInteractions(validMediaCondition);
    }

    @Test
    public void updateMediaIdIncludeUrlRequestTest() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        UpdateMediaIncludeUrlRequest request =
            new UpdateMediaIncludeUrlRequest();
        request.setTitle("updated title");
        request.setUrl("https://cdn.example.com/updated.mp4");
        MediaModel media = MediaModel.builder()
            .id(790L)
            .name("video.mp4")
            .type(MediaType.VIDEO)
            .build();
        MediaModel updatedMedia = MediaModel.builder()
            .id(790L)
            .name("video.mp4")
            .title("updated title")
            .build();
        UpdateMediaModel updateMediaModel = UpdateMediaModel.builder()
            .mediaId(790L)
            .title("updated title")
            .url("https://cdn.example.com/updated.mp4")
            .fileSize(13L)
            .build();
        File mediaFilePath = File.createTempFile("update-media-url-", ".mp4");
        mediaFilePath.deleteOnExit();
        Files.write(
            mediaFilePath.toPath(),
            "hello world!!".getBytes(StandardCharsets.UTF_8)
        );

        when(mediaValidator.validateMediaId(790L)).thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(
            fileSystemManager.getMediaFilePath(
                MediaType.VIDEO.getFolder(),
                "video.mp4"
            )
        ).thenReturn(mediaFilePath);
        when(requestToModelConverter.toModel(790L, request))
            .thenReturn(updateMediaModel);
        when(mediaService.updateMedia(updateMediaModel)).thenReturn(updatedMedia);

        // when
        instance.updateMedia(
            790L,
            request,
            validMediaCondition
        );

        // then
        ArgumentCaptor<MediaUpdatedEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaUpdatedEvent.class);

        verify(mediaValidator).validate(request);
        verify(mediaValidator).validateMediaId(790L);
        verify(validMediaCondition).test(media);
        verify(fileSystemManager).getMediaFilePath(
            MediaType.VIDEO.getFolder(),
            "video.mp4"
        );
        verify(requestToModelConverter).toModel(790L, request);
        verify(mediaService).updateMedia(updateMediaModel);
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());

        Asserts.assertEquals(request.getFileSize(), mediaFilePath.length());
        Asserts.assertEquals(eventCaptor.getValue().getMedia(), updatedMedia);

        InOrder inOrder = inOrder(
            mediaValidator,
            validMediaCondition,
            fileSystemManager,
            requestToModelConverter,
            mediaService,
            eventHandlerManager
        );
        inOrder.verify(mediaValidator).validate(request);
        inOrder.verify(mediaValidator).validateMediaId(790L);
        inOrder.verify(validMediaCondition).test(media);
        inOrder.verify(fileSystemManager).getMediaFilePath(
            MediaType.VIDEO.getFolder(),
            "video.mp4"
        );
        inOrder.verify(requestToModelConverter).toModel(790L, request);
        inOrder.verify(mediaService).updateMedia(updateMediaModel);
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUpdatedEvent.class));

        verifyNoMoreInteractions(validMediaCondition);
    }

    @Test
    public void updateMediaNameTest() throws Exception {
        // given
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        UpdateMediaRequest request = new UpdateMediaRequest();
        request.setTitle("named title");
        MediaModel media = MediaModel.builder()
            .id(791L)
            .name("named-poster.png")
            .type(MediaType.IMAGE)
            .build();
        MediaModel updatedMedia = MediaModel.builder()
            .id(791L)
            .name("named-poster.png")
            .title("named title")
            .build();
        UpdateMediaModel updateMediaModel = UpdateMediaModel.builder()
            .mediaName("named-poster.png")
            .title("named title")
            .fileSize(14L)
            .build();
        File mediaFilePath = File.createTempFile("update-media-name-", ".png");
        mediaFilePath.deleteOnExit();
        Files.write(
            mediaFilePath.toPath(),
            "hello world!!!".getBytes(StandardCharsets.UTF_8)
        );

        when(mediaValidator.validateMediaNameAndGet("named-poster.png"))
            .thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(
            fileSystemManager.getMediaFilePath(
                MediaType.IMAGE.getFolder(),
                "named-poster.png"
            )
        ).thenReturn(mediaFilePath);
        when(requestToModelConverter.toModel("named-poster.png", request))
            .thenReturn(updateMediaModel);
        when(mediaService.updateMedia(updateMediaModel)).thenReturn(updatedMedia);

        // when
        instance.updateMedia(
            "named-poster.png",
            request,
            validMediaCondition
        );

        // then
        ArgumentCaptor<MediaUpdatedEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaUpdatedEvent.class);

        verify(mediaValidator).validate(request);
        verify(mediaValidator).validateMediaNameAndGet("named-poster.png");
        verify(validMediaCondition).test(media);
        verify(fileSystemManager).getMediaFilePath(
            MediaType.IMAGE.getFolder(),
            "named-poster.png"
        );
        verify(requestToModelConverter).toModel("named-poster.png", request);
        verify(mediaService).updateMedia(updateMediaModel);
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());

        Asserts.assertEquals(request.getFileSize(), mediaFilePath.length());
        Asserts.assertEquals(eventCaptor.getValue().getMedia(), updatedMedia);

        InOrder inOrder = inOrder(
            mediaValidator,
            validMediaCondition,
            fileSystemManager,
            requestToModelConverter,
            mediaService,
            eventHandlerManager
        );
        inOrder.verify(mediaValidator).validate(request);
        inOrder.verify(mediaValidator).validateMediaNameAndGet("named-poster.png");
        inOrder.verify(validMediaCondition).test(media);
        inOrder.verify(fileSystemManager).getMediaFilePath(
            MediaType.IMAGE.getFolder(),
            "named-poster.png"
        );
        inOrder.verify(requestToModelConverter).toModel("named-poster.png", request);
        inOrder.verify(mediaService).updateMedia(updateMediaModel);
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUpdatedEvent.class));

        verifyNoMoreInteractions(validMediaCondition);
    }

    @Test
    public void updateMediaStatusTest() {
        // given
        MediaModel updatedMedia = MediaModel.builder()
            .id(900L)
            .name("status-media.png")
            .status("INACTIVE")
            .build();
        when(mediaService.updateMediaStatus(900L, "INACTIVE"))
            .thenReturn(updatedMedia);

        // when
        instance.updateMediaStatus(900L, "INACTIVE");

        // then
        ArgumentCaptor<MediaUpdatedEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaUpdatedEvent.class);

        verify(mediaService).updateMediaStatus(900L, "INACTIVE");
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());
        Asserts.assertEquals(eventCaptor.getValue().getMedia(), updatedMedia);

        InOrder inOrder = inOrder(mediaService, eventHandlerManager);
        inOrder.verify(mediaService).updateMediaStatus(900L, "INACTIVE");
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaUpdatedEvent.class));
    }

    @Test
    public void getMediaFileSizeTest() throws Exception {
        // given
        MediaModel media = MediaModel.builder()
            .id(901L)
            .name("file-size.png")
            .type(MediaType.IMAGE)
            .build();
        File mediaFilePath = File.createTempFile("get-media-file-size-", ".png");
        mediaFilePath.deleteOnExit();
        Files.write(
            mediaFilePath.toPath(),
            "file-size-data".getBytes(StandardCharsets.UTF_8)
        );
        when(
            fileSystemManager.getMediaFilePath(
                MediaType.IMAGE.getFolder(),
                "file-size.png"
            )
        ).thenReturn(mediaFilePath);

        // when
        long actual = instance.getMediaFileSize(media);

        // then
        Asserts.assertEquals(actual, mediaFilePath.length());
        verify(fileSystemManager).getMediaFilePath(
            MediaType.IMAGE.getFolder(),
            "file-size.png"
        );
    }

    @Test
    public void removeMediaByIdTest() {
        // given
        MediaModel removedMedia = MediaModel.builder()
            .id(902L)
            .name("removed-media.png")
            .type(MediaType.IMAGE)
            .status("ACTIVE")
            .build();
        when(mediaService.removeMedia(902L)).thenReturn(removedMedia);

        // when
        instance.removeMediaById(902L, false);

        // then
        ArgumentCaptor<MediaRemovedEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaRemovedEvent.class);

        verify(mediaService).removeMedia(902L);
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());
        Asserts.assertEquals(eventCaptor.getValue().getMedia(), removedMedia);

        InOrder inOrder = inOrder(mediaService, eventHandlerManager);
        inOrder.verify(mediaService).removeMedia(902L);
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaRemovedEvent.class));
    }

    @Test
    public void removeMediaByNameTest() {
        // given
        MediaModel removedMedia = MediaModel.builder()
            .id(903L)
            .name("removed-by-name.png")
            .type(MediaType.IMAGE)
            .status("ACTIVE")
            .build();
        when(mediaService.removeMedia("removed-by-name.png"))
            .thenReturn(removedMedia);

        // when
        instance.removeMediaByName("removed-by-name.png", false);

        // then
        ArgumentCaptor<MediaRemovedEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaRemovedEvent.class);

        verify(mediaService).removeMedia("removed-by-name.png");
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());
        Asserts.assertEquals(eventCaptor.getValue().getMedia(), removedMedia);

        InOrder inOrder = inOrder(mediaService, eventHandlerManager);
        inOrder.verify(mediaService).removeMedia("removed-by-name.png");
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaRemovedEvent.class));
    }

    @Test
    public void getMediaByNameUseMediaUpDownloaderWhenSupportedTest()
        throws Exception {
        // given
        RequestArguments requestArguments = mock(RequestArguments.class);
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(mediaUpDownloader.isDownloadSupported()).thenReturn(true);

        // when
        instance.getMediaByName(
            requestArguments,
            "downloaded.png",
            true,
            validMediaCondition
        );

        // then
        ArgumentCaptor<MediaDownloadArguments> argumentsCaptor =
            ArgumentCaptor.forClass(MediaDownloadArguments.class);
        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(mediaUpDownloader).isDownloadSupported();
        verify(mediaUpDownloader).download(argumentsCaptor.capture());

        MediaDownloadArguments arguments = argumentsCaptor.getValue();
        Asserts.assertEquals(arguments.getRequestArguments(), requestArguments);
        Asserts.assertEquals(arguments.getName(), "downloaded.png");
        Asserts.assertTrue(arguments.isExposePrivateMedia());
        Asserts.assertEquals(arguments.getValidMediaCondition(), validMediaCondition);

        verifyNoMoreInteractions(
            requestArguments,
            mediaUpDownloader,
            validMediaCondition
        );
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getMediaByNameTest() throws Exception {
        // given
        RequestArguments requestArguments = mock(RequestArguments.class);
        AsyncContext asyncContext = mock(AsyncContext.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        MediaUpDownloader mediaUpDownloader = mock(MediaUpDownloader.class);
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        CapturingServletOutputStream outputStream =
            new CapturingServletOutputStream();
        MediaModel media = MediaModel.builder()
            .id(904L)
            .name("private-video.mp4")
            .type(MediaType.VIDEO)
            .publicMedia(false)
            .build();
        File mediaFilePath = File.createTempFile("get-media-by-name-", ".mp4");
        mediaFilePath.deleteOnExit();
        Files.write(
            mediaFilePath.toPath(),
            "video-content".getBytes(StandardCharsets.UTF_8)
        );

        when(settingService.getMediaUpDownloaderName()).thenReturn("cloud");
        when(mediaUpDownloaderManager.getMediaUpDownloaderByName("cloud"))
            .thenReturn(mediaUpDownloader);
        when(mediaUpDownloader.isDownloadSupported()).thenReturn(false);
        when(mediaService.getMediaByName("private-video.mp4")).thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(
            fileSystemManager.getMediaFilePath(
                MediaType.VIDEO.getFolder(),
                "private-video.mp4"
            )
        ).thenReturn(mediaFilePath);
        when(requestArguments.getAsyncContext()).thenReturn(asyncContext);
        when(asyncContext.getResponse()).thenReturn(response);
        when(requestArguments.getHeader("Range")).thenReturn(null);
        when(inputStreamLoader.load(mediaFilePath.toString()))
            .thenReturn(new ByteArrayInputStream(
                "video-content".getBytes(StandardCharsets.UTF_8)
            ));
        when(response.getOutputStream()).thenReturn(outputStream);
        doAnswer(answer -> {
            Object[] arguments = answer.getArguments();
            InputStream inputStream = (InputStream) arguments[0];
            OutputStream out = (OutputStream) arguments[1];
            @SuppressWarnings("unchecked")
            EzyResultCallback<Boolean> callback =
                (EzyResultCallback<Boolean>) arguments[2];
            byte[] data = new byte[64];
            int readBytes = inputStream.read(data);
            if (readBytes > 0) {
                out.write(data, 0, readBytes);
            }
            callback.onResponse(true);
            return null;
        }).when(resourceDownloadManager)
            .drainAsync(
                any(InputStream.class),
                any(OutputStream.class),
                any(EzyResultCallback.class)
            );

        // when
        instance.getMediaByName(
            requestArguments,
            "private-video.mp4",
            false,
            validMediaCondition
        );

        // then
        ArgumentCaptor<MediaDownloadEvent> eventCaptor =
            ArgumentCaptor.forClass(MediaDownloadEvent.class);

        verify(settingService).getMediaUpDownloaderName();
        verify(mediaUpDownloaderManager).getMediaUpDownloaderByName("cloud");
        verify(mediaUpDownloader).isDownloadSupported();
        verify(mediaValidator).validateMediaName("private-video.mp4");
        verify(mediaService).getMediaByName("private-video.mp4");
        verify(validMediaCondition).test(media);
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());
        verify(fileSystemManager).getMediaFilePath(
            MediaType.VIDEO.getFolder(),
            "private-video.mp4"
        );
        verify(requestArguments).getAsyncContext();
        verify(asyncContext).getResponse();
        verify(requestArguments).getHeader("Range");
        verify(inputStreamLoader).load(mediaFilePath.toString());
        verify(response).setContentType("video/mp4");
        verify(response).getOutputStream();
        verify(response).setStatus(200);
        verify(resourceDownloadManager).drainAsync(
            any(InputStream.class),
            any(OutputStream.class),
            any(EzyResultCallback.class)
        );
        verify(asyncContext).complete();

        Asserts.assertEquals(eventCaptor.getValue().getMedia(), media);
        Asserts.assertEquals(
            outputStream.asString(),
            "video-content"
        );

        InOrder inOrder = inOrder(
            mediaValidator,
            mediaService,
            validMediaCondition,
            eventHandlerManager,
            fileSystemManager,
            requestArguments,
            inputStreamLoader,
            resourceDownloadManager,
            asyncContext
        );
        inOrder.verify(mediaValidator).validateMediaName("private-video.mp4");
        inOrder.verify(mediaService).getMediaByName("private-video.mp4");
        inOrder.verify(validMediaCondition).test(media);
        inOrder.verify(eventHandlerManager).handleEvent(any(MediaDownloadEvent.class));
        inOrder.verify(fileSystemManager).getMediaFilePath(
            MediaType.VIDEO.getFolder(),
            "private-video.mp4"
        );
        inOrder.verify(requestArguments).getAsyncContext();
        inOrder.verify(requestArguments).getHeader("Range");
        inOrder.verify(inputStreamLoader).load(mediaFilePath.toString());
        inOrder.verify(resourceDownloadManager).drainAsync(
            any(InputStream.class),
            any(OutputStream.class),
            any(EzyResultCallback.class)
        );
        inOrder.verify(asyncContext).complete();

        verifyNoMoreInteractions(
            requestArguments,
            asyncContext,
            response,
            mediaUpDownloader,
            validMediaCondition
        );
    }

    @Test
    public void getMediaDetailsByIdTest() {
        // given
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        MediaModel media = MediaModel.builder()
            .id(905L)
            .name("video-detail.mp4")
            .originalName("video-detail-original.mp4")
            .uploadFrom("ADMIN")
            .type(MediaType.VIDEO)
            .mimeType("video/mp4")
            .ownerAdminId(11L)
            .ownerUserId(22L)
            .title("detail title")
            .caption("detail caption")
            .alternativeText("detail alt")
            .description("detail description")
            .publicMedia(true)
            .status("ACTIVE")
            .createdAt(100L)
            .updatedAt(200L)
            .build();
        when(mediaService.getMediaById(905L)).thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(mediaService.getMediaFileLengthOrNegative(
            MediaType.VIDEO,
            "video-detail.mp4"
        )).thenReturn(321L);

        // when
        MediaDetailsModel actual = instance.getMediaDetailsById(
            905L,
            validMediaCondition
        );

        // then
        verify(mediaService).getMediaById(905L);
        verify(validMediaCondition).test(media);
        verify(mediaService).getMediaFileLengthOrNegative(
            MediaType.VIDEO,
            "video-detail.mp4"
        );

        Asserts.assertEquals(actual.getId(), 905L);
        Asserts.assertEquals(actual.getName(), "video-detail.mp4");
        Asserts.assertEquals(
            actual.getOriginalName(),
            "video-detail-original.mp4"
        );
        Asserts.assertEquals(actual.getUploadFrom(), "ADMIN");
        Asserts.assertEquals(actual.getType(), "VIDEO");
        Asserts.assertEquals(actual.getMimeType(), "video/mp4");
        Asserts.assertEquals(actual.getOwnerAdminId(), 11L);
        Asserts.assertEquals(actual.getOwnerUserId(), 22L);
        Asserts.assertEquals(actual.getTitle(), "detail title");
        Asserts.assertEquals(actual.getCaption(), "detail caption");
        Asserts.assertEquals(actual.getAlternativeText(), "detail alt");
        Asserts.assertEquals(actual.getDescription(), "detail description");
        Asserts.assertTrue(actual.isPublicMedia());
        Asserts.assertEquals(actual.getStatus(), "ACTIVE");
        Asserts.assertEquals(actual.getCreatedAt(), 100L);
        Asserts.assertEquals(actual.getUpdatedAt(), 200L);
        Asserts.assertEquals(actual.getSize(), 321L);
        Asserts.assertEquals(actual.getWidth(), 0L);
        Asserts.assertEquals(actual.getHeight(), 0L);

        InOrder inOrder = inOrder(mediaService, validMediaCondition);
        inOrder.verify(mediaService).getMediaById(905L);
        inOrder.verify(validMediaCondition).test(media);
        inOrder.verify(mediaService).getMediaFileLengthOrNegative(
            MediaType.VIDEO,
            "video-detail.mp4"
        );

        verifyNoMoreInteractions(validMediaCondition);
    }

    @Test
    public void getMediaDetailsByNameTest() {
        // given
        @SuppressWarnings("unchecked")
        Predicate<MediaModel> validMediaCondition = mock(Predicate.class);
        MediaModel media = MediaModel.builder()
            .id(906L)
            .name("audio-detail.mp3")
            .originalName("audio-detail-original.mp3")
            .uploadFrom("USER")
            .type(MediaType.AUDIO)
            .mimeType("audio/mpeg")
            .ownerAdminId(33L)
            .ownerUserId(44L)
            .title("audio title")
            .caption("audio caption")
            .alternativeText("audio alt")
            .description("audio description")
            .publicMedia(false)
            .status("ACTIVE")
            .createdAt(300L)
            .updatedAt(400L)
            .build();
        when(mediaService.getMediaByName("audio-detail.mp3")).thenReturn(media);
        when(validMediaCondition.test(media)).thenReturn(true);
        when(mediaService.getMediaFileLengthOrNegative(
            MediaType.AUDIO,
            "audio-detail.mp3"
        )).thenReturn(654L);

        // when
        MediaDetailsModel actual = instance.getMediaDetailsByName(
            "audio-detail.mp3",
            validMediaCondition
        );

        // then
        verify(mediaService).getMediaByName("audio-detail.mp3");
        verify(validMediaCondition).test(media);
        verify(mediaService).getMediaFileLengthOrNegative(
            MediaType.AUDIO,
            "audio-detail.mp3"
        );

        Asserts.assertEquals(actual.getId(), 906L);
        Asserts.assertEquals(actual.getName(), "audio-detail.mp3");
        Asserts.assertEquals(
            actual.getOriginalName(),
            "audio-detail-original.mp3"
        );
        Asserts.assertEquals(actual.getUploadFrom(), "USER");
        Asserts.assertEquals(actual.getType(), "AUDIO");
        Asserts.assertEquals(actual.getMimeType(), "audio/mpeg");
        Asserts.assertEquals(actual.getOwnerAdminId(), 33L);
        Asserts.assertEquals(actual.getOwnerUserId(), 44L);
        Asserts.assertEquals(actual.getTitle(), "audio title");
        Asserts.assertEquals(actual.getCaption(), "audio caption");
        Asserts.assertEquals(actual.getAlternativeText(), "audio alt");
        Asserts.assertEquals(actual.getDescription(), "audio description");
        Asserts.assertTrue(!actual.isPublicMedia());
        Asserts.assertEquals(actual.getStatus(), "ACTIVE");
        Asserts.assertEquals(actual.getCreatedAt(), 300L);
        Asserts.assertEquals(actual.getUpdatedAt(), 400L);
        Asserts.assertEquals(actual.getSize(), 654L);
        Asserts.assertEquals(actual.getWidth(), 0L);
        Asserts.assertEquals(actual.getHeight(), 0L);

        InOrder inOrder = inOrder(mediaService, validMediaCondition);
        inOrder.verify(mediaService).getMediaByName("audio-detail.mp3");
        inOrder.verify(validMediaCondition).test(media);
        inOrder.verify(mediaService).getMediaFileLengthOrNegative(
            MediaType.AUDIO,
            "audio-detail.mp3"
        );

        verifyNoMoreInteractions(validMediaCondition);
    }

    @Test
    public void getMediaDetailsTest() throws Exception {
        // given
        MediaModel media = MediaModel.builder()
            .id(907L)
            .name("poster-image.png")
            .originalName("poster-image-original.png")
            .uploadFrom("ADMIN")
            .type(MediaType.IMAGE)
            .mimeType("image/png")
            .ownerAdminId(55L)
            .ownerUserId(66L)
            .title("poster title")
            .caption("poster caption")
            .alternativeText("poster alt")
            .description("poster description")
            .publicMedia(true)
            .status("ACTIVE")
            .createdAt(500L)
            .updatedAt(600L)
            .build();
        File mediaFilePath = File.createTempFile("media-details-", ".png");
        mediaFilePath.deleteOnExit();
        when(mediaService.getMediaImageSizeOrNull(
            "poster-image.png",
            MediaType.IMAGE
        )).thenReturn(null);
        when(eventHandlerManager.handleEvent(any(GetMediaFilePathEvent.class)))
            .thenReturn(mediaFilePath);
        when(mediaService.getMediaImageSizeOrDefault(mediaFilePath))
            .thenReturn(new org.youngmonkeys.ezyplatform.data.ImageSize(
                800,
                600,
                1234L
            ));

        // when
        MediaDetailsModel actual = instance.getMediaDetails(media);

        // then
        ArgumentCaptor<GetMediaFilePathEvent> eventCaptor =
            ArgumentCaptor.forClass(GetMediaFilePathEvent.class);

        verify(mediaService).getMediaImageSizeOrNull(
            "poster-image.png",
            MediaType.IMAGE
        );
        verify(eventHandlerManager).handleEvent(eventCaptor.capture());
        verify(mediaService).getMediaImageSizeOrDefault(mediaFilePath);

        Asserts.assertEquals(eventCaptor.getValue().getMedia(), media);
        Asserts.assertEquals(actual.getId(), 907L);
        Asserts.assertEquals(actual.getName(), "poster-image.png");
        Asserts.assertEquals(
            actual.getOriginalName(),
            "poster-image-original.png"
        );
        Asserts.assertEquals(actual.getUploadFrom(), "ADMIN");
        Asserts.assertEquals(actual.getType(), "IMAGE");
        Asserts.assertEquals(actual.getMimeType(), "image/png");
        Asserts.assertEquals(actual.getOwnerAdminId(), 55L);
        Asserts.assertEquals(actual.getOwnerUserId(), 66L);
        Asserts.assertEquals(actual.getTitle(), "poster title");
        Asserts.assertEquals(actual.getCaption(), "poster caption");
        Asserts.assertEquals(actual.getAlternativeText(), "poster alt");
        Asserts.assertEquals(actual.getDescription(), "poster description");
        Asserts.assertTrue(actual.isPublicMedia());
        Asserts.assertEquals(actual.getStatus(), "ACTIVE");
        Asserts.assertEquals(actual.getCreatedAt(), 500L);
        Asserts.assertEquals(actual.getUpdatedAt(), 600L);
        Asserts.assertEquals(actual.getWidth(), 800L);
        Asserts.assertEquals(actual.getHeight(), 600L);
        Asserts.assertEquals(actual.getSize(), 1234L);

        InOrder inOrder = inOrder(mediaService, eventHandlerManager);
        inOrder.verify(mediaService).getMediaImageSizeOrNull(
            "poster-image.png",
            MediaType.IMAGE
        );
        inOrder.verify(eventHandlerManager).handleEvent(any(GetMediaFilePathEvent.class));
        inOrder.verify(mediaService).getMediaImageSizeOrDefault(mediaFilePath);
    }

    @Test
    public void getMediaListTest() {
        // given
        MediaFilter filter = mock(MediaFilter.class);
        PaginationMediaRepository paginationMediaRepository =
            mock(PaginationMediaRepository.class);
        DefaultEntityToModelConverter entityToModelConverter =
            mock(DefaultEntityToModelConverter.class);
        PaginationMediaService realPaginationMediaService =
            new PaginationMediaService(
                paginationMediaRepository,
                entityToModelConverter,
                mediaPaginationParameterConverter
            );
        MediaControllerService localInstance = new MediaControllerService(
            httpClient,
            eventHandlerManager,
            fileSystemManager,
            inputStreamLoader,
            mediaUpDownloaderManager,
            objectMapper,
            resourceDownloadManager,
            singletonFactory,
            mediaService,
            realPaginationMediaService,
            settingService,
            commonValidator,
            mediaValidator,
            mediaPaginationParameterConverter,
            modelToResponseConverter,
            requestToModelConverter
        );
        org.youngmonkeys.ezyplatform.entity.Media mediaEntity =
            mock(org.youngmonkeys.ezyplatform.entity.Media.class);
        MediaModel mediaModel = MediaModel.builder()
            .id(908L)
            .name("pagination-media.png")
            .build();
        MediaResponse mediaResponse = MediaResponse.builder()
            .id(908L)
            .name("pagination-media.png")
            .build();
        PaginationModel<MediaModel> paginationModel =
            PaginationModel.<MediaModel>builder()
                .items(java.util.Collections.singletonList(mediaModel))
                .count(1)
                .total(1L)
                .timestamp(123L)
                .pageToken(PaginationModel.PageToken.builder().build())
                .continuation(PaginationModel.Continuation.builder().build())
                .build();

        when(
            paginationMediaRepository.findNextElements(
                eq(filter),
                any(),
                eq(21)
            )
        ).thenReturn(java.util.Collections.singletonList(mediaEntity));
        when(paginationMediaRepository.findSettingValue(any(String.class)))
            .thenReturn(null);
        when(
            paginationMediaRepository.findFirstElements(
                eq(filter),
                eq(1_000_000),
                eq(1)
            )
        ).thenReturn(java.util.Collections.emptyList());
        when(paginationMediaRepository.countElements(filter)).thenReturn(1L);
        when(entityToModelConverter.toModel(mediaEntity)).thenReturn(mediaModel);
        when(mediaPaginationParameterConverter.serialize(any(String.class), eq(mediaModel)))
            .thenReturn("page-token");
        when(modelToResponseConverter.toResponse(mediaModel))
            .thenReturn(mediaResponse);

        // when
        PaginationModel<MediaResponse> actual = localInstance.getMediaList(
            filter,
            null,
            null,
            null,
            false,
            20
        );

        // then
        verify(commonValidator).validatePageSize(20);
        verify(paginationMediaRepository).findNextElements(
            eq(filter),
            any(),
            eq(21)
        );
        verify(paginationMediaRepository).findSettingValue(any(String.class));
        verify(paginationMediaRepository).findFirstElements(
            eq(filter),
            eq(1_000_000),
            eq(1)
        );
        verify(paginationMediaRepository).countElements(filter);
        verify(entityToModelConverter).toModel(mediaEntity);
        verify(modelToResponseConverter).toResponse(mediaModel);

        Asserts.assertEquals(actual.getItems().size(), 1);
        Asserts.assertEquals(actual.getItems().get(0), mediaResponse);
        Asserts.assertEquals(actual.getCount(), 1);
        Asserts.assertEquals(actual.getTotal(), 1L);
        Asserts.assertTrue(actual.getTimestamp() > 0);

        InOrder inOrder = inOrder(
            commonValidator,
            modelToResponseConverter
        );
        inOrder.verify(commonValidator).validatePageSize(20);
        inOrder.verify(modelToResponseConverter).toResponse(mediaModel);
    }

    private static class CapturingServletOutputStream
        extends ServletOutputStream {

        private final ByteArrayOutputStream outputStream =
            new ByteArrayOutputStream();

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
        }

        @Override
        public void write(int b) {
            outputStream.write(b);
        }

        public String asString() {
            return new String(
                outputStream.toByteArray(),
                StandardCharsets.UTF_8
            );
        }
    }
}
