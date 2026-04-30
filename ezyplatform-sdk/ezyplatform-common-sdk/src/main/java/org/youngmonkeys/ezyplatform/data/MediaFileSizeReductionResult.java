package org.youngmonkeys.ezyplatform.data;

import lombok.Builder;
import lombok.Getter;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

@Getter
@Builder
public class MediaFileSizeReductionResult {
    private boolean reduced;
    private String originalSizeFileName;
    private String newFileMimeType;
    private long newFileSize;

    public static MediaFileSizeReductionResult NO =
        MediaFileSizeReductionResult.builder().build();

    public String getNewFileMimeTypeOrDefault(
        String defaultValue
    ) {
        return isBlank(newFileMimeType)
            ? defaultValue
            : newFileMimeType;
    }

    public long getNewFileSizeOrDefault(
        long defaultValue
    ) {
        return newFileSize <= ZERO_LONG
            ? defaultValue
            : newFileSize;
    }
}
