package org.youngmonkeys.ezyplatform.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SaveLinkModel {
    private String linkUri;
    private String linkType;
    private long linkImageId;
    private String description;
}
