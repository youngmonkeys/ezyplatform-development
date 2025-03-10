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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigInteger;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_DATA_META;

@Setter
@Getter
@Entity
@Table(name = TABLE_NAME_DATA_META)
@AllArgsConstructor
@NoArgsConstructor
public class DataMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "data_type")
    private String dataType;

    @Column(name = "data_id")
    private long dataId;

    @Column(name = "meta_key")
    private String metaKey;

    @Column(name = "meta_value")
    private String metaValue;

    @Column(name = "meta_number_value")
    private BigInteger metaNumberValue;

    @Column(name = "meta_text_value")
    private String metaTextValue;
}
