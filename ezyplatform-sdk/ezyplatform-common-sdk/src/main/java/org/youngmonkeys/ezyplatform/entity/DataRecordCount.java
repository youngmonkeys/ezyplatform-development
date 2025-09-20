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

package org.youngmonkeys.ezyplatform.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_DATA_RECORD_COUNT;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_DATA_RECORD_COUNT)
@AllArgsConstructor
@NoArgsConstructor
public class DataRecordCount {
    @Id
    @Column(name = "data_type")
    private String dataType;

    @Column(name = "record_count")
    private long recordCount;

    @Column(name = "last_record_id")
    private long lastRecordId;

    @Column(name = "last_counted_at")
    private LocalDateTime lastCountedAt;

    @Column(name = "query_string")
    private String queryString;

    @Column(name = "query_type")
    private String queryType;

    private String parameters;

    @Column(name = "parameters_type")
    private String parameterType;
}
