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

package org.youngmonkeys.ezyplatform.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;

public final class ZipFileProxy {

    private ZipFileProxy() {}

    public static File unzipFile(
        File rootFolder,
        File zipFile
    ) throws IOException {
        return unzipFile(rootFolder, zipFile, Boolean.FALSE);
    }

    public static File unzipFile(
        File rootFolder,
        File zipFile,
        boolean excludeParentFolder
    ) throws IOException {
        ZipInputStream zipInputStream = new ZipInputStream(
            Files.newInputStream(zipFile.toPath())
        );
        try {
            return unzipStream(
                rootFolder,
                zipInputStream,
                excludeParentFolder
            );
        } finally {
            zipInputStream.closeEntry();
            zipInputStream.close();
        }
    }

    public static File unzipStream(
        File rootFolder,
        ZipInputStream zipInputStream,
        boolean excludeParentFolder
    ) throws IOException {
        File answer = null;
        String rootFolderCanonicalPath = rootFolder.getCanonicalPath();
        ZipEntry zipEntry;
        byte[] buffer = new byte[1024];
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = zipEntry.getName().replace('\\', '/');
            if (excludeParentFolder) {
                int index = entryName.indexOf('/', 1);
                if (index > ZERO) {
                    entryName = entryName.substring(index + 1);
                }
            }
            if (entryName.isEmpty()) {
                continue;
            }
            File newFile = Paths.get(rootFolder.toString(), entryName).toFile();
            validateEntryInRootFolder(
                rootFolderCanonicalPath,
                newFile,
                entryName
            );
            if (answer == null) {
                answer = newFile;
            }
            if (zipEntry.isDirectory()) {
                FolderProxy.mkdirs(newFile);
                continue;
            }
            FolderProxy.createNewFile(newFile);
            try (FileOutputStream fileOutputStream = new FileOutputStream(newFile)) {
                int readBytes;
                while ((readBytes = zipInputStream.read(buffer)) > ZERO) {
                    fileOutputStream.write(buffer, ZERO, readBytes);
                }
            }
        }
        return answer;
    }

    private static void validateEntryInRootFolder(
        String rootFolderCanonicalPath,
        File file,
        String entryName
    ) throws IOException {
        String canonicalFilePath = file.getCanonicalPath();
        if (!canonicalFilePath.startsWith(
            rootFolderCanonicalPath + File.separator
        )) {
            throw new SecurityException(
                "Entry is outside of the target dir: " + entryName
            );
        }
    }
}
