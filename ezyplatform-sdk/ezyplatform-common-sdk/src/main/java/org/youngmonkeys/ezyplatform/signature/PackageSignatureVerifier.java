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

import java.security.PublicKey;
import java.util.Collection;
import java.util.Map;

import static org.youngmonkeys.ezyplatform.signature.PackageSignatures.JWS_TYPE;

public class PackageSignatureVerifier {

    private final Map<String, PublicKey> trustedKeyById;

    public PackageSignatureVerifier(Collection<PublicKey> trustedKeys) {
        this.trustedKeyById = PackageSigningKeys.toKeyIdMap(trustedKeys);
    }

    public boolean isTrustedKeyId(String keyId) {
        return trustedKeyById.containsKey(keyId);
    }

    public PackageManifest verify(
        String jws,
        PackageType expectedType,
        String expectedName,
        String expectedVersion
    ) {
        PackageManifest manifest = verify(jws);
        PackageManifests.validateExpected(
            manifest,
            expectedType,
            expectedName,
            expectedVersion
        );
        return manifest;
    }

    public PackageManifest verify(String jws) {
        return PackageManifests.fromMap(
            PackageSignatures.verifyJws(jws, JWS_TYPE, trustedKeyById)
        );
    }
}
