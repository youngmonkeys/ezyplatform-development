/*
 * Copyright 2026 youngmonkeys.org
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

package org.youngmonkeys.ezyplatform.signature;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.security.PublicKey;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class PackageSigningKeyList {
    private final long version;
    private final long issuedAt;
    private final long expiresAt;
    private final Map<PackageSigningKeyRole, List<PublicKey>> keysByRole;
    private final String jws;

    public List<PublicKey> getKeys(PackageSigningKeyRole role) {
        List<PublicKey> keys = keysByRole.get(role);
        return keys != null ? keys : Collections.emptyList();
    }

    public PackageSignatureVerifier getVerifier(PackageType packageType) {
        return new PackageSignatureVerifier(
            getKeys(PackageSigningKeyRole.of(packageType))
        );
    }

    public boolean isExpired(long now) {
        return expiresAt <= now;
    }
}
