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
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.entity.UploadAction;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;
import org.youngmonkeys.ezyplatform.event.*;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.exception.ResourceNotFoundException;
import org.youngmonkeys.ezyplatform.io.FolderProxy;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.media.MediaDownloadArguments;
import org.youngmonkeys.ezyplatform.media.MediaUpDownloader;
import org.youngmonkeys.ezyplatform.media.MediaUpDownloaderManager;
import org.youngmonkeys.ezyplatform.media.MediaUploadArguments;
import org.youngmonkeys.ezyplatform.model.*;
import org.youngmonkeys.ezyplatform.pagination.MediaFilter;
import org.youngmonkeys.ezyplatform.pagination.MediaPaginationParameterConverter;
import org.youngmonkeys.ezyplatform.request.AddMediaFromUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaIncludeUrlRequest;
import org.youngmonkeys.ezyplatform.request.UpdateMediaRequest;
import org.youngmonkeys.ezyplatform.response.MediaResponse;
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
import java.util.Collection;
import java.util.function.Predicate;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static java.util.Collections.singletonMap;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.*;
import static org.youngmonkeys.ezyplatform.model.MediaDetailsModel.fromMediaModel;
import static org.youngmonkeys.ezyplatform.pagination.PaginationModelFetchers.getPaginationModelBySortOrder;
import static org.youngmonkeys.ezyplatform.util.Strings.from;

public class MediaControllerService extends EzyLoggable {

    private final HttpClient httpClient;
    private final EventHandlerManager eventHandlerManager;
    private final FileSystemManager fileSystemManager;
    private final EzyInputStreamLoader inputStreamLoader;
    private final MediaUpDownloaderManager mediaUpDownloaderManager;
    private final ObjectMapper objectMapper;
    private final ResourceDownloadManager resourceDownloadManager;
    private final MediaService mediaService;
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

    @SuppressWarnings("MethodLength")
    public void addMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        boolean avatar,
        boolean notPublic
    ) throws Exception {
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        FileUploader fileUploader = fileUploaderWrapper.get();
        if (mediaUpDownloader != null) {
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
            settingService.getMaxUploadFileSize(),
            () -> {
                MediaModel model = saveMediaInformation(
                    uploadFrom,
                    ownerAdminId,
                    ownerUserId,
                    submittedFileName,
                    newFileName,
                    fileMetadata,
                    mediaFilePath.length(),
                    notPublic
                );
                eventHandlerManager.handleEvent(
                    new MediaUploadedEvent(
                        model,
                        mediaFilePath
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

    @SuppressWarnings("MethodLength")
    public void replaceMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        long mediaId,
        Predicate<MediaModel> validMediaCondition
    ) throws Exception {
        MediaModel media = mediaService.getMediaById(mediaId);
        if (media == null || !validMediaCondition.test(media)) {
            throw new ResourceNotFoundException("media");
        }
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        FileUploader fileUploader = fileUploaderWrapper.get();
        String uploadFrom = media.getUploadFrom();
        long ownerAdminId = media.getOwnerAdminId();
        long ownerUserId = media.getOwnerUserId();
        boolean avatar = media.getType() == MediaType.AVATAR;
        if (mediaUpDownloader != null) {
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
                .mediaId(mediaId)
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
                MediaModel model = replaceMediaInformation(
                    mediaId,
                    submittedFileName,
                    fileMetadata,
                    mediaFilePath.length()
                );
                eventHandlerManager.handleEvent(
                    new MediaUploadedEvent(
                        model,
                        mediaFilePath
                    )
                );
                byte[] responseBytes = objectMapper
                    .writeValueAsString(model)
                    .getBytes();
                response.getOutputStream().write(responseBytes);
            }
        );
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
        mediaService.updateMedia(model);
        eventHandlerManager.handleEvent(
            new MediaUpdatedEvent(model.getMediaId())
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
        mediaService.updateMedia(model);
        eventHandlerManager.handleEvent(
            new MediaUpdatedEvent(model.getMediaId())
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
        mediaService.updateMedia(model);
        eventHandlerManager.handleEvent(
            new MediaUpdatedEvent(model.getMediaId())
        );
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
        long mediaId,
        boolean deleteFile
    ) {
        MediaModel media = mediaService.removeMedia(mediaId);
        if (deleteFile
            && DELETED.equals(media.getStatus())
        ) {
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
        String mediaName,
        boolean deleteFile
    ) {
        MediaModel media = mediaService.removeMedia(mediaName);
        if (deleteFile
            && DELETED.equals(media.getStatus())
        ) {
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

    public long saveMediaFileFromUrl(
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        SaveMediaFileFromUrlModel model
    ) {
        String mediaUrl = model.getMediaUrl();
        if (isBlank(mediaUrl)) {
            return ZERO_LONG;
        }
        String mediaTypeText = model.getMediaType();
        try {
            File outFolder = fileSystemManager.getMediaFolderPath(
                MediaType.ofName(mediaTypeText)
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
            org.apache.tika.mime.MediaType tikaMediaType;
            try (
                InputStream inputStream = Files.newInputStream(
                    outFolder
                        .toPath()
                        .resolve(result.getNewFileName())
                )
            ) {
                tikaMediaType = tika.get().getDetector().detect(
                    TikaInputStream.get(inputStream),
                    new Metadata()
                );
            }
            MediaModel media = mediaService.addMedia(
                uploadFrom,
                AddMediaModel.builder()
                    .fileName(result.getNewFileName())
                    .originalFileName(result.getOriginalFileName())
                    .mediaType(mediaTypeText)
                    .mimeType(tikaMediaType.toString())
                    .ownerAdminId(ownerAdminId)
                    .ownerUserId(ownerUserId)
                    .build()
            );
            return media.getId();
        } catch (Exception e) {
            logger.info("can not download media from url: {}", mediaUrl, e);
            return ZERO_LONG;
        }
    }

    public void getMediaByName(
        RequestArguments requestArguments,
        String name,
        boolean exposePrivateMedia,
        Predicate<MediaModel> validMediaCondition
    ) throws Exception {
        String mediaUploaderName = settingService
            .getMediaUpDownloaderName();
        MediaUpDownloader mediaUpDownloader = mediaUpDownloaderManager
            .getMediaUpDownloaderByName(mediaUploaderName);
        if (mediaUpDownloader != null) {
            mediaUpDownloader.download(
                MediaDownloadArguments.builder()
                    .requestArguments(requestArguments)
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
            new MediaDownloadEvent(media)
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
        long mediaId,
        Predicate<MediaModel> validMediaCondition
    ) {
        MediaModel media = mediaService.getMediaById(mediaId);
        if (media == null || !validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaId);
        }
        return getMediaDetails(media);
    }

    public MediaDetailsModel getMediaDetailsByName(
        String mediaName,
        Predicate<MediaModel> validMediaCondition
    ) {
        MediaModel media = mediaService.getMediaByName(mediaName);
        if (media == null || !validMediaCondition.test(media)) {
            throw new MediaNotFoundException(mediaName);
        }
        return getMediaDetails(media);
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

    private MediaModel saveMediaInformation(
        String uploadFrom,
        long ownerAdminId,
        long ownerUserId,
        String submittedFileName,
        String fileName,
        FileMetadata fileMetadata,
        long fileSize,
        boolean notPublic
    ) {
        return mediaService.addMedia(
            uploadFrom,
            AddMediaModel.builder()
                .ownerAdminId(ownerAdminId)
                .ownerUserId(ownerUserId)
                .fileName(fileName)
                .originalFileName(submittedFileName)
                .mediaType(fileMetadata.getMediaType().toString())
                .mimeType(fileMetadata.getMimeType())
                .fileSize(fileSize)
                .notPublic(notPublic)
                .build()
        );
    }

    private MediaModel replaceMediaInformation(
        long mediaId,
        String submittedFileName,
        FileMetadata fileMetadata,
        long fileSize
    ) {
        return mediaService.replaceMedia(
            ReplaceMediaModel.builder()
                .mediaId(mediaId)
                .originalFileName(submittedFileName)
                .mediaType(from(fileMetadata.getMediaType()))
                .mimeType(fileMetadata.getMimeType())
                .fileSize(fileSize)
                .build()
        );
    }
}
