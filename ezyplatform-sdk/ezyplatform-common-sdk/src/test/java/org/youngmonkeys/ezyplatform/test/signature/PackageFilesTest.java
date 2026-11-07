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

package org.youngmonkeys.ezyplatform.test.signature;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.exception.InvalidPackageSignatureException;
import org.youngmonkeys.ezyplatform.signature.PackageFiles;
import org.youngmonkeys.ezyplatform.signature.PackageFilesScanResult;
import org.youngmonkeys.ezyplatform.signature.PackageManifest;
import org.youngmonkeys.ezyplatform.signature.PackageManifests;
import org.youngmonkeys.ezyplatform.signature.PackageSignatureVerifier;
import org.youngmonkeys.ezyplatform.signature.PackageSigner;
import org.youngmonkeys.ezyplatform.signature.PackageSigningKeys;
import org.youngmonkeys.ezyplatform.signature.PackageType;
import org.youngmonkeys.ezyplatform.util.Hashes;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class PackageFilesTest {

    @Test
    public void readZipFileHashesTest() throws Exception {
        // given
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("ezyplatform/lib/b.jar", "b");
        entries.put("ezyplatform/lib/a.jar", "a");
        File zipFile = createZipFile(entries, 1_600_000_000_000L, true);

        // when
        Map<String, String> actual = PackageFiles.readZipFileHashes(zipFile);

        // then
        Asserts.assertEquals(
            actual.keySet().toArray(),
            new Object[] {"ezyplatform/lib/a.jar", "ezyplatform/lib/b.jar"}
        );
        Asserts.assertEquals(actual.get("ezyplatform/lib/a.jar"), sha256("a"));
        Asserts.assertEquals(actual.get("ezyplatform/lib/b.jar"), sha256("b"));
        Asserts.assertEquals(
            PackageFiles.toText(actual),
            sha256("a") + "  ezyplatform/lib/a.jar\n"
                + sha256("b") + "  ezyplatform/lib/b.jar\n"
        );
    }

    @Test
    public void filesSha256DoesNotDependOnZipTimestampsTest() throws Exception {
        // given
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("ezyplatform/lib/a.jar", "a");
        entries.put("ezyplatform/settings/platform.properties", "x=1");
        File firstZip = createZipFile(entries, 1_600_000_000_000L, false);
        File secondZip = createZipFile(entries, 1_700_000_000_000L, false);

        // when
        PackageManifest first = createPlatformManifest(firstZip);
        PackageManifest second = createPlatformManifest(secondZip);

        // then
        Asserts.assertNotEquals(first.getSha256(), second.getSha256());
        Asserts.assertEquals(first.getFilesSha256(), second.getFilesSha256());
    }

    @Test
    public void signedFileHashesAreAcceptedTest() throws Exception {
        // given
        File zipFile = createPlatformZipFile();
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        String jws = new PackageSigner(keyPair).sign(
            createPlatformManifest(zipFile)
        );
        PackageManifest manifest = new PackageSignatureVerifier(
            Collections.singletonList(keyPair.getPublic())
        ).verify(jws, PackageType.PLATFORM, "ezyplatform", "1.0.0");
        String marketText = PackageFiles.readZipFileHashesText(zipFile);

        // when
        Map<String, String> actual = PackageManifests.verifyFileHashes(
            manifest,
            marketText
        );

        // then
        Asserts.assertEquals(actual, PackageFiles.readZipFileHashes(zipFile));
    }

    @Test
    public void fileHashesReplacedByMarketAreRejectedTest() throws Exception {
        // given
        File zipFile = createPlatformZipFile();
        PackageManifest manifest = createPlatformManifest(zipFile);
        Map<String, String> fileHashes = PackageFiles.readZipFileHashes(zipFile);
        fileHashes.put("ezyplatform/lib/ezyplatform-core.jar", sha256("evil"));
        String tamperedText = PackageFiles.toText(fileHashes);

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageManifests.verifyFileHashes(manifest, tamperedText)
        );

        // then
        assertReason(e, "filesSha256Mismatch");
    }

    @Test
    public void fileHashesAddedByMarketAreRejectedTest() throws Exception {
        // given
        File zipFile = createPlatformZipFile();
        PackageManifest manifest = createPlatformManifest(zipFile);
        String tamperedText = PackageFiles.readZipFileHashesText(zipFile)
            + sha256("evil") + "  ezyplatform/lib/evil.jar\n";

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageManifests.verifyFileHashes(manifest, tamperedText)
        );

        // then
        assertReason(e, "filesSha256Mismatch");
    }

    @Test
    public void manifestWithoutFilesSha256CanNotVerifyFileHashesTest() throws Exception {
        // given
        File zipFile = createPlatformZipFile();
        PackageManifest manifest = PackageManifests.create(
            PackageType.PLATFORM,
            "ezyplatform",
            "1.0.0",
            zipFile,
            null,
            1000L
        );
        String text = PackageFiles.readZipFileHashesText(zipFile);

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageManifests.verifyFileHashes(manifest, text)
        );

        // then
        assertReason(e, "filesSha256Missing");
    }

    @Test
    public void oldSignatureWithoutFilesSha256IsStillValidTest() throws Exception {
        // given
        File zipFile = createPlatformZipFile();
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        String jws = new PackageSigner(keyPair).sign(
            PackageManifests.create(
                PackageType.PLATFORM,
                "ezyplatform",
                "1.0.0",
                zipFile,
                null,
                1000L
            )
        );

        // when
        PackageManifest actual = new PackageSignatureVerifier(
            Collections.singletonList(keyPair.getPublic())
        ).verify(jws, PackageType.PLATFORM, "ezyplatform", "1.0.0");
        PackageManifests.verifyFile(actual, zipFile);

        // then
        Asserts.assertNull(actual.getFilesSha256());
        Asserts.assertFalse(
            PackageManifests.toMap(actual).containsKey("filesSha256")
        );
    }

    @Test
    public void zipSlipEntryCanNotBeSignedTest() throws Exception {
        // given
        File zipFile = createZipFile(
            Collections.singletonMap("../ezyplatform/lib/evil.jar", "evil"),
            1_600_000_000_000L,
            false
        );

        // when
        Throwable e = Asserts.assertThrows(
            () -> createPlatformManifest(zipFile)
        );

        // then
        assertReason(e, "invalidFilePath");
    }

    @Test
    public void sdkPackedEntriesWithLeadingSlashAreHashedAsRelativePathsTest() throws Exception {
        // given
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("/ezyarticle/admin/plugins/ezyarticle/module.properties", "a");
        entries.put("/ezyarticle/web/plugins/ezyarticle/lib/ezyarticle.jar", "b");
        File zipFile = createZipFile(entries, 1_600_000_000_000L, false);

        // when
        Map<String, String> actual = PackageFiles.readZipFileHashes(zipFile);

        // then
        Map<String, String> expected = new LinkedHashMap<>();
        expected.put("ezyarticle/admin/plugins/ezyarticle/module.properties", sha256("a"));
        expected.put("ezyarticle/web/plugins/ezyarticle/lib/ezyarticle.jar", sha256("b"));
        Asserts.assertEquals(actual, expected, false);
        Asserts.assertEquals(
            PackageFiles.parseText(PackageFiles.toText(actual)),
            expected,
            false
        );
    }

    @Test
    public void leadingSlashDoesNotHideZipSlipEntryTest() throws Exception {
        // given
        File zipFile = createZipFile(
            Collections.singletonMap("/../ezyplatform/lib/evil.jar", "evil"),
            1_600_000_000_000L,
            false
        );

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageFiles.readZipFileHashes(zipFile)
        );

        // then
        assertReason(e, "invalidFilePath");
    }

    @Test
    public void sameFileWithAndWithoutLeadingSlashIsDuplicatedTest() throws Exception {
        // given
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("ezyplatform/lib/a.jar", "a");
        entries.put("/ezyplatform/lib/a.jar", "evil");
        File zipFile = createZipFile(entries, 1_600_000_000_000L, false);

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageFiles.readZipFileHashes(zipFile)
        );

        // then
        assertReason(e, "duplicatedFilePath");
    }

    @Test
    public void parseTextRejectsUnsafePathsTest() {
        // given
        String[] texts = new String[] {
            sha256("a") + "  ../settings/platform.properties\n",
            sha256("a") + "  /etc/passwd\n",
            sha256("a") + "  lib\\..\\..\\evil.jar\n",
            sha256("a") + "  C:/evil.jar\n"
        };

        // when
        // then
        for (String text : texts) {
            Throwable e = Asserts.assertThrows(() -> PackageFiles.parseText(text));
            assertReason(e, "invalidFilePath");
        }
    }

    @Test
    public void parseTextRejectsInvalidLinesTest() {
        // given
        String text = "not-a-hash  lib/a.jar\n";

        // when
        Throwable e = Asserts.assertThrows(() -> PackageFiles.parseText(text));

        // then
        assertReason(e, "invalidFileHashes");
    }

    @Test
    public void stripPathPrefixTest() {
        // given
        Map<String, String> fileHashes = new LinkedHashMap<>();
        fileHashes.put("ezyplatform/lib/a.jar", "1");
        fileHashes.put("ezyplatform/", "2");
        fileHashes.put("other/b.jar", "3");

        // when
        Map<String, String> actual = PackageFiles.stripPathPrefix(
            fileHashes,
            "ezyplatform/"
        );

        // then
        Asserts.assertEquals(
            actual,
            Collections.singletonMap("lib/a.jar", "1"),
            false
        );
    }

    @Test
    public void scanCleanInstallationTest() throws Exception {
        // given
        File home = createInstallation();
        Map<String, String> fileHashes = installationFileHashes();

        // when
        PackageFilesScanResult actual = PackageFiles.scan(
            home,
            fileHashes,
            Arrays.asList("lib", "admin/lib")
        );

        // then
        Asserts.assertTrue(actual.isClean());
    }

    @Test
    public void scanDetectsTamperedInstallationTest() throws Exception {
        // given
        File home = createInstallation();
        Map<String, String> fileHashes = installationFileHashes();
        write(new File(home, "lib/ezyplatform-core.jar"), "patched");
        Files.delete(new File(home, "admin/lib/ezyplatform-admin.jar").toPath());
        write(new File(home, "lib/evil.jar"), "evil");
        write(new File(home, "admin/lib/sub/evil.jar"), "evil");
        write(new File(home, "logs/admin.log"), "log");

        // when
        PackageFilesScanResult actual = PackageFiles.scan(
            home,
            fileHashes,
            Arrays.asList("lib", "admin/lib")
        );

        // then
        Asserts.assertFalse(actual.isClean());
        Asserts.assertEquals(
            actual.getModifiedFiles(),
            Collections.singletonList("lib/ezyplatform-core.jar"),
            false
        );
        Asserts.assertEquals(
            actual.getMissingFiles(),
            Collections.singletonList("admin/lib/ezyplatform-admin.jar"),
            false
        );
        Asserts.assertEquals(
            actual.getUnknownFiles(),
            Arrays.asList("admin/lib/sub/evil.jar", "lib/evil.jar"),
            false
        );
    }

    @Test
    public void scanIgnoresFilesOutsideOwnedDirectoriesTest() throws Exception {
        // given
        File home = createInstallation();
        Map<String, String> fileHashes = installationFileHashes();
        write(new File(home, "settings/extra.properties"), "x=1");

        // when
        PackageFilesScanResult actual = PackageFiles.scan(
            home,
            fileHashes,
            Collections.singletonList("lib")
        );

        // then
        Asserts.assertTrue(actual.isClean());
    }

    private static Map<String, String> installationFileHashes() {
        Map<String, String> fileHashes = new LinkedHashMap<>();
        fileHashes.put("lib/ezyplatform-core.jar", sha256("core"));
        fileHashes.put("admin/lib/ezyplatform-admin.jar", sha256("admin"));
        fileHashes.put("settings/platform.properties", sha256("x=1"));
        return fileHashes;
    }

    private static File createInstallation() throws Exception {
        File home = Files.createTempDirectory("package-files-test").toFile();
        home.deleteOnExit();
        write(new File(home, "lib/ezyplatform-core.jar"), "core");
        write(new File(home, "admin/lib/ezyplatform-admin.jar"), "admin");
        write(new File(home, "settings/platform.properties"), "x=1");
        return home;
    }

    private static File createPlatformZipFile() throws Exception {
        Map<String, String> entries = new LinkedHashMap<>();
        entries.put("ezyplatform/lib/ezyplatform-core.jar", "core");
        entries.put("ezyplatform/admin/lib/ezyplatform-admin.jar", "admin");
        return createZipFile(entries, 1_600_000_000_000L, true);
    }

    private static PackageManifest createPlatformManifest(
        File zipFile
    ) throws Exception {
        return PackageManifests.create(
            PackageType.PLATFORM,
            "ezyplatform",
            "1.0.0",
            zipFile,
            1000L
        );
    }

    private static File createZipFile(
        Map<String, String> entries,
        long time,
        boolean withDirectoryEntry
    ) throws Exception {
        File file = File.createTempFile("package-files-test", ".zip");
        file.deleteOnExit();
        try (ZipOutputStream output = new ZipOutputStream(
            Files.newOutputStream(file.toPath())
        )) {
            if (withDirectoryEntry) {
                ZipEntry directory = new ZipEntry("ezyplatform/");
                directory.setTime(time);
                output.putNextEntry(directory);
                output.closeEntry();
            }
            for (Map.Entry<String, String> e : entries.entrySet()) {
                ZipEntry entry = new ZipEntry(e.getKey());
                entry.setTime(time);
                output.putNextEntry(entry);
                output.write(e.getValue().getBytes(StandardCharsets.UTF_8));
                output.closeEntry();
            }
        }
        return file;
    }

    private static void write(File file, String content) throws Exception {
        Files.createDirectories(file.getParentFile().toPath());
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
    }

    private static String sha256(String content) {
        return Hashes.sha256Hex(content.getBytes(StandardCharsets.UTF_8));
    }

    private static void assertReason(Throwable e, String reason) {
        Asserts.assertEqualsType(e, InvalidPackageSignatureException.class);
        Asserts.assertEquals(
            ((InvalidPackageSignatureException) e).getReason(),
            reason
        );
    }
}
