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
import org.youngmonkeys.ezyplatform.signature.EcdsaSignatureFormats;
import org.youngmonkeys.ezyplatform.signature.PackageManifest;
import org.youngmonkeys.ezyplatform.signature.PackageManifests;
import org.youngmonkeys.ezyplatform.signature.PackageSignatureVerifier;
import org.youngmonkeys.ezyplatform.signature.PackageSigner;
import org.youngmonkeys.ezyplatform.signature.PackageSigningKeys;
import org.youngmonkeys.ezyplatform.signature.PackageType;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.Base64;
import java.util.Collections;
import java.util.List;

public class PackageSignatureTest {

    @Test
    public void signAndVerifyModuleTest() throws Exception {
        // given
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        PackageSigner signer = new PackageSigner(keyPair);
        File file = createFile("module content");
        PackageManifest manifest = PackageManifests.create(
            PackageType.MODULE,
            "ezyarticle",
            "1.2.3",
            file,
            1000L
        );
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            Collections.singletonList(keyPair.getPublic())
        );

        // when
        String jws = signer.sign(manifest);
        PackageManifest actual = verifier.verify(
            jws,
            PackageType.MODULE,
            "ezyarticle",
            "1.2.3"
        );
        PackageManifests.verifyFile(actual, file);

        // then
        Asserts.assertEquals(jws.split("\\.").length, 3);
        Asserts.assertEquals(actual.getType(), PackageType.MODULE);
        Asserts.assertEquals(actual.getName(), "ezyarticle");
        Asserts.assertEquals(actual.getVersion(), "1.2.3");
        Asserts.assertEquals(actual.getFileName(), file.getName());
        Asserts.assertEquals(actual.getSize(), file.length());
        Asserts.assertEquals(actual.getSha256(), manifest.getSha256());
        Asserts.assertEquals(actual.getIssuedAt(), 1000L);
        Asserts.assertEquals(
            signer.getKeyId(),
            PackageSigningKeys.keyIdOf(keyPair.getPublic())
        );
    }

    @Test
    public void verifyLatestVersionResolvesToConcreteVersionTest() throws Exception {
        // given
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        String jws = new PackageSigner(keyPair).sign(
            moduleManifest("ezyarticle", "1.2.3")
        );
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            Collections.singletonList(keyPair.getPublic())
        );

        // when
        PackageManifest actual = verifier.verify(
            jws,
            PackageType.MODULE,
            "ezyarticle",
            "latest"
        );

        // then
        Asserts.assertEquals(actual.getVersion(), "1.2.3");
    }

    @Test
    public void tamperedFileIsRejectedTest() throws Exception {
        // given
        File file = createFile("module content");
        PackageManifest manifest = PackageManifests.create(
            PackageType.MODULE,
            "ezyarticle",
            "1.2.3",
            file,
            1000L
        );
        Files.write(
            file.toPath(),
            "module contenT".getBytes(StandardCharsets.UTF_8)
        );

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageManifests.verifyFile(manifest, file)
        );

        // then
        assertReason(e, "sha256Mismatch");
    }

    @Test
    public void resizedFileIsRejectedTest() throws Exception {
        // given
        File file = createFile("module content");
        PackageManifest manifest = PackageManifests.create(
            PackageType.MODULE,
            "ezyarticle",
            "1.2.3",
            file,
            1000L
        );
        Files.write(
            file.toPath(),
            "module content!".getBytes(StandardCharsets.UTF_8)
        );

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageManifests.verifyFile(manifest, file)
        );

        // then
        assertReason(e, "sizeMismatch");
    }

    @Test
    public void tamperedPayloadIsRejectedTest() throws Exception {
        // given
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        String jws = new PackageSigner(keyPair).sign(
            moduleManifest("ezyarticle", "1.2.3")
        );
        String[] parts = jws.split("\\.");
        String forgedPayload = new String(
            Base64.getUrlDecoder().decode(parts[1]),
            StandardCharsets.UTF_8
        ).replace("1.2.3", "1.2.4");
        String forged = parts[0] + "." +
            Base64.getUrlEncoder().withoutPadding().encodeToString(
                forgedPayload.getBytes(StandardCharsets.UTF_8)
            ) +
            "." + parts[2];
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            Collections.singletonList(keyPair.getPublic())
        );

        // when
        Throwable e = Asserts.assertThrows(() -> verifier.verify(forged));

        // then
        assertReason(e, "invalidSignature");
    }

    @Test
    public void untrustedKeyIsRejectedTest() throws Exception {
        // given
        String jws = new PackageSigner(
            PackageSigningKeys.generateKeyPair()
        ).sign(moduleManifest("ezyarticle", "1.2.3"));
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            Collections.singletonList(
                PackageSigningKeys.generateKeyPair().getPublic()
            )
        );

        // when
        Throwable e = Asserts.assertThrows(() -> verifier.verify(jws));

        // then
        assertReason(e, "untrustedKey");
    }

    @Test
    public void algorithmNoneIsRejectedTest() throws Exception {
        // given
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        String jws = new PackageSigner(keyPair).sign(
            moduleManifest("ezyarticle", "1.2.3")
        );
        String keyId = PackageSigningKeys.keyIdOf(keyPair.getPublic());
        String header = "{\"alg\":\"none\",\"kid\":\"" + keyId +
            "\",\"typ\":\"ezy-package+jws\"}";
        String forged = Base64.getUrlEncoder().withoutPadding().encodeToString(
            header.getBytes(StandardCharsets.UTF_8)
        ) + "." + jws.split("\\.")[1] + ".";
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            Collections.singletonList(keyPair.getPublic())
        );

        // when
        Throwable e = Asserts.assertThrows(() -> verifier.verify(forged));

        // then
        assertReason(e, "unsupportedAlgorithm");
    }

    @Test
    public void invalidFormatIsRejectedTest() throws Exception {
        // given
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            Collections.singletonList(
                PackageSigningKeys.generateKeyPair().getPublic()
            )
        );

        // when
        Throwable e1 = Asserts.assertThrows(() -> verifier.verify("a.b"));
        Throwable e2 = Asserts.assertThrows(() -> verifier.verify("!.!.!"));
        Throwable e3 = Asserts.assertThrows(() -> verifier.verify(null));

        // then
        assertReason(e1, "invalidFormat");
        assertReason(e2, "invalidFormat");
        assertReason(e3, "invalidFormat");
    }

    @Test
    public void mismatchedExpectationsAreRejectedTest() throws Exception {
        // given
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        String jws = new PackageSigner(keyPair).sign(
            moduleManifest("ezyarticle", "1.2.3")
        );
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            Collections.singletonList(keyPair.getPublic())
        );

        // when
        Throwable typeError = Asserts.assertThrows(() ->
            verifier.verify(jws, PackageType.PLATFORM, "ezyarticle", "1.2.3")
        );
        Throwable nameError = Asserts.assertThrows(() ->
            verifier.verify(jws, PackageType.MODULE, "ezysupport", "1.2.3")
        );
        Throwable versionError = Asserts.assertThrows(() ->
            verifier.verify(jws, PackageType.MODULE, "ezyarticle", "1.2.2")
        );

        // then
        assertReason(typeError, "typeMismatch");
        assertReason(nameError, "nameMismatch");
        assertReason(versionError, "versionMismatch");
    }

    @Test
    public void platformManifestMustUseFixedNameTest() {
        // given
        PackageManifest manifest = PackageManifest.builder()
            .type(PackageType.PLATFORM)
            .name("ezyarticle")
            .version("1.5.6")
            .fileName("ezyplatform-1.5.6.zip")
            .size(1L)
            .sha256(repeat('a', 64))
            .issuedAt(1L)
            .build();

        // when
        Throwable e = Asserts.assertThrows(
            () -> PackageManifests.validate(manifest)
        );

        // then
        assertReason(e, "invalidName");
    }

    @Test
    public void latestVersionCanNotBeSignedTest() throws Exception {
        // given
        PackageSigner signer = new PackageSigner(
            PackageSigningKeys.generateKeyPair()
        );

        // when
        Throwable e = Asserts.assertThrows(
            () -> signer.sign(moduleManifest("ezyarticle", "latest"))
        );

        // then
        assertReason(e, "invalidVersion");
    }

    @Test
    public void pemRoundTripTest() throws Exception {
        // given
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        KeyPair otherKeyPair = PackageSigningKeys.generateKeyPair();
        String keyPairPem = PackageSigningKeys.toPem(keyPair);
        String publicKeysPem = PackageSigningKeys.toPem(keyPair.getPublic()) +
            PackageSigningKeys.toPem(otherKeyPair.getPublic());

        // when
        KeyPair actualKeyPair = PackageSigningKeys.parseKeyPair(keyPairPem);
        List<PublicKey> actualPublicKeys = PackageSigningKeys.parsePublicKeys(
            publicKeysPem
        );

        // then
        Asserts.assertEquals(
            actualKeyPair.getPublic().getEncoded(),
            keyPair.getPublic().getEncoded()
        );
        Asserts.assertEquals(
            actualKeyPair.getPrivate().getEncoded(),
            keyPair.getPrivate().getEncoded()
        );
        Asserts.assertEquals(actualPublicKeys.size(), 2);
        Asserts.assertEquals(
            PackageSigningKeys.keyIdOf(actualPublicKeys.get(1)),
            PackageSigningKeys.keyIdOf(otherKeyPair.getPublic())
        );
        Asserts.assertTrue(
            PackageSigningKeys.keyIdOf(keyPair.getPublic()).matches("ezy-[0-9a-f]{16}")
        );
    }

    @Test
    public void mismatchedKeyPairIsRejectedTest() throws Exception {
        // given
        KeyPair keyPair = new KeyPair(
            PackageSigningKeys.generateKeyPair().getPublic(),
            PackageSigningKeys.generateKeyPair().getPrivate()
        );

        // when
        Throwable e = Asserts.assertThrows(() -> new PackageSigner(keyPair));

        // then
        Asserts.assertEqualsType(e, IllegalArgumentException.class);
    }

    @Test
    public void ecdsaFormatRoundTripTest() {
        // given
        byte[] concatenated = new byte[64];
        concatenated[0] = (byte) 0x80;
        concatenated[31] = 0x01;
        concatenated[63] = 0x7f;

        // when
        byte[] der = EcdsaSignatureFormats.concatenatedToDer(concatenated);
        byte[] actual = EcdsaSignatureFormats.derToConcatenated(der, 32);

        // then
        Asserts.assertEquals(actual, concatenated);
        Asserts.assertEquals(der[0] & 0xFF, 0x30);
    }

    private static void assertReason(Throwable e, String reason) {
        Asserts.assertEqualsType(e, InvalidPackageSignatureException.class);
        Asserts.assertEquals(
            ((InvalidPackageSignatureException) e).getReason(),
            reason
        );
    }

    private static PackageManifest moduleManifest(
        String name,
        String version
    ) {
        return PackageManifest.builder()
            .type(PackageType.MODULE)
            .name(name)
            .version(version)
            .fileName(name + "-" + version + ".zip")
            .size(10L)
            .sha256(repeat('b', 64))
            .issuedAt(1L)
            .build();
    }

    private static File createFile(String content) throws Exception {
        File file = File.createTempFile("package-signature-test", ".zip");
        file.deleteOnExit();
        Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    private static String repeat(char ch, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            builder.append(ch);
        }
        return builder.toString();
    }
}
