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
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public final class PackageFiles {

    public static final String FILE_HASH_SEPARATOR = "  ";
    public static final String LINE_SEPARATOR = "\n";

    private static final Pattern FILE_HASH_LINE_PATTERN = Pattern.compile(
        "^([0-9a-f]{64})  (.+)$"
    );
    private static final Pattern WINDOWS_DRIVE_PATTERN = Pattern.compile(
        "^[A-Za-z]:.*"
    );

    private PackageFiles() {}

    public static Map<String, String> readZipFileHashes(
        File zipFile
    ) throws IOException {
        Map<String, String> answer = new TreeMap<>();
        try (ZipFile zip = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> entries = zip.entries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                }
                String path = entry.getName();
                validatePath(path);
                String sha256;
                try (InputStream input = zip.getInputStream(entry)) {
                    sha256 = Hashes.sha256Hex(input);
                }
                if (answer.put(path, sha256) != null) {
                    throw new InvalidPackageSignatureException(
                        "duplicatedFilePath"
                    );
                }
            }
        }
        return answer;
    }

    public static String toText(Map<String, String> fileHashes) {
        StringBuilder builder = new StringBuilder();
        for (Map.Entry<String, String> e : new TreeMap<>(fileHashes).entrySet()) {
            builder
                .append(e.getValue())
                .append(FILE_HASH_SEPARATOR)
                .append(e.getKey())
                .append(LINE_SEPARATOR);
        }
        return builder.toString();
    }

    public static Map<String, String> parseText(String text) {
        Map<String, String> answer = new TreeMap<>();
        if (text == null) {
            throw new InvalidPackageSignatureException("invalidFileHashes");
        }
        for (String line : text.split(LINE_SEPARATOR)) {
            if (line.isEmpty()) {
                continue;
            }
            Matcher matcher = FILE_HASH_LINE_PATTERN.matcher(line);
            if (!matcher.matches()) {
                throw new InvalidPackageSignatureException("invalidFileHashes");
            }
            String path = matcher.group(2);
            validatePath(path);
            if (answer.put(path, matcher.group(1)) != null) {
                throw new InvalidPackageSignatureException(
                    "duplicatedFilePath"
                );
            }
        }
        return answer;
    }

    public static String sha256Of(String text) {
        return Hashes.sha256Hex(text.getBytes(StandardCharsets.UTF_8));
    }

    public static String readZipFileHashesText(
        File zipFile
    ) throws IOException {
        return toText(readZipFileHashes(zipFile));
    }

    public static Map<String, String> stripPathPrefix(
        Map<String, String> fileHashes,
        String prefix
    ) {
        Map<String, String> answer = new TreeMap<>();
        for (Map.Entry<String, String> e : fileHashes.entrySet()) {
            String path = e.getKey();
            if (path.startsWith(prefix) && path.length() > prefix.length()) {
                answer.put(path.substring(prefix.length()), e.getValue());
            }
        }
        return answer;
    }

    public static PackageFilesScanResult scan(
        File baseDirectory,
        Map<String, String> fileHashes,
        Collection<String> ownedDirectories
    ) throws IOException {
        List<String> modifiedFiles = new ArrayList<>();
        List<String> missingFiles = new ArrayList<>();
        for (Map.Entry<String, String> e : new TreeMap<>(fileHashes).entrySet()) {
            String path = e.getKey();
            validatePath(path);
            File file = new File(baseDirectory, path);
            if (!file.isFile()) {
                missingFiles.add(path);
            } else if (!Hashes.sha256Hex(file).equals(e.getValue())) {
                modifiedFiles.add(path);
            }
        }
        List<String> unknownFiles = new ArrayList<>();
        Path basePath = baseDirectory.toPath().toAbsolutePath().normalize();
        for (String ownedDirectory : ownedDirectories) {
            validatePath(ownedDirectory);
            Path directoryPath = basePath.resolve(ownedDirectory).normalize();
            if (!Files.isDirectory(directoryPath, LinkOption.NOFOLLOW_LINKS)) {
                continue;
            }
            try (Stream<Path> paths = Files.walk(directoryPath)) {
                paths
                    .filter(p -> !Files.isDirectory(p, LinkOption.NOFOLLOW_LINKS))
                    .map(p -> basePath
                        .relativize(p)
                        .toString()
                        .replace(File.separatorChar, '/')
                    )
                    .filter(p -> !fileHashes.containsKey(p))
                    .forEach(unknownFiles::add);
            }
        }
        Collections.sort(unknownFiles);
        return new PackageFilesScanResult(
            modifiedFiles,
            missingFiles,
            unknownFiles
        );
    }

    private static void validatePath(String path) {
        if (path == null
            || path.isEmpty()
            || path.startsWith("/")
            || path.startsWith("\\")
            || path.indexOf('\n') >= 0
            || path.indexOf('\r') >= 0
            || path.indexOf('\0') >= 0
            || WINDOWS_DRIVE_PATTERN.matcher(path).matches()
        ) {
            throw new InvalidPackageSignatureException("invalidFilePath");
        }
        for (String segment : path.split("[/\\\\]")) {
            if (segment.equals("..")) {
                throw new InvalidPackageSignatureException("invalidFilePath");
            }
        }
    }
}
