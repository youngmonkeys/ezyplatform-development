package org.youngmonkeys.ezyplatform.admin.model;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SearchResultModel {
    private String resultType;
    private String title;
    private String url;
    private String content;
    private int searchPriority;
    private int resultWeight;

    public static final String RESULT_TYPE_URL = "URL";
}
