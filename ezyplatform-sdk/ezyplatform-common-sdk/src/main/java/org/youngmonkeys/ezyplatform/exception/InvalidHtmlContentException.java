package org.youngmonkeys.ezyplatform.exception;

import com.tvd12.ezyfox.util.EzyMapBuilder;
import lombok.Getter;

@Getter
public class InvalidHtmlContentException extends BadRequestException {

    private final String errorTag;
    private final String errorCode;
    private final int stoppedAtIndex;
    private final String errorNearBy;

    public InvalidHtmlContentException(
        String errorTag,
        String errorCode,
        int stoppedAtIndex,
        String errorNearBy
    ) {
        super(
            EzyMapBuilder
                .mapBuilder()
                .put("content", "invalid")
                .put("errorTag", errorTag)
                .put("errorCode", errorCode)
                .put("stoppedAtIndex", stoppedAtIndex)
                .put("errorNearBy", errorNearBy)
                .toMap()
        );
        this.errorTag = errorTag;
        this.errorCode = errorCode;
        this.stoppedAtIndex = stoppedAtIndex;
        this.errorNearBy = errorNearBy;
    }
}
