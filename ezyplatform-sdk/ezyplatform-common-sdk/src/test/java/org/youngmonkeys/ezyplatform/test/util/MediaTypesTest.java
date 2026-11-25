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

package org.youngmonkeys.ezyplatform.test.util;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.FileMetadata;
import org.youngmonkeys.ezyplatform.entity.MediaType;
import org.youngmonkeys.ezyplatform.util.MediaTypes;

public class MediaTypesTest {

    @Test
    public void builtInMediaTypeUsesEnumFolderTest() {
        Asserts.assertEquals(MediaTypes.getMediaFolder("IMAGE"), "images");
        Asserts.assertEquals(MediaTypes.getMediaFolder("image"), "images");
        Asserts.assertEquals(MediaTypes.getMediaFolder("AVATAR"), "avatars");
        Asserts.assertEquals(MediaTypes.getMediaFolder("DOCUMENT"), "files");
        Asserts.assertEquals(MediaTypes.getMediaFolder("VIDEO"), "videos");
        Asserts.assertFalse(MediaTypes.isCustomMediaType("IMAGE"));
        Asserts.assertFalse(MediaTypes.isCustomMediaType("FILE"));
    }

    @Test
    public void customMediaTypeUsesDerivedFolderTest() {
        Asserts.assertTrue(MediaTypes.isCustomMediaType("EBOOK"));
        Asserts.assertTrue(MediaTypes.isCustomMediaType("MODEL_3D"));
        Asserts.assertEquals(MediaTypes.getMediaFolder("EBOOK"), "ebooks");
        Asserts.assertEquals(MediaTypes.getMediaFolder("MODEL_3D"), "model_3ds");
    }

    @Test
    public void invalidMediaTypeFallsBackToFilesFolderTest() {
        String tooLong = "A" + new String(new char[49]).replace('\0', 'B');
        String[] invalidTypes = new String[] {
            null,
            "",
            "ebook",
            "Ebook",
            "3D_MODEL",
            "_EBOOK",
            "EBOOK/../IMAGE",
            "../EBOOK",
            "EBOOK-X",
            "EBOOK ",
            tooLong
        };
        for (String type : invalidTypes) {
            Asserts.assertFalse(MediaTypes.isCustomMediaType(type));
            Asserts.assertEquals(MediaTypes.getMediaFolder(type), "files");
        }
        String maxLength = "A" + new String(new char[48]).replace('\0', 'B');
        Asserts.assertTrue(MediaTypes.isCustomMediaType(maxLength));
    }

    @Test
    public void ofMimeTypeNameMapsTopLevelMimeTypeTest() {
        Asserts.assertEquals(MediaType.ofMimeTypeName("application"), MediaType.DOCUMENT);
        Asserts.assertEquals(MediaType.ofMimeTypeName("image"), MediaType.IMAGE);
        Asserts.assertEquals(MediaType.ofMimeTypeName("video"), MediaType.VIDEO);
        Asserts.assertEquals(MediaType.ofMimeTypeName("audio"), MediaType.AUDIO);
        Asserts.assertEquals(MediaType.ofMimeTypeName("font"), MediaType.FONT);
        Asserts.assertEquals(MediaType.ofMimeTypeName("text"), MediaType.FILE);
        Asserts.assertEquals(MediaType.ofMimeTypeName("document"), MediaType.FILE);
        Asserts.assertEquals(MediaType.ofMimeTypeName(null), MediaType.FILE);
    }

    @Test
    public void fileMetadataUsesCustomMediaTypeOnlyWhenValidTest() {
        FileMetadata custom = FileMetadata.builder()
            .mediaType(MediaType.DOCUMENT)
            .customMediaType("EBOOK")
            .build();
        Asserts.assertEquals(custom.getMediaType(), MediaType.DOCUMENT);
        Asserts.assertEquals(custom.getMediaTypeText(), "EBOOK");
        Asserts.assertEquals(custom.getMediaFolder(), "ebooks");

        FileMetadata invalidCustom = FileMetadata.builder()
            .mediaType(MediaType.IMAGE)
            .customMediaType("../x")
            .build();
        Asserts.assertEquals(invalidCustom.getMediaTypeText(), "IMAGE");
        Asserts.assertEquals(invalidCustom.getMediaFolder(), "images");

        FileMetadata noType = FileMetadata.builder().build();
        Asserts.assertEquals(noType.getMediaTypeText(), "FILE");
        Asserts.assertEquals(noType.getMediaFolder(), "files");
    }
}
