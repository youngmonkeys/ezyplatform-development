/*
 * Copyright 2025 youngmonkeys.org
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
import java.math.BigDecimal;
import java.math.BigInteger;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_UNIQUE_DATA;

@Getter
@Setter
@ToString
@Entity
@IdClass(UniqueDataId.class)
@Table(name = TABLE_NAME_UNIQUE_DATA)
@AllArgsConstructor
@NoArgsConstructor
public class UniqueData {
    @Id
    @Column(name = "data_type")
    private String dataType;

    @Id
    @Column(name = "data_id")
    private long dataId;

    @Id
    @Column(name = "unique_key")
    private String uniqueKey;

    @Column(name = "text_value")
    private String textValue;

    @Column(name = "number_value")
    private BigInteger numberValue;

    @Column(name = "decimal_value")
    private BigDecimal decimalValue;

    private String metadata;

    public UniqueDataId identifier() {
        return new UniqueDataId(
            dataType,
            dataId,
            uniqueKey
        );
    }
}
