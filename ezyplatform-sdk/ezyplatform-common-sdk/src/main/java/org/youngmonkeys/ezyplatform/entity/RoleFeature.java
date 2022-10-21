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
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@IdClass(RoleFeatureId.class)
@Table(name = "ezy_role_features")
@AllArgsConstructor
@NoArgsConstructor
public class RoleFeature {
    @Id
    @Column(name = "role_id")
    private long roleId;

    @Id
    private String target;

    @Id
    private String feature;

    @Id
    @Column(name = "feature_uri")
    private String featureUri;

    @Id
    @Column(name = "feature_method")
    private String featureMethod;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Transient
    public RoleFeatureId identifier() {
        return new RoleFeatureId(
            roleId,
            target,
            feature,
            featureUri,
            featureMethod
        );
    }
}
