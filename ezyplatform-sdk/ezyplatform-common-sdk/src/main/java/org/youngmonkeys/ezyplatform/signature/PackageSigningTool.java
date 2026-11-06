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

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.KeyPair;
import java.security.PublicKey;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public final class PackageSigningTool {

    private static final String USAGE =
        "Usage:\n" +
        "  generate-key <key-pem-file>\n" +
        "  sign <key-pem-file> <platform|sdk|module> <version> <bundle-file> [module-name]\n" +
        "  verify <public-keys-pem-file> <jws-file> <bundle-file>\n" +
        "  sign-keys <root-key-pem-file> <version> <valid-days> " +
        "<market-public-keys-pem-file> <platform-public-keys-pem-file> <output-file>\n" +
        "  verify-keys <root-public-keys-base64> <key-list-file>";

    private PackageSigningTool() {}

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            exit(USAGE);
        }
        switch (args[0]) {
            case "generate-key":
                generateKey(args);
                break;
            case "sign":
                sign(args);
                break;
            case "verify":
                verify(args);
                break;
            case "sign-keys":
                signKeys(args);
                break;
            case "verify-keys":
                verifyKeys(args);
                break;
            default:
                exit(USAGE);
        }
    }

    private static void generateKey(String[] args) throws Exception {
        if (args.length != 2) {
            exit(USAGE);
        }
        File keyFile = new File(args[1]);
        if (keyFile.exists()) {
            exit("key file already exists: " + keyFile);
        }
        KeyPair keyPair = PackageSigningKeys.generateKeyPair();
        Files.write(
            keyFile.toPath(),
            PackageSigningKeys.toPem(keyPair).getBytes(StandardCharsets.US_ASCII),
            StandardOpenOption.CREATE_NEW
        );
        System.out.println("key id: " + PackageSigningKeys.keyIdOf(keyPair.getPublic()));
        System.out.println("public key (base64):");
        System.out.println(PackageSigningKeys.toBase64(keyPair.getPublic()));
        System.out.println("public key (pem):");
        System.out.print(PackageSigningKeys.toPem(keyPair.getPublic()));
    }

    private static void signKeys(String[] args) throws Exception {
        if (args.length != 7) {
            exit(USAGE);
        }
        KeyPair rootKeyPair = PackageSigningKeys.readKeyPair(new File(args[1]));
        long version = Long.parseLong(args[2]);
        long validDays = Long.parseLong(args[3]);
        Map<PackageSigningKeyRole, List<PublicKey>> keysByRole =
            new EnumMap<>(PackageSigningKeyRole.class);
        keysByRole.put(
            PackageSigningKeyRole.MARKET,
            PackageSigningKeys.readPublicKeys(new File(args[4]))
        );
        keysByRole.put(
            PackageSigningKeyRole.PLATFORM,
            PackageSigningKeys.readPublicKeys(new File(args[5]))
        );
        long now = System.currentTimeMillis();
        String jws = PackageSigningKeyLists.sign(
            rootKeyPair,
            version,
            now,
            now + TimeUnit.DAYS.toMillis(validDays),
            keysByRole
        );
        Files.write(
            new File(args[6]).toPath(),
            jws.getBytes(StandardCharsets.US_ASCII)
        );
        System.out.println("root key id: " + PackageSigningKeys.keyIdOf(rootKeyPair.getPublic()));
        System.out.println("version: " + version);
        System.out.println("key list file: " + args[6]);
    }

    private static void verifyKeys(String[] args) throws Exception {
        if (args.length != 3) {
            exit(USAGE);
        }
        PackageSigningKeyList keyList = PackageSigningKeyLists.verify(
            new String(
                Files.readAllBytes(new File(args[2]).toPath()),
                StandardCharsets.US_ASCII
            ),
            PackageSigningKeys.parseBase64PublicKeys(args[1]),
            System.currentTimeMillis()
        );
        System.out.println("version: " + keyList.getVersion());
        System.out.println("expires at: " + keyList.getExpiresAt());
        for (PackageSigningKeyRole role : PackageSigningKeyRole.values()) {
            for (PublicKey key : keyList.getKeys(role)) {
                System.out.println(role.getValue() + ": " + PackageSigningKeys.keyIdOf(key));
            }
        }
    }

    private static void sign(String[] args) throws Exception {
        if (args.length < 5 || args.length > 6) {
            exit(USAGE);
        }
        PackageType type = PackageType.of(args[2]);
        if (type == null) {
            exit("invalid package type: " + args[2]);
        }
        String name = type.getFixedPackageName();
        if (name == null) {
            if (args.length != 6) {
                exit("module name is required");
            }
            name = args[5];
        }
        File bundleFile = new File(args[4]);
        PackageSigner signer = new PackageSigner(
            PackageSigningKeys.readKeyPair(new File(args[1]))
        );
        PackageManifest manifest = PackageManifests.create(
            type,
            name,
            args[3],
            bundleFile,
            System.currentTimeMillis()
        );
        File jwsFile = new File(bundleFile.getPath() + ".jws");
        Files.write(
            jwsFile.toPath(),
            signer.sign(manifest).getBytes(StandardCharsets.US_ASCII)
        );
        System.out.println("key id: " + signer.getKeyId());
        System.out.println("sha256: " + manifest.getSha256());
        System.out.println("signature file: " + jwsFile);
    }

    private static void verify(String[] args) throws Exception {
        if (args.length != 4) {
            exit(USAGE);
        }
        PackageSignatureVerifier verifier = new PackageSignatureVerifier(
            PackageSigningKeys.readPublicKeys(new File(args[1]))
        );
        String jws = new String(
            Files.readAllBytes(new File(args[2]).toPath()),
            StandardCharsets.US_ASCII
        ).trim();
        PackageManifest manifest = verifier.verify(jws);
        PackageManifests.verifyFile(manifest, new File(args[3]));
        System.out.println("valid: " + manifest);
    }

    private static void exit(String message) {
        System.err.println(message);
        System.exit(1);
    }
}
