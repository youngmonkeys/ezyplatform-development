/*
 * Copyright 2023 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.pagination;

public enum DataMappingPaginationSortOrder {
    DISPLAY_ORDER_ASC_FROM_DATA_ID_ASC,
    DISPLAY_ORDER_DESC_FROM_DATA_ID_DESC,
    DISPLAY_ORDER_ASC_TO_DATA_ID_ASC,
    DISPLAY_ORDER_DESC_TO_DATA_ID_DESC,
    MAPPED_AT_ASC_FROM_DATA_ID_ASC,
    MAPPED_AT_DESC_FROM_DATA_ID_DESC,
    MAPPED_AT_ASC_TO_DATA_ID_ASC,
    MAPPED_AT_DESC_TO_DATA_ID_DESC
}
