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

package org.youngmonkeys.ezyplatform.controller.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tvd12.ezyfox.bean.EzySingletonFactory;
import com.tvd12.ezyfox.concurrent.EzyLazyInitializer;
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyfox.util.EzyReturner;
import com.tvd12.ezyhttp.client.HttpClient;
import com.tvd12.ezyhttp.client.data.DownloadFileResult;
import com.tvd12.ezyhttp.core.exception.HttpBadRequestException;
import com.tvd12.ezyhttp.core.exception.HttpNotAcceptableException;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.resources.FileUploader;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.youngmonkeys.ezyplatform.converter.HttpModelToResponseConverter;
import org.youngmonkeys.ezyplatform.converter.HttpRequestToModelConverter;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.data.ImageSize;
import org.youngmonkeys.ezyplatform.data.MediaFileSizeReductionResult;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.entity.UploadAction;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;
import org.youngmonkeys.ezyplatform.event.EventHandlerManager;
import org.youngmonkeys.ezyplatform.event.GetMediaDetailsEvent;
import org.youngmonkeys.ezyplatform.event.GetMediaFilePathEvent;
import org.youngmonkeys.ezyplatform.event.MediaAddedEvent;
import org.youngmonkeys.ezyplatform.event.MediaDownloadEvent;
import org.youngmonkeys.ezyplatform.event.MediaFileSizeReducedEvent;
import org.youngmonkeys.ezyplatform.event.MediaFileSizeReductionEvent;
import org.youngmonkeys.ezyplatform.event.MediaRemovedEvent;
import org.youngmonkeys.ezyplatform.event.MediaReplacedEvent;
import org.youngmonkeys.ezyplatform.event.MediaUpdatedEvent;
import org.youngmonkeys.ezyplatform.event.MediaUploadEvent;
import org.youngmonkeys.ezyplatform.event.MediaUploadedEvent;
import org.youngmonkeys.ezyplatform.event.ValidateMediaOwnerEvent;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.io.FolderProxy;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.media.MediaDownloadArguments;
import org.youngmonkeys.ezyplatform.media.MediaFileSizeReductionArguments;
import org.youngmonkeys.ezyplatform.media.MediaUpDownloader;
import org.youngmonkeys.ezyplatform.media.MediaUpDownloaderManager;
import org.youngmonkeys.ezyplatform.media.MediaUploadArguments;
import org.youngmonkeys.ezyplatform.media.MediaUploadFromUrlArguments;
import org.youngmonkeys.ezyplatform.model.AddMediaModel;
import org.youngmonkeys.ezyplatform.model.MediaDetailsModel;
import org.youngmonkeys.ezyplatform.model.MediaModel;
import org.youngmonkeys.ezyplatform.model.PaginationModel;
import org.youngmonkeys.ezyplatform.model.ReplaceMediaModel;
import org.youngmonkeys.ezyplatform.model.SaveMediaFileFromUrlModel;
import org.youngmonkeys.ezyplatform.model.UpdateMediaModel;
import org.youngmonkeys.ezyplatform.pagination.MediaFilter;
import org.youngmonkeys.ezyplatform.pagination.MediaPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.request.AddMediaFromUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaIncludeUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaRequest;
import org.youngmonkeys.ezyplatform.response.MediaResponse;
import org.youngmonkeys.ezyplatform.service.MediaFileService;
import org.youngmonkeys.ezyplatform.service.MediaService;
import org.youngmonkeys.ezyplatform.service.PaginationMediaService;
import org.youngmonkeys.ezyplatform.service.SettingService;
import org.youngmonkeys.ezyplatform.util.Uris;
import org.youngmonkeys.ezyplatform.validator.CommonValidator;
import org.youngmonkeys.ezyplatform.validator.MediaValidator;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Predicate;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static com.tvd12.ezyfox.io.EzyStrings.isNotBlank;
import static java.util.Collections.singletonMap;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.DELETED;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;
import static org.youngmonkeys.ezyplatform.model.MediaDetailsModel.fromMediaModel;
import static org.youngmonkeys.ezyplatform.pagination.PaginationModelFetchers.getPaginationModelBySortOrder;
import static org.youngmonkeys.ezyplatform.util.Strings.from;
import static org.youngmonkeys.ezyplatform.validator.DefaultValidator.isValidExternalUrl;

public class MediaControllerService extends EzyLoggable {

    private final HttpClient httpClient;
    private final EventHandlerManager eventHandlerManager;
    private final FileSystemManager fileSystemManager;
    private final EzyInputStreamLoader inputStreamLoader;
    private final MediaUpDownloaderManager mediaUpDownloaderManager;
    private final ObjectMapper objectMapper;
    private final ResourceDownloadManager resourceDownloadManager;
    private final MediaService mediaService;
    private final MediaFileService mediaFileService;
    private final PaginationMediaService paginationMediaService;
    private final SettingService settingService;
    private final CommonValidator commonValidator;
    private final MediaValidator mediaValidator;
    private final MediaPaginationParameterConverter mediaPaginationParameterConverter;
    private final HttpModelToResponseConverter modelToResponseConverter;
    private final HttpRequestToModelConverter requestToModelConverter;
    private final EzyLazyInitializer<FileUploader> fileUploaderWrapper;

    private final EzyLazyInitializer<TikaConfig> tika =
        new EzyLazyInitializer<>(() ->
            EzyReturner.returnWithException(TikaConfig::new)
        );

    public MediaControllerService(
        HttpClient httpClient,
        EventHandlerManager eventHandlerManager,
        FileSystemManager fileSystemManager,
        EzyInputStreamLoader inputStreamLoader,
        MediaUpDownloaderManager mediaUpDownloaderManager,
        ObjectMapper objectMapper,
        ResourceDownloadManager resourceDownloadManager,
        EzySingletonFactory singletonFactory,
        MediaService mediaService,
        MediaFileService mediaFileService,
        PaginationMediaService paginationMediaService,
        SettingService settingService,
        CommonValidator commonValidator,
        MediaValidator mediaValidator,
        MediaPaginationParameterConverter mediaPaginationParameterConverter,
        HttpModelToResponseConverter modelToResponseConverter,
        HttpRequestToModelConverter requestToModelConverter
    ) {
        this.httpClient = httpClient;
        this.mediaService = mediaService;
        this.mediaFileService = mediaFileService;
        this.paginationMediaService = paginationMediaService;
        this.settingService = settingService;
        this.objectMapper = objectMapper;
        this.inputStreamLoader = inputStreamLoader;
        this.commonValidator = commonValidator;
        this.mediaValidator = mediaValidator;
        this.eventHandlerManager = eventHandlerManager;
        this.fileSystemManager = fileSystemManager;
        this.mediaUpDownloaderManager = mediaUpDownloaderManager;
        this.resourceDownloadManager = resourceDownloadManager;
        this.mediaPaginationParameterConverter = mediaPaginationParameterConverter;
        this.modelToResponseConverter = modelToResponseConverter;
        this.requestToModelConverter = requestToModelConverter;
        this.fileUploaderWrapper = new EzyLazyInitializer<>(
            () -> singletonFactory.getSingletonCast(
                FileUploader.class
            )
        );
    }

    public void addMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        boolean avatar,
        boolean notPublic
    ) throws Exception {
        addMedia(
            request,
            response,
            uploadFrom,
            ownerAdminId,
            ownerUserId,
            avatar,
            notPublic,
            settingService.getMaxUploadFileSize()
        );
    }

    @SuppressWarnings("MethodLength")
    public void addMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        boolean avatar,
        boolean notPublic,
        long maxFileSize
    ) throws Exception {
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        FileUploader fileUploader = fileUploaderWrapper.get();
        if (mediaUpDownloader != null
            && mediaUpDownloader.isUploadSupported()
        ) {
            mediaUpDownloader.upload(
                MediaUploadArguments.builder()
                    .tika(tika.get())
                    .fileUploader(fileUploader)
                    .request(request)
                    .response(response)
                    .uploadFrom(uploadFrom)
                    .action(UploadAction.ADD)
                    .ownerAdminId(ownerAdminId)
                    .ownerUserId(ownerUserId)
                    .avatar(avatar)
                    .notPublic(notPublic)
                    .build()
            );
            return;
        }
        if (fileUploader == null) {
            throw new HttpNotAcceptableException(
                singletonMap("fileUpload", "disabled")
            );
        }
        Part filePart = request.getPart("file");
        if (filePart == null) {
            Collection<Part> parts = request.getParts();
            if (!parts.isEmpty()) {
                filePart = parts.iterator().next();
            }
        }
        FileMetadata fileMetadata = mediaValidator.validateFilePart(
            filePart,
            avatar
        );
        eventHandlerManager.handleEvent(
            MediaUploadEvent.builder()
                .uploadFrom(uploadFrom)
                .ownerAdminId(ownerAdminId)
                .ownerUserId(ownerUserId)
                .fileMetadata(fileMetadata)
                .build()
        );
        //noinspection ConstantConditions
        String submittedFileName = filePart.getSubmittedFileName();
        String fileExtension = fileMetadata.getExtension();
        String containerFolder = fileMetadata.getMediaType().getFolder();
        String newFileName = mediaService.generateMediaFileName(
            submittedFileName,
            fileExtension
        );
        AsyncContext asyncContext = request.getAsyncContext();
        File mediaFilePath = fileSystemManager.getMediaFilePath(
            containerFolder,
            newFileName
        );
        fileUploader.accept(
            asyncContext,
            filePart,
            mediaFilePath,
            maxFileSize,
            () -> {
                MediaFileSizeReductionResult reduceResult =
                    reduceMediaFileSize(
                        fileMetadata.getMediaType(),
                        mediaFilePath
                    );
                String storedFileName = reduceResult
                    .getNewFileNameOrDefault(newFileName);
                File storedMediaFilePath = isBlank(reduceResult.getNewFileName())
                    ? mediaFilePath
                    : new File(mediaFilePath.getParentFile(), storedFileName);
                MediaModel model = mediaService.addMedia(
                    uploadFrom,
                    AddMediaModel.builder()
                        .ownerAdminId(ownerAdminId)
                        .ownerUserId(ownerUserId)
                        .fileName(storedFileName)
                        .originalFileName(submittedFileName)
                        .mediaType(fileMetadata.getMediaTypeText())
                        .mimeType(
                            reduceResult.getNewFileMimeTypeOrDefault(
                                fileMetadata.getMimeType()
                            )
                        )
                        .fileSize(
                            reduceResult.getNewFileSizeOrDefault(
                                storedMediaFilePath.length()
                            )
                        )
                        .notPublic(notPublic)
                        .build()
                );
                saveMediaFileSizeReductionResult(
                    model.getId(),
                    reduceResult,
                    NULL_STRING
                );
                eventHandlerManager.handleEvent(
                    new MediaUploadedEvent(
                        model,
                        storedMediaFilePath
                    )
                );
                byte[] responseBytes = objectMapper
                    .writeValueAsString(model)
                    .getBytes();
                response.getOutputStream().write(responseBytes);
            }
        );
    }

    public MediaModel addMedia(
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        AddMediaFromUrlRequest request,
        boolean notPublic
    ) {
        mediaValidator.validate(request);
        String mediaName = mediaService.generateMediaFileName(
            request.getUrl(),
            request.getType().toString().toLowerCase()
        );
        AddMediaModel model = requestToModelConverter.toModel(
            ownerAdminId,
            ownerUserId,
            mediaName,
            request,
            notPublic
        );
        MediaModel media = mediaService.addMedia(uploadFrom, model);
        eventHandlerManager.handleEvent(
            new MediaAddedEvent(media.getId())
        );
        return media;
    }

    public void replaceMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        long mediaId,
        Predicate<MediaModel> validMediaCondition
    ) throws Exception {
        replaceMedia(
            request,
            response,
            mediaService.getMediaById(mediaId),
            validMediaCondition
        );
    }

    public void replaceMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        String mediaName,
        Predicate<MediaModel> validMediaCondition
    ) throws Exception {
        replaceMedia(
            request,
            response,
            mediaService.getMediaByName(mediaName),
            validMediaCondition
        );
    }

    @SuppressWarnings("MethodLength")
    public void replaceMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        MediaModel media,
        Predicate<MediaModel> validMediaCondition
    ) throws Exception {
        if (media == null || !validMediaCondition.test(media)) {
            throw new ResourceNotFoundException("media");
        }
        long mediaId = media.getId();
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        FileUploader fileUploader = fileUploaderWrapper.get();
        String uploadFrom = media.getUploadFrom();
        long ownerAdminId = media.getOwnerAdminId();
        long ownerUserId = media.getOwnerUserId();
        boolean avatar = media.getType() == MediaType.AVATAR;
        if (mediaUpDownloader != null
            && mediaUpDownloader.isUploadSupported()
        ) {
            mediaUpDownloader.upload(
                MediaUploadArguments.builder()
                    .tika(tika.get())
                    .fileUploader(fileUploader)
                    .request(request)
                    .response(response)
                    .uploadFrom(uploadFrom)
                    .action(UploadAction.REPLACE)
                    .mediaId(mediaId)
                    .ownerAdminId(ownerAdminId)
                    .ownerUserId(ownerUserId)
                    .avatar(avatar)
                    .notPublic(!media.isPublicMedia())
                    .build()
            );
            return;
        }
        if (fileUploader == null) {
            throw new HttpNotAcceptableException(
                singletonMap("fileUpload", "disabled")
            );
        }
        Part filePart = request.getPart("file");
        if (filePart == null) {
            Collection<Part> parts = request.getParts();
            if (!parts.isEmpty()) {
                filePart = parts.iterator().next();
            }
        }
        FileMetadata fileMetadata = mediaValidator.validateFilePart(
            filePart,
            avatar
        );
        eventHandlerManager.handleEvent(
            MediaUploadEvent.builder()
                .uploadFrom(uploadFrom)
                .ownerAdminId(ownerAdminId)
                .ownerUserId(ownerUserId)
                .media(media)
                .fileMetadata(fileMetadata)
                .build()
        );
        //noinspection ConstantConditions
        String submittedFileName = filePart.getSubmittedFileName();
        String containerFolder = fileMetadata.getMediaType().getFolder();
        String fileName = media.getName();
        AsyncContext asyncContext = request.getAsyncContext();
        File mediaFilePath = fileSystemManager.getMediaFilePath(
            containerFolder,
            fileName
        );
        fileUploader.accept(
            asyncContext,
            filePart,
            mediaFilePath,
            settingService.getMaxUploadFileSize(),
            () -> {
                MediaFileSizeReductionResult reduceResult = reduceMediaFileSize(
                    fileMetadata.getMediaType(),
                    mediaFilePath
                );
                String storedFileName = reduceResult
                    .getNewFileNameOrDefault(fileName);
                File storedMediaFilePath = isBlank(reduceResult.getNewFileName())
                    ? mediaFilePath
                    : new File(mediaFilePath.getParentFile(), storedFileName);
                MediaModel model = mediaService.replaceMedia(
                    ReplaceMediaModel.builder()
                        .mediaId(mediaId)
                        .fileName(storedFileName)
                        .originalFileName(submittedFileName)
                        .mediaType(from(fileMetadata.getMediaType()))
                        .mimeType(
                            reduceResult.getNewFileMimeTypeOrDefault(
                                fileMetadata.getMimeType()
                            )
                        )
                        .fileSize(
                            reduceResult.getNewFileSizeOrDefault(
                                storedMediaFilePath.length()
                            )
                        )
                        .build()
                );
                saveMediaFileSizeReductionResult(
                    mediaId,
                    reduceResult,
                    fileName
                );
                eventHandlerManager.handleEvent(
                    new MediaReplacedEvent(
                        model,
                        storedMediaFilePath
                    )
                );
                byte[] responseBytes = objectMapper
                    .writeValueAsString(model)
                    .getBytes();
                response.getOutputStream().write(responseBytes);
            }
        );
    }

    public long saveMediaFile(
        MediaType mediaType,
        String mediaUrl,
        UploadFrom uploadFrom,
        long ownerAdminId,
        long ownerUserId
    ) {
        return saveMediaFileFromUrl(
            uploadFrom.toString(),
            ownerAdminId,
            ownerUserId,
            SaveMediaFileFromUrlModel.builder()
                .mediaType(mediaType.toString())
                .mediaUrl(mediaUrl)
                .build()
        );
    }

    /**
     * Saves a media file from the URL in the given model.
     *
     * <p>When an error occurs, this method catches the exception, writes a
     * warning log with the media URL and exception details, and returns {@code 0}.
     * The generated log can contain sensitive information if the URL or exception
     * message includes credentials, tokens, private paths, or other confidential
     * data. Use {@link #saveMediaFileFromUrlOrThrow(String, long, long,
     * SaveMediaFileFromUrlModel)} when you need to handle the exception yourself
     * or avoid logging sensitive information from this method.</p>
     *
     * @return the saved media ID, or {@code 0} if the URL is blank or the media
     *     file cannot be saved.
     */
    @SuppressWarnings("MethodLength")
    public long saveMediaFileFromUrl(
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        SaveMediaFileFromUrlModel model
    ) {
        try {
            return saveMediaFileFromUrlOrThrow(
                uploadFrom,
                ownerAdminId,
                ownerUserId,
                model
            );
        } catch (Exception e) {
            logger.warn(
                "can not download media from url: {}",
                model.getMediaUrl(),
                e
            );
            return ZERO_LONG;
        }
    }

    @SuppressWarnings("MethodLength")
    public long saveMediaFileFromUrlOrThrow(
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        SaveMediaFileFromUrlModel model
    ) throws Exception {
        String mediaUrl = model.getMediaUrl();
        if (isBlank(mediaUrl)) {
            return ZERO_LONG;
        }
        if (!isValidExternalUrl(mediaUrl)) {
            throw new HttpBadRequestException(
                singletonMap("mediaUrl", "invalid")
            );
        }
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        String mediaTypeText = model.getMediaType();
        boolean isNotPublic = model.isNotPublic();
        if (mediaUpDownloader != null
            && mediaUpDownloader.isUploadFromUrlSupported()
        ) {
            return mediaUpDownloader.uploadFromUrl(
                MediaUploadFromUrlArguments.builder()
                    .tika(tika.get())
                    .httpClient(httpClient)
                    .uploadFrom(uploadFrom)
                    .action(UploadAction.ADD)
                    .mediaType(mediaTypeText)
                    .mediaUrl(mediaUrl)
                    .ownerAdminId(ownerAdminId)
                    .ownerUserId(ownerUserId)
                    .notPublic(isNotPublic)
                    .build()
            );
        }
        MediaType mediaType = MediaType.ofName(mediaTypeText);
        File outFolder = fileSystemManager.getMediaFolderPath(
            mediaType
        );
        String fileName = mediaService.generateMediaFileName(
            mediaUrl,
            Uris.getFileExtensionInUrl(mediaUrl)
        );
        DownloadFileResult result = httpClient.download(
            mediaUrl,
            outFolder,
            fileName
        );
        String newFileName = result.getNewFileName();
        Path mediaFilePath = outFolder
            .toPath()
            .resolve(newFileName);
        org.apache.tika.mime.MediaType tikaMediaType;
        try (
            InputStream inputStream = Files.newInputStream(
                mediaFilePath
            )
        ) {
            tikaMediaType = tika.get().getDetector().detect(
                TikaInputStream.get(inputStream),
                new Metadata()
            );
        }
        MediaFileSizeReductionResult reduceResult = reduceMediaFileSize(
            mediaType,
            mediaFilePath.toFile()
        );
        String storedFileName = reduceResult
            .getNewFileNameOrDefault(newFileName);
        Path storedMediaFilePath = isBlank(reduceResult.getNewFileName())
            ? mediaFilePath
            : mediaFilePath.resolveSibling(storedFileName);
        MediaModel media = mediaService.addMedia(
            uploadFrom,
            AddMediaModel.builder()
                .fileName(storedFileName)
                .originalFileName(result.getOriginalFileName())
                .mediaType(mediaTypeText)
                .mimeType(
                    reduceResult.getNewFileMimeTypeOrDefault(
                        tikaMediaType.toString()
                    )
                )
                .fileSize(
                    reduceResult.getNewFileSizeOrDefault(
                        Files.size(storedMediaFilePath)
                    )
                )
                .ownerAdminId(ownerAdminId)
                .ownerUserId(ownerUserId)
                .notPublic(isNotPublic)
                .build()
        );
        saveMediaFileSizeReductionResult(
            media.getId(),
            reduceResult,
            NULL_STRING
        );
        eventHandlerManager.handleEvent(
            new MediaUploadedEvent(
                media,
                storedMediaFilePath.toFile()
            )
        );
        return media.getId();
    }

    public MediaFileSizeReductionResult reduceMediaFileSizeById(
        long mediaId,
        long expectedFileSize,
        Predicate<MediaModel> validMediaCondition
    ) {
        MediaModel media = mediaService.getMediaById(mediaId);
        if (media == null || !validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaId);
        }
        return reduceMediaFileSizeAnyway(media, expectedFileSize);
    }

    public MediaFileSizeReductionResult reduceMediaFileSizeByName(
        String mediaName,
        long expectedFileSize,
        Predicate<MediaModel> validMediaCondition
    ) {
        MediaModel media = mediaService.getMediaByName(mediaName);
        if (media == null || !validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaName);
        }
        return reduceMediaFileSizeAnyway(media, expectedFileSize);
    }

    public MediaFileSizeReductionResult reduceMediaFileSizeAnyway(
        MediaModel media,
        long expectedFileSize
    ) {
        String fileName = media.getName();
        MediaType mediaType = media.getType();
        String containerFolder = mediaType.getFolder();
        File mediaFilePath = fileSystemManager.getMediaFilePath(
            containerFolder,
            fileName
        );
        MediaFileSizeReductionResult reduceResult = reduceMediaFileSize(
            mediaType,
            mediaFilePath,
            expectedFileSize
        );
        long mediaId = media.getId();
        String storedFileName = reduceResult
            .getNewFileNameOrDefault(fileName);
        File storedMediaFilePath = isBlank(reduceResult.getNewFileName())
            ? mediaFilePath
            : new File(mediaFilePath.getParentFile(), storedFileName);
        MediaModel model = mediaService.replaceMedia(
            ReplaceMediaModel.builder()
                .mediaId(mediaId)
                .fileName(storedFileName)
                .originalFileName(media.getOriginalName())
                .mediaType(media.getType().toString())
                .mimeType(
                    reduceResult.getNewFileMimeTypeOrDefault(
                        media.getMimeType()
                    )
                )
                .fileSize(
                    reduceResult.getNewFileSizeOrDefault(
                        storedMediaFilePath.length()
                    )
                )
                .build()
        );
        saveMediaFileSizeReductionResult(
            media.getId(),
            reduceResult,
            fileName
        );
        eventHandlerManager.handleEvent(
            new MediaFileSizeReducedEvent(
                model,
                storedMediaFilePath
            )
        );
        return reduceResult;
    }

    public MediaFileSizeReductionResult reduceMediaFileSize(
        MediaType mediaType,
        File mediaFilePath
    ) {
        return reduceMediaFileSize(
            mediaType,
            mediaFilePath,
            ZERO_LONG
        );
    }

    public MediaFileSizeReductionResult reduceMediaFileSize(
        MediaType mediaType,
        File mediaFilePath,
        long expectedFileSize
    ) {
        if (!settingService.isAllowReduceMediaFileSize()) {
            return MediaFileSizeReductionResult.builder().build();
        }
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        if (mediaUpDownloader != null
            && mediaUpDownloader.isReduceMediaSupported()
        ) {
            return mediaUpDownloader.reduceMediaFileSize(
                MediaFileSizeReductionArguments.builder()
                    .mediaType(mediaType)
                    .mediaFilePath(mediaFilePath)
                    .expectedFileSize(expectedFileSize)
                    .build()
            );
        }
        MediaFileSizeReductionResult result = eventHandlerManager
            .handleEvent(
                new MediaFileSizeReductionEvent(
                    mediaType,
                    mediaFilePath,
                    expectedFileSize
                )
            );
        if (result == null) {
            result = mediaFileService.reduceMediaFileSize(
                mediaType,
                mediaFilePath,
                expectedFileSize
            );
        }
        return result;
    }

    public void updateMedia(
        long mediaId,
        UpdateMediaRequest request,
        Predicate<MediaModel> validMediaCondition
    ) {
        mediaValidator.validate(request);
        MediaModel media = mediaValidator.validateMediaId(mediaId);
        if (!validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaId);
        }
        request.setFileSize(getMediaFileSize(media));
        UpdateMediaModel model = requestToModelConverter
            .toModel(mediaId, request);
        MediaModel updatedMedia = mediaService.updateMedia(model);
        eventHandlerManager.handleEvent(
            new MediaUpdatedEvent(updatedMedia)
        );
    }

    public void updateMedia(
        long mediaId,
        UpdateMediaIncludeUrlRequest request,
        Predicate<MediaModel> validMediaCondition
    ) {
        mediaValidator.validate(request);
        MediaModel media = mediaValidator.validateMediaId(mediaId);
        if (!validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaId);
        }
        request.setFileSize(getMediaFileSize(media));
        UpdateMediaModel model = requestToModelConverter
            .toModel(mediaId, request);
        MediaModel updatedMedia = mediaService.updateMedia(model);
        eventHandlerManager.handleEvent(
            new MediaUpdatedEvent(updatedMedia)
        );
    }

    public void updateMedia(
        String mediaName,
        UpdateMediaRequest request,
        Predicate<MediaModel> validMediaCondition
    ) {
        mediaValidator.validate(request);
        MediaModel media = mediaValidator.validateMediaNameAndGet(
            mediaName
        );
        if (!validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaName);
        }
        request.setFileSize(getMediaFileSize(media));
        UpdateMediaModel model = requestToModelConverter
            .toModel(mediaName, request);
        MediaModel updatedMedia = mediaService.updateMedia(model);
        eventHandlerManager.handleEvent(
            new MediaUpdatedEvent(updatedMedia)
        );
    }

    public void updateMediaStatus(
        long mediaId,
        String status
    ) {
        MediaModel media = mediaService
            .updateMediaStatus(mediaId, status);
        eventHandlerManager.handleEvent(
            new MediaUpdatedEvent(media)
        );
    }

    public void saveMediaFileSizeReductionResult(
        long mediaId,
        MediaFileSizeReductionResult result,
        String mediaSlug
    ) {
        if (!result.isReduced()) {
            return;
        }
        String originalFileName = result.getOriginalSizeFileName();
        if (isNotBlank(originalFileName)) {
            mediaService.saveMediaOriginalSizeFileNameIfNotExists(
                mediaId,
                originalFileName
            );
            mediaService.saveMediaSlugIfNotExists(
                mediaId,
                originalFileName
            );
        }
        String newFileName = result.getNewFileName();
        if (isNotBlank(newFileName) && isNotBlank(mediaSlug)) {
            mediaService.saveMediaSlugIfNotExists(
                mediaId,
                mediaSlug
            );
        }
    }

    public long getMediaFileSize(MediaModel media) {
        String containerFolder = media.getType().getFolder();
        String fileName = media.getName();
        File mediaFilePath = fileSystemManager.getMediaFilePath(
            containerFolder,
            fileName
        );
        return mediaFilePath.length();
    }

    public void removeMediaById(
        long mediaId
    ) {
        removeMediaById(
            mediaId,
            settingService.isAllowPermanentlyDeleteMedia()
        );
    }

    public void removeMediaById(
        long mediaId,
        boolean deleteFile
    ) {
        MediaModel media = mediaService.removeMedia(mediaId);
        if (deleteFile
            && DELETED.equals(media.getStatus())
        ) {
            mediaService.removeMediaPermanently(media.getId());
            File file = fileSystemManager.getMediaFilePath(
                media.getType().getFolder(),
                media.getName()
            );
            FolderProxy.deleteFile(file);
        }
        eventHandlerManager.handleEvent(
            new MediaRemovedEvent(media)
        );
    }

    public void removeMediaByName(
        String mediaName
    ) {
        removeMediaByName(
            mediaName,
            settingService.isAllowPermanentlyDeleteMedia()
        );
    }

    public void removeMediaByName(
        String mediaName,
        boolean deleteFile
    ) {
        MediaModel media = mediaService.removeMedia(mediaName);
        if (deleteFile
            && DELETED.equals(media.getStatus())
        ) {
            mediaService.removeMediaPermanently(media.getId());
            String containerFolder = media.getType().getFolder();
            File filePath = fileSystemManager.getMediaFilePath(
                containerFolder,
                media.getName()
            );
            FolderProxy.deleteFile(filePath);
        }
        eventHandlerManager.handleEvent(
            new MediaRemovedEvent(media)
        );
    }

    public void getMediaByName(
        RequestArguments requestArguments,
        Long userId,
        String name,
        boolean exposePrivateMedia,
        Predicate<MediaModel> validMediaCondition
    ) throws Exception {
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        if (mediaUpDownloader != null
            && mediaUpDownloader.isDownloadSupported()
        ) {
            mediaUpDownloader.download(
                MediaDownloadArguments.builder()
                    .requestArguments(requestArguments)
                    .userId(userId)
                    .name(name)
                    .exposePrivateMedia(exposePrivateMedia)
                    .validMediaCondition(validMediaCondition)
                    .build()
            );
            return;
        }
        mediaValidator.validateMediaName(name);
        MediaModel media = mediaService.getMediaByName(name);
        if (media == null
            || (
                !media.isPublicMedia()
                    && !exposePrivateMedia
                    && !validMediaCondition.test(media)
            )
        ) {
            throw new MediaNotFoundException(name);
        }
        eventHandlerManager.handleEvent(
            new ValidateMediaOwnerEvent(userId, media)
        );
        eventHandlerManager.handleEvent(
            new MediaDownloadEvent(userId, media)
        );
        MediaType mediaType = media.getType();
        String mediaName = media.getName();
        File resourcePath = fileSystemManager.getMediaFilePath(
            mediaType.getFolder(),
            mediaName
        );
        if (!resourcePath.exists()) {
            throw new MediaNotFoundException(mediaName);
        }
        String extension = FolderProxy.getFileExtension(mediaName);
        ResourceRequestHandler handler = new ResourceRequestHandler(
            resourcePath.toString(),
            resourcePath.toString(),
            extension,
            inputStreamLoader,
            resourceDownloadManager
        );
        handler.handle(requestArguments);
    }

    public MediaDetailsModel getMediaDetailsById(
        Long userId,
        long mediaId,
        Predicate<MediaModel> validMediaCondition
    ) {
        MediaModel media = mediaService.getMediaById(mediaId);
        if (media == null || !validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaId);
        }
        return getMediaDetailsAnyway(userId, media);
    }

    public MediaDetailsModel getMediaDetailsByName(
        Long userId,
        String mediaName,
        Predicate<MediaModel> validMediaCondition
    ) {
        MediaModel media = mediaService.getMediaByName(mediaName);
        if (media == null || !validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaName);
        }
        return getMediaDetailsAnyway(userId, media);
    }

    public MediaDetailsModel getMediaDetailsAnyway(
        Long userId,
        MediaModel media
    ) {
        eventHandlerManager.handleEvent(
            new ValidateMediaOwnerEvent(userId, media)
        );
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        MediaDetailsModel mediaDetails = null;
        if (mediaUpDownloader != null) {
            mediaDetails = mediaUpDownloader
                .getMediaDetails(userId, media);
        }
        if (mediaDetails == null) {
            mediaDetails = eventHandlerManager.handleEvent(
                new GetMediaDetailsEvent(userId, media)
            );
        }
        if (mediaDetails == null) {
            mediaDetails = getMediaDetails(media);
        }
        return mediaDetails;
    }

    public MediaDetailsModel getMediaDetails(
        MediaModel media
    ) {
        int width = ZERO;
        int height = ZERO;
        long size;
        MediaType mediaType = media.getType();
        if (mediaType == MediaType.IMAGE
            || mediaType == MediaType.AVATAR
        ) {
            ImageSize imageSize = mediaService.getMediaImageSizeOrNull(
                media.getName(),
                media.getType()
            );
            if (imageSize == null) {
                File mediaFilePath = eventHandlerManager.handleEvent(
                    new GetMediaFilePathEvent(media)
                );
                if (mediaFilePath != null) {
                    imageSize = mediaService.getMediaImageSizeOrDefault(
                        mediaFilePath
                    );
                }
            }
            if (imageSize == null) {
                imageSize = ImageSize.ZERO;
            }
            width = imageSize.getWidth();
            height = imageSize.getHeight();
            size = imageSize.getSize();
        } else {
            size = mediaService.getMediaFileLengthOrNegative(
                mediaType,
                media.getName()
            );
            if (size < ZERO) {
                File mediaFilePath = eventHandlerManager.handleEvent(
                    new GetMediaFilePathEvent(media)
                );
                if (mediaFilePath != null) {
                    size = mediaService.getMediaFileLengthOrZero(
                        mediaFilePath
                    );
                }
            }
        }
        if (size < ZERO) {
            size = ZERO;
        }
        return fromMediaModel(media)
            .width(width)
            .height(height)
            .size(size)
            .originalSizeFileName(
                mediaService.getOriginalSizeFileNameByMediaId(
                    media.getId()
                )
            )
            .build();
    }

    public PaginationModel<MediaResponse> getMediaList(
        MediaFilter filter,
        String sortOrder,
        String nextPageToken,
        String prevPageToken,
        boolean lastPage,
        int limit
    ) {
        commonValidator.validatePageSize(limit);
        PaginationModel<MediaModel> pagination = getPaginationModelBySortOrder(
            paginationMediaService,
            mediaPaginationParameterConverter,
            filter,
            sortOrder,
            nextPageToken,
            prevPageToken,
            lastPage,
            limit
        );
        return pagination.map(modelToResponseConverter::toResponse);
    }
}
