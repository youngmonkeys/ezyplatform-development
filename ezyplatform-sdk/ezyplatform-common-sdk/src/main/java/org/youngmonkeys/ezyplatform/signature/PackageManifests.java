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

import org.youngmonkeys.ezyplatform.exception.InvalidPackageSignatureException;
import org.youngmonkeys.ezyplatform.util.Hashes;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import static com.tvd12.ezyfox.io.EzyStrings.isBlank;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.LATEST;
import static org.youngmonkeys.ezyplatform.util.Versions.compareVersions;

public final class PackageManifests {

    public static final String FIELD_TYPE = "type";
    public static final String FIELD_NAME = "name";
    public static final String FIELD_VERSION = "version";
    public static final String FIELD_FILE_NAME = "fileName";
    public static final String FIELD_SIZE = "size";
    public static final String FIELD_SHA256 = "sha256";
    public static final String FIELD_FILES_SHA256 = "filesSha256";
    public static final String FIELD_ISSUED_AT = "issuedAt";

    private static final Pattern SHA256_PATTERN = Pattern.compile(
        "^[0-9a-f]{64}$"
    );

    private PackageManifests() {}

    public static PackageManifest create(
        PackageType type,
        String name,
        String version,
        File file,
        long issuedAt
    ) throws IOException {
        if (!file.isFile()) {
            throw new IOException("package file not found: " + file);
        }
        return create(
            type,
            name,
            version,
            file,
            PackageFiles.sha256Of(PackageFiles.readZipFileHashesText(file)),
            issuedAt
        );
    }

    public static PackageManifest create(
        PackageType type,
        String name,
        String version,
        File file,
        String filesSha256,
        long issuedAt
    ) throws IOException {
        if (!file.isFile()) {
            throw new IOException("package file not found: " + file);
        }
        PackageManifest manifest = PackageManifest.builder()
            .type(type)
            .name(name)
            .version(version)
            .fileName(file.getName())
            .size(file.length())
            .sha256(Hashes.sha256Hex(file))
            .filesSha256(filesSha256)
            .issuedAt(issuedAt)
            .build();
        validate(manifest);
        return manifest;
    }

    public static void validate(PackageManifest manifest) {
        PackageType type = manifest.getType();
        if (type == null) {
            throw new InvalidPackageSignatureException("invalidType");
        }
        String name = manifest.getName();
        if (isBlank(name)) {
            throw new InvalidPackageSignatureException("invalidName");
        }
        String fixedPackageName = type.getFixedPackageName();
        if (fixedPackageName != null && !fixedPackageName.equals(name)) {
            throw new InvalidPackageSignatureException("invalidName");
        }
        String version = manifest.getVersion();
        if (isBlank(version) || LATEST.equals(version)) {
            throw new InvalidPackageSignatureException("invalidVersion");
        }
        if (manifest.getSize() < 0) {
            throw new InvalidPackageSignatureException("invalidSize");
        }
        String sha256 = manifest.getSha256();
        if (sha256 == null || !SHA256_PATTERN.matcher(sha256).matches()) {
            throw new InvalidPackageSignatureException("invalidSha256");
        }
        String filesSha256 = manifest.getFilesSha256();
        if (filesSha256 != null && !SHA256_PATTERN.matcher(filesSha256).matches()) {
            throw new InvalidPackageSignatureException("invalidFilesSha256");
        }
    }

    public static void validateExpected(
        PackageManifest manifest,
        PackageType expectedType,
        String expectedName,
        String expectedVersion
    ) {
        if (expectedType != null && manifest.getType() != expectedType) {
            throw new InvalidPackageSignatureException("typeMismatch");
        }
        if (expectedName != null && !expectedName.equals(manifest.getName())) {
            throw new InvalidPackageSignatureException("nameMismatch");
        }
        if (expectedVersion != null
            && !LATEST.equals(expectedVersion)
            && !expectedVersion.equals(manifest.getVersion())
        ) {
            throw new InvalidPackageSignatureException("versionMismatch");
        }
    }

    public static void validateNotOlderThan(
        PackageManifest manifest,
        String installedVersion
    ) {
        if (!isBlank(installedVersion)
            && compareVersions(manifest.getVersion(), installedVersion) < 0
        ) {
            throw new InvalidPackageSignatureException("versionRollback");
        }
    }

    public static void validateNewerThan(
        PackageManifest manifest,
        String currentVersion
    ) {
        if (!isBlank(currentVersion)
            && compareVersions(manifest.getVersion(), currentVersion) <= 0
        ) {
            throw new InvalidPackageSignatureException("versionNotNewer");
        }
    }

    public static void verifyFile(
        PackageManifest manifest,
        File file
    ) throws IOException {
        if (!file.isFile()) {
            throw new InvalidPackageSignatureException("fileNotFound");
        }
        if (file.length() != manifest.getSize()) {
            throw new InvalidPackageSignatureException("sizeMismatch");
        }
        if (!Hashes.sha256Hex(file).equals(manifest.getSha256())) {
            throw new InvalidPackageSignatureException("sha256Mismatch");
        }
    }

    public static Map<String, String> verifyFileHashes(
        PackageManifest manifest,
        String fileHashesText
    ) {
        String filesSha256 = manifest.getFilesSha256();
        if (filesSha256 == null) {
            throw new InvalidPackageSignatureException("filesSha256Missing");
        }
        if (fileHashesText == null
            || !PackageFiles.sha256Of(fileHashesText).equals(filesSha256)
        ) {
            throw new InvalidPackageSignatureException("filesSha256Mismatch");
        }
        return PackageFiles.parseText(fileHashesText);
    }

    public static Map<String, Object> toMap(PackageManifest manifest) {
        Map<String, Object> answer = new LinkedHashMap<>();
        answer.put(FIELD_TYPE, manifest.getType().getValue());
        answer.put(FIELD_NAME, manifest.getName());
        answer.put(FIELD_VERSION, manifest.getVersion());
        answer.put(FIELD_FILE_NAME, manifest.getFileName());
        answer.put(FIELD_SIZE, manifest.getSize());
        answer.put(FIELD_SHA256, manifest.getSha256());
        if (manifest.getFilesSha256() != null) {
            answer.put(FIELD_FILES_SHA256, manifest.getFilesSha256());
        }
        answer.put(FIELD_ISSUED_AT, manifest.getIssuedAt());
        return answer;
    }

    public static PackageManifest fromMap(Map<?, ?> map) {
        PackageManifest manifest = PackageManifest.builder()
            .type(PackageType.of(toStringOrNull(map.get(FIELD_TYPE))))
            .name(toStringOrNull(map.get(FIELD_NAME)))
            .version(toStringOrNull(map.get(FIELD_VERSION)))
            .fileName(toStringOrNull(map.get(FIELD_FILE_NAME)))
            .size(toLong(map.get(FIELD_SIZE), FIELD_SIZE))
            .sha256(toStringOrNull(map.get(FIELD_SHA256)))
            .filesSha256(toStringOrNull(map.get(FIELD_FILES_SHA256)))
            .issuedAt(toLong(map.get(FIELD_ISSUED_AT), FIELD_ISSUED_AT))
            .build();
        validate(manifest);
        return manifest;
    }

    private static String toStringOrNull(Object value) {
        return value instanceof String ? (String) value : null;
    }

    private static long toLong(Object value, String field) {
        if (value instanceof Integer || value instanceof Long) {
            return ((Number) value).longValue();
        }
        throw new InvalidPackageSignatureException(
            "invalid" + Character.toUpperCase(field.charAt(0)) + field.substring(1)
        );
    }
}
