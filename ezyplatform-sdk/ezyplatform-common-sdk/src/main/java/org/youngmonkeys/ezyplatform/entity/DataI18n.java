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

import javax.persistence.*;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_DATA_I18N;

@Getter
@Setter
@ToString
@Entity
@IdClass(DataI18nId.class)
@Table(name = TABLE_NAME_DATA_I18N)
@AllArgsConstructor
@NoArgsConstructor
public class DataI18n {
    @Id
    @Column(name = "data_type")
    private String dataType;

    @Id
    @Column(name = "data_id")
    private long dataId;

    @Id
    @Column(name = "language")
    private String language;

    @Id
    @Column(name = "field_name")
    private String fieldName;

    @Column(name = "field_value")
    private String fieldValue;

    public DataI18nId identifier() {
        return new DataI18nId(
            dataType,
            dataId,
            language,
            fieldName
        );
    }
}
