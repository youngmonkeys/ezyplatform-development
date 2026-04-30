package org.youngmonkeys.ezyplatform.service;

import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.data.MediaFileSizeReductionResult;
import org.youngmonkeys.ezyplatform.entity.MediaType;

import java.io.File;

@AllArgsConstructor
public class MediaFileService {

    private final ImageFileService imageFileService;

    public MediaFileSizeReductionResult reduceImageFileSize(
        MediaType mediaType,
        File mediaFilePath
    ) {
        if (mediaType == MediaType.AVATAR
            || mediaType == MediaType.IMAGE
        ) {
            return imageFileService.reduceImageFileSize(
                mediaFilePath
            );
        }
        return MediaFileSizeReductionResult.NO;
    }
}
