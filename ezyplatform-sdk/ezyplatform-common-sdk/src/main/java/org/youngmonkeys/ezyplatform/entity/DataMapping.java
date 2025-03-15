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
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_DATA_MAPPING;

@Getter
@Setter
@ToString
@Entity
@IdClass(DataMappingId.class)
@Table(name = TABLE_NAME_DATA_MAPPING)
@AllArgsConstructor
@NoArgsConstructor
public class DataMapping {
    @Id
    @Column(name = "mapping_name")
    private String mappingName;

    @Id
    @Column(name = "from_data_id")
    private long fromDataId;

    @Id
    @Column(name = "to_data_id")
    private long toDataId;

    @Column(name = "display_order")
    private int displayOrder;

    private BigInteger quantity;

    @Column(name = "remaining_quantity")
    private BigInteger remainingQuantity;

    @Column(name = "decimal_data")
    private BigDecimal decimalData;

    @Column(name = "text_data")
    private String textData;

    private String metadata;

    @Column(name = "mapped_at")
    private LocalDateTime mappedAt;

    public DataMappingId identifier() {
        return new DataMappingId(
            mappingName,
            fromDataId,
            toDataId
        );
    }
}
