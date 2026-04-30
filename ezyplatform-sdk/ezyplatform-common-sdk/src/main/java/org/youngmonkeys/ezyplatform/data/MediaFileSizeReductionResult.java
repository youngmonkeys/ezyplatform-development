package org.youngmonkeys.ezyplatform.data;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MediaFileSizeReductionResult {
    private boolean reduced;
    private String originalSizeFileName;
    private String newFileMimeType;
    private long newFileSize;

    public static MediaFileSizeReductionResult NO =
        MediaFileSizeReductionResult.builder().build();
}
