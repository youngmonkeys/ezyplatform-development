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
import com.tvd12.ezyfox.stream.EzyInputStreamLoader;
import com.tvd12.ezyfox.util.EzyLoggable;
import com.tvd12.ezyhttp.core.exception.HttpNotAcceptableException;
import com.tvd12.ezyhttp.core.resources.ResourceDownloadManager;
import com.tvd12.ezyhttp.server.core.handler.ResourceRequestHandler;
import com.tvd12.ezyhttp.server.core.request.RequestArguments;
import com.tvd12.ezyhttp.server.core.resources.FileUploader;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.converter.HttpRequestToModelConverter;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.data.ImageSize;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.entity.UploadFrom;
import org.youngmonkeys.ezyplatform.exception.MediaNotFoundException;
import org.youngmonkeys.ezyplatform.io.FolderProxy;
import org.youngmonkeys.ezyplatform.manager.FileSystemManager;
import org.youngmonkeys.ezyplatform.model.*;
import org.youngmonkeys.ezyplatform.pagination.PaginationModelFetchers;
import org.youngmonkeys.ezyplatform.request.UpdateMediaRequest;
import org.youngmonkeys.ezyplatform.service.MediaService;
import org.youngmonkeys.ezyplatform.service.PaginationService;
import org.youngmonkeys.ezyplatform.service.SettingService;
import org.youngmonkeys.ezyplatform.validator.CommonValidator;
import org.youngmonkeys.ezyplatform.validator.MediaValidator;

import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.Collections.singletonMap;

@AllArgsConstructor
public class MediaControllerService extends EzyLoggable {

    private final MediaService mediaService;
    private final SettingService settingService;
    @SuppressWarnings("rawtypes")
    private final PaginationService paginationMediaService;
    private final ObjectMapper objectMapper;
    private final EzyInputStreamLoader inputStreamLoader;
    private final EzySingletonFactory singletonFactory;
    private final CommonValidator commonValidator;
    private final MediaValidator mediaValidator;
    private final FileSystemManager fileSystemManager;
    private final ResourceDownloadManager resourceDownloadManager;
    private final HttpRequestToModelConverter requestToModelConverter;

    public void addMedia(
        HttpServletRequest request,
        HttpServletResponse response,
        UploadFrom uploadFrom,
        long ownerId,
        boolean avatar,
        boolean notPublic
    ) throws Exception {
        FileUploader fileUploader = singletonFactory.getSingletonCast(
            FileUploader.class
        );
        if (fileUploader == null) {
            throw new HttpNotAcceptableException(
                singletonMap("fileUpload", "disabled")
            );
        }
        Part filePart = request.getPart("file");
        if (filePart == null) {
            Collection<Part> parts = request.getParts();
            if (parts.size() > 0) {
                filePart = parts.iterator().next();
            }
        }
        FileMetadata fileMetadata = mediaValidator.validateFilePart(
            filePart,
            avatar
        );
        String submittedFileName = filePart.getSubmittedFileName();
        String fileExtension = fileMetadata.getExtension();
        String containerFolder = fileMetadata.getMediaType().getFolder();
        String newFileName = mediaService.generateMediaFileName(
            submittedFileName,
            fileExtension
        );
        AsyncContext asyncContext = request.getAsyncContext();
        fileUploader.accept(
            asyncContext,
            filePart,
            fileSystemManager.getMediaFilePath(containerFolder, newFileName),
            settingService.getMaxUploadFileSize(),
            () -> {
                MediaModel model = saveMediaInformation(
                    uploadFrom,
                    ownerId,
                    submittedFileName,
                    newFileName,
                    fileMetadata,
                    notPublic
                );
                byte[] responseBytes = mediaAddResponse(model).getBytes();
                response.getOutputStream().write(responseBytes);
            }
        );
    }

    public void updateMedia(
        long mediaId,
        UpdateMediaRequest request
    ) {
        UpdateMediaModel model = requestToModelConverter
            .toModel(mediaId, request);
        mediaService.updateMedia(model);
    }

    public void updateMedia(
        long ownerId,
        String mediaName,
        UpdateMediaRequest request
    ) {
        UpdateMediaModel model = requestToModelConverter
            .toModel(mediaName, request);
        mediaService.updateMedia(ownerId, model);
    }

    public void removeMedia(long mediaId) {
        MediaModel mediaModel = mediaService.removeMedia(mediaId);
        File file = fileSystemManager.getMediaFilePath(
            mediaModel.getType().getFolder(),
            mediaModel.getName()
        );
        FolderProxy.deleteFile(file);
    }

    public void removeMedia(
        long ownerId,
        String mediaName,
        boolean deleteFile
    ) {
        MediaModel media = mediaService.removeMedia(ownerId, mediaName);
        if (deleteFile) {
            String containerFolder = media.getType().getFolder();
            File filePath = fileSystemManager.getMediaFilePath(
                containerFolder,
                media.getName()
            );
            FolderProxy.deleteFile(filePath);
        }
    }

    public void getMedia(
        RequestArguments requestArguments,
        String name
    ) throws Exception {
        getMedia(requestArguments, name, true);
    }

    public void getMedia(
        RequestArguments requestArguments,
        String name,
        boolean exposePrivateMedia
    ) throws Exception {
        getMedia(
            requestArguments,
            name,
            exposePrivateMedia,
            media -> true
        );
    }

    public void getMedia(
        RequestArguments requestArguments,
        String name,
        boolean exposePrivateMedia,
        Predicate<MediaModel> validMediaCondition
    ) throws Exception {
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
        MediaType mediaType = media.getType();
        File resourcePath = fileSystemManager.getMediaFilePath(
            mediaType.getFolder(),
            name
        );
        if (!resourcePath.exists()) {
            throw new MediaNotFoundException(name);
        }
        String extension = FolderProxy.getFileExtension(name);
        ResourceRequestHandler handler = new ResourceRequestHandler(
            resourcePath.toString(),
            resourcePath.toString(),
            extension,
            inputStreamLoader,
            resourceDownloadManager
        );
        handler.handle(requestArguments);
    }

    public MediaDetailsModel getMediaDetails(
        long mediaId
    ) throws IOException {
        MediaModel media = mediaService.getMediaById(mediaId);
        if (media == null) {
            throw new MediaNotFoundException(mediaId);
        }
        return getMediaDetails(media);
    }

    public MediaDetailsModel getMediaDetails(
        long ownerId,
        String mediaName
    ) throws IOException {
        MediaModel media = mediaService.getMediaByName(mediaName);
        if (media == null || media.getOwnerUserId() != ownerId) {
            throw new MediaNotFoundException(mediaName);
        }
        return getMediaDetails(media);
    }

    public MediaDetailsModel getMediaDetails(
        MediaModel media
    ) throws IOException {
        int width = 0;
        int height = 0;
        long size;
        MediaType mediaType = media.getType();
        if (mediaType == MediaType.IMAGE
            || mediaType == MediaType.AVATAR
        ) {
            ImageSize imageSize = mediaService.getMediaImageSize(
                media.getName(),
                media.getType()
            );
            width = imageSize.getWidth();
            height = imageSize.getHeight();
            size = imageSize.getSize();
        } else {
            size = mediaService.getMediaFileLength(
                mediaType,
                media.getName()
            );
        }
        return MediaDetailsModel.from(media)
            .width(width)
            .height(height)
            .size(size)
            .build();
    }

    public String getMediaName(long mediaId) {
        return mediaService.getMediaName(mediaId);
    }

    public MediaModel getMediaById(long mediaId) {
        return mediaService.getMediaById(mediaId);
    }

    public MediaModel getMediaByName(String name) {
        return mediaService.getMediaByName(name);
    }

    public PaginationModel<MediaModel> getMediaList(
        String nextPageToken,
        String prevPageToken,
        boolean lastPage,
        int limit
    ) {
        commonValidator.validatePageSize(limit);
        return PaginationModelFetchers.getPaginationModel(
            paginationMediaService,
            nextPageToken,
            prevPageToken,
            lastPage,
            limit
        );
    }

    public PaginationModel<MediaModel> getMediaList(
        long ownerId,
        String nextPageToken,
        String prevPageToken,
        boolean lastPage,
        int limit
    ) {
        commonValidator.validatePageSize(limit);
        return PaginationModelFetchers.getPaginationModel(
            paginationMediaService,
            ownerId,
            nextPageToken,
            prevPageToken,
            lastPage,
            limit
        );
    }

    private MediaModel saveMediaInformation(
        UploadFrom uploadFrom,
        long ownerId,
        String submittedFileName,
        String fileName,
        FileMetadata fileMetadata,
        boolean notPublic
    ) {
        return mediaService.addMedia(
            AddMediaModel.builder()
                .ownerId(ownerId)
                .fileName(fileName)
                .originalFileName(submittedFileName)
                .mediaType(fileMetadata.getMediaType())
                .mimeType(fileMetadata.getMimeType())
                .notPublic(notPublic)
                .build(),
            uploadFrom
        );
    }

    public List<MediaModel> getMediaListByIds(Collection<Long> mediaIds) {
        return mediaService.getMediaListByIds(mediaIds);
    }

    public Map<Long, MediaModel> getMediaMapByIds(Collection<Long> mediaIds) {
        return mediaService.getMediaMapByIds(mediaIds);
    }

    private String mediaAddResponse(MediaModel model) throws IOException {
        return objectMapper.writeValueAsString(model);
    }
}
