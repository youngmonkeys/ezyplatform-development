package org.youngmonkeys.ezyplatform.admin.service;

import org.youngmonkeys.ezyplatform.admin.model.SearchResultModel;

import java.util.List;

public interface AdminSearchService {

    List<SearchResultModel> search(String keyword);

    default int priority() {
        return 0;
    }
}
