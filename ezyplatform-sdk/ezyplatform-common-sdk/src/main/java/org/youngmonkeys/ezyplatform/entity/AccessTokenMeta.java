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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

import static org.youngmonkeys.ezyplatform.constant.CommonTableNames.TABLE_NAME_ACCESS_TOKEN_META;

@Getter
@Setter
@ToString
@Entity
@Table(name = TABLE_NAME_ACCESS_TOKEN_META)
@AllArgsConstructor
@NoArgsConstructor
public class AccessTokenMeta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String target;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "access_token_full")
    private String accessTokenFull;

    @Column(name = "parent_id")
    private long parentId;

    @Column(name = "token_type")
    private String tokenType;

    private String algorithm;

    private String scope;

    private String issuer;

    @Column(name = "tenant_id")
    private String tenantId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "device_id")
    private String deviceId;

    @Column(name = "client_secret")
    private String clientSecret;

    @Column(name = "grant_type")
    private String grantType;

    private String kid;

    @Column(name = "jwks_uri")
    private String jwksUri;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "private_key")
    private String privateKey;

    private String audience;

    @Column(name = "not_before")
    private LocalDateTime notBefore;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
