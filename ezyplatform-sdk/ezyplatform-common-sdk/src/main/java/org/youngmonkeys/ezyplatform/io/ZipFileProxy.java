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

public final class ZipFileProxy {

    private ZipFileProxy() {}

    public static File unzipFile(
        File rootFolder,
        File zipFile
    ) throws IOException {
        return unzipFile(rootFolder, zipFile, false);
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
        ZipEntry zipEntry;
        byte[] buffer = new byte[1024];
        while ((zipEntry = zipInputStream.getNextEntry()) != null) {
            String entryName = zipEntry.getName().replace('\\', '/');
            if (entryName.contains("..")) {
                continue;
            }
            if (excludeParentFolder) {
                int index = entryName.indexOf('/', 1);
                if (index > 0) {
                    entryName = entryName.substring(index + 1);
                }
            }
            File newFile = Paths.get(rootFolder.toString(), entryName).toFile();
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
                while ((readBytes = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, readBytes);
                }
            }
        }
        return answer;
    }
}
