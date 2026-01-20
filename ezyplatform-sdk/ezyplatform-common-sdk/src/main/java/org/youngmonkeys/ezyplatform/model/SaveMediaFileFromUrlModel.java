package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveMediaFileFromUrlModel {
    private String mediaType;
    private String mediaUrl;
}
