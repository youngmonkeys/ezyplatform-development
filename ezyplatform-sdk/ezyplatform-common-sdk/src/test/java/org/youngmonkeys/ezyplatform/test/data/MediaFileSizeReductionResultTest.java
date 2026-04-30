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

package org.youngmonkeys.ezyplatform.test.data;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.MediaFileSizeReductionResult;

public class MediaFileSizeReductionResultTest {

    @Test
    public void getNewFileMimeTypeOrDefaultReturnNewFileMimeTypeTest() {
        // given
        String newFileMimeType = "image/webp";
        String defaultValue = "image/png";
        MediaFileSizeReductionResult instance = MediaFileSizeReductionResult
            .builder()
            .newFileMimeType(newFileMimeType)
            .build();

        // when
        String actual = instance.getNewFileMimeTypeOrDefault(defaultValue);

        // then
        Asserts.assertEquals(actual, newFileMimeType);
    }

    @Test
    public void getNewFileMimeTypeOrDefaultReturnDefaultValueTest() {
        // given
        String defaultValue = "image/png";

        // when
        // then
        Asserts.assertEquals(
            MediaFileSizeReductionResult
                .builder()
                .build()
                .getNewFileMimeTypeOrDefault(defaultValue),
            defaultValue
        );
        Asserts.assertEquals(
            MediaFileSizeReductionResult
                .builder()
                .newFileMimeType("")
                .build()
                .getNewFileMimeTypeOrDefault(defaultValue),
            defaultValue
        );
        Asserts.assertEquals(
            MediaFileSizeReductionResult
                .builder()
                .newFileMimeType(" ")
                .build()
                .getNewFileMimeTypeOrDefault(defaultValue),
            defaultValue
        );
    }

    @Test
    public void getNewFileSizeOrDefaultReturnNewFileSizeTest() {
        // given
        long newFileSize = 1024L;
        long defaultValue = 2048L;
        MediaFileSizeReductionResult instance = MediaFileSizeReductionResult
            .builder()
            .newFileSize(newFileSize)
            .build();

        // when
        long actual = instance.getNewFileSizeOrDefault(defaultValue);

        // then
        Asserts.assertEquals(actual, newFileSize);
    }

    @Test
    public void getNewFileSizeOrDefaultReturnDefaultValueTest() {
        // given
        long defaultValue = 2048L;

        // when
        // then
        Asserts.assertEquals(
            MediaFileSizeReductionResult
                .builder()
                .build()
                .getNewFileSizeOrDefault(defaultValue),
            defaultValue
        );
        Asserts.assertEquals(
            MediaFileSizeReductionResult
                .builder()
                .newFileSize(0L)
                .build()
                .getNewFileSizeOrDefault(defaultValue),
            defaultValue
        );
        Asserts.assertEquals(
            MediaFileSizeReductionResult
                .builder()
                .newFileSize(-1L)
                .build()
                .getNewFileSizeOrDefault(defaultValue),
            defaultValue
        );
    }

    @Test
    public void noResultTest() {
        // given
        String defaultMimeType = "image/png";
        long defaultFileSize = 2048L;

        // when
        MediaFileSizeReductionResult instance = MediaFileSizeReductionResult.NO;

        // then
        Asserts.assertFalse(instance.isReduced());
        Asserts.assertNull(instance.getOriginalSizeFileName());
        Asserts.assertNull(instance.getNewFileMimeType());
        Asserts.assertEquals(instance.getNewFileSize(), 0L);
        Asserts.assertEquals(
            instance.getNewFileMimeTypeOrDefault(defaultMimeType),
            defaultMimeType
        );
        Asserts.assertEquals(
            instance.getNewFileSizeOrDefault(defaultFileSize),
            defaultFileSize
        );
    }

    @Test
    public void builderTest() {
        // given
        String originalSizeFileName = "avatar.png";
        String newFileMimeType = "image/webp";
        long newFileSize = 1024L;

        // when
        MediaFileSizeReductionResult instance = MediaFileSizeReductionResult
            .builder()
            .reduced(true)
            .originalSizeFileName(originalSizeFileName)
            .newFileMimeType(newFileMimeType)
            .newFileSize(newFileSize)
            .build();

        // then
        Asserts.assertTrue(instance.isReduced());
        Asserts.assertEquals(
            instance.getOriginalSizeFileName(),
            originalSizeFileName
        );
        Asserts.assertEquals(instance.getNewFileMimeType(), newFileMimeType);
        Asserts.assertEquals(instance.getNewFileSize(), newFileSize);
    }
}
