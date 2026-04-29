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

package org.youngmonkeys.ezyplatform.test.io;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.io.FolderProxy;
import org.youngmonkeys.ezyplatform.io.ZipFileProxy;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipFileProxyTest {

    @Test
    public void unzipFileNormalCaseTest() throws Exception {
        // given
        Path tempFolder = Files.createTempDirectory("zip-file-proxy-test");
        File rootFolder = tempFolder.resolve("root").toFile();
        File zipFile = tempFolder.resolve("test.zip").toFile();
        writeZipFile(zipFile, "folder/test.txt", "test");

        // when
        File actual = ZipFileProxy.unzipFile(rootFolder, zipFile);

        // then
        Asserts.assertEquals(
            actual.getCanonicalPath(),
            new File(rootFolder, "folder/test.txt").getCanonicalPath()
        );
        Asserts.assertTrue(new File(rootFolder, "folder/test.txt").exists());

        FolderProxy.deleteFolder(tempFolder.toFile());
    }

    @Test
    public void unzipFileWithTraversalEntryTest() throws Exception {
        // given
        Path tempFolder = Files.createTempDirectory("zip-file-proxy-test");
        File rootFolder = tempFolder.resolve("root").toFile();
        File zipFile = tempFolder.resolve("test.zip").toFile();
        writeZipFile(zipFile, "folder/../../evil.txt", "evil");

        // when
        Throwable e = Asserts.assertThrows(
            () -> ZipFileProxy.unzipFile(rootFolder, zipFile)
        );

        // then
        Asserts.assertEqualsType(e, SecurityException.class);
        Asserts.assertFalse(tempFolder.resolve("evil.txt").toFile().exists());

        FolderProxy.deleteFolder(tempFolder.toFile());
    }

    private void writeZipFile(
        File zipFile,
        String entryName,
        String content
    ) throws Exception {
        try (ZipOutputStream zipOutputStream = new ZipOutputStream(
            new FileOutputStream(zipFile)
        )) {
            zipOutputStream.putNextEntry(new ZipEntry(entryName));
            zipOutputStream.write(content.getBytes(StandardCharsets.UTF_8));
            zipOutputStream.closeEntry();
        }
    }
}
