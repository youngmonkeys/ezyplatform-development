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

package org.youngmonkeys.ezyplatform.test.service;

import com.tvd12.test.assertion.Asserts;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import org.youngmonkeys.ezyplatform.data.ImageSize;
import org.youngmonkeys.ezyplatform.data.MediaFileSizeReductionResult;
import org.youngmonkeys.ezyplatform.io.ImageProxy;
import org.youngmonkeys.ezyplatform.service.ImageFileService;
import org.youngmonkeys.ezyplatform.service.SettingService;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageFileServiceTest {

    @DataProvider
    public Object[][] imageExtensionsWithoutDefaultImageIoSupport() {
        return new Object[][] {
            {".webp", webpFileBytes()},
            {".avif", avifFileBytes()},
            {".svg", svgFileBytes()}
        };
    }

    @Test
    public void reduceImageFileSizeShouldReducePngWithoutRenaming()
        throws Exception {
        // given
        SettingService settingService = mock(SettingService.class);
        ImageFileService instance = new ImageFileService(settingService);
        File imageFile = File.createTempFile("image-service-", ".png");
        imageFile.deleteOnExit();
        BufferedImage image = newTestImage();
        Asserts.assertTrue(ImageIO.write(image, "png", imageFile));
        long originalFileSize = imageFile.length();
        Asserts.assertTrue(originalFileSize > 0);
        when(settingService.getMaxReducedImageFileSize())
            .thenReturn(1L);
        when(settingService.isKeepOriginalSizeImageFile())
            .thenReturn(true);

        // when
        MediaFileSizeReductionResult result = instance.reduceImageFileSize(
            imageFile
        );

        // then
        Asserts.assertNull(result.getNewFileMimeType());
        Asserts.assertTrue(imageFile.exists());
        Asserts.assertTrue(imageFile.length() <= originalFileSize);
        ImageSize imageSize = ImageProxy.getImageSize(imageFile);
        Asserts.assertEquals(imageSize.getWidth(), image.getWidth());
        Asserts.assertEquals(imageSize.getHeight(), image.getHeight());
        if (result.isReduced()) {
            Asserts.assertTrue(imageFile.length() < originalFileSize);
        }
    }

    @Test
    public void reduceImageFileSizeShouldSupportJpg()
        throws Exception {
        reduceImageFileSizeShouldSupportImageFormat("jpg", ".jpg");
    }

    @Test
    public void reduceImageFileSizeShouldSupportJpeg()
        throws Exception {
        reduceImageFileSizeShouldSupportImageFormat("jpeg", ".jpeg");
    }

    private void reduceImageFileSizeShouldSupportImageFormat(
        String formatName,
        String fileSuffix
    )
        throws Exception {
        // given
        SettingService settingService = mock(SettingService.class);
        ImageFileService instance = new ImageFileService(settingService);
        File imageFile = File.createTempFile("image-service-", fileSuffix);
        imageFile.deleteOnExit();
        BufferedImage image = newTestImage();
        Asserts.assertTrue(ImageIO.write(image, formatName, imageFile));
        long originalFileSize = imageFile.length();
        Asserts.assertTrue(originalFileSize > 0);
        when(settingService.getMaxReducedImageFileSize())
            .thenReturn(originalFileSize / 2);
        when(settingService.isKeepOriginalSizeImageFile())
            .thenReturn(true);

        // when
        MediaFileSizeReductionResult result = instance.reduceImageFileSize(
            imageFile
        );

        // then
        Asserts.assertTrue(result.isReduced());
        Asserts.assertEquals(
            result.getOriginalSizeFileName(),
            "original_" + imageFile.getName()
        );
        Asserts.assertTrue(imageFile.length() < originalFileSize);
        ImageSize imageSize = ImageProxy.getImageSize(imageFile);
        Asserts.assertTrue(imageSize.getWidth() > 0);
        Asserts.assertTrue(imageSize.getHeight() > 0);
    }

    @Test
    public void reduceImageFileSizeShouldNotDependOnExtension()
        throws Exception {
        // given
        SettingService settingService = mock(SettingService.class);
        ImageFileService instance = new ImageFileService(settingService);
        File imageFile = File.createTempFile("image-service-", ".unknown");
        imageFile.deleteOnExit();
        BufferedImage image = newTestImage();
        Asserts.assertTrue(ImageIO.write(image, "jpg", imageFile));
        long originalFileSize = imageFile.length();
        Asserts.assertTrue(originalFileSize > 0);
        when(settingService.getMaxReducedImageFileSize())
            .thenReturn(originalFileSize / 2);
        when(settingService.isKeepOriginalSizeImageFile())
            .thenReturn(true);

        // when
        MediaFileSizeReductionResult result = instance.reduceImageFileSize(
            imageFile
        );

        // then
        Asserts.assertTrue(result.isReduced());
        Asserts.assertEquals(
            result.getOriginalSizeFileName(),
            "original_" + imageFile.getName()
        );
        Asserts.assertTrue(imageFile.length() < originalFileSize);
        ImageSize imageSize = ImageProxy.getImageSize(imageFile);
        Asserts.assertTrue(imageSize.getWidth() > 0);
        Asserts.assertTrue(imageSize.getHeight() > 0);
    }

    @Test
    public void reduceImageFileSizeShouldReturnNoForGif()
        throws Exception {
        // given
        SettingService settingService = mock(SettingService.class);
        ImageFileService instance = new ImageFileService(settingService);
        File imageFile = File.createTempFile("image-service-", ".gif");
        imageFile.deleteOnExit();
        Files.write(imageFile.toPath(), gifFileBytes());
        long originalFileSize = imageFile.length();
        when(settingService.getMaxReducedImageFileSize())
            .thenReturn(1L);

        // when
        MediaFileSizeReductionResult result = instance.reduceImageFileSize(
            imageFile
        );

        // then
        Asserts.assertFalse(result.isReduced());
        Asserts.assertEquals(imageFile.length(), originalFileSize);
        Asserts.assertFalse(
            new File(
                imageFile.getParentFile(),
                "original_" + imageFile.getName()
            ).exists()
        );
    }

    @Test(dataProvider = "imageExtensionsWithoutDefaultImageIoSupport")
    public void reduceImageFileSizeShouldReturnNoForUnsupportedImageIoFormats(
        String fileSuffix,
        byte[] fileBytes
    )
        throws Exception {
        // given
        SettingService settingService = mock(SettingService.class);
        ImageFileService instance = new ImageFileService(settingService);
        File imageFile = File.createTempFile("image-service-", fileSuffix);
        imageFile.deleteOnExit();
        Files.write(imageFile.toPath(), fileBytes);
        long originalFileSize = imageFile.length();
        when(settingService.getMaxReducedImageFileSize())
            .thenReturn(1L);

        // when
        MediaFileSizeReductionResult result = instance.reduceImageFileSize(
            imageFile
        );

        // then
        Asserts.assertFalse(result.isReduced());
        Asserts.assertEquals(imageFile.length(), originalFileSize);
    }

    private BufferedImage newTestImage() {
        BufferedImage image = new BufferedImage(
            400,
            400,
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = image.createGraphics();
        try {
            for (int y = 0; y < image.getHeight(); ++y) {
                for (int x = 0; x < image.getWidth(); ++x) {
                    graphics.setColor(
                        new Color(
                            (x * 17 + y) % 256,
                            (x + y * 31) % 256,
                            (x * y) % 256
                        )
                    );
                    graphics.fillRect(x, y, 1, 1);
                }
            }
        } finally {
            graphics.dispose();
        }
        return image;
    }

    private static byte[] webpFileBytes() {
        return "unsupported webp image".getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] avifFileBytes() {
        return "unsupported avif image".getBytes(StandardCharsets.UTF_8);
    }

    private static byte[] gifFileBytes() {
        return new byte[] {
            71, 73, 70, 56, 57, 97, 1, 0, 1, 0, -128, 0, 0, -1, -1, -1,
            0, 0, 0, 33, -7, 4, 1, 0, 0, 0, 0, 44, 0, 0, 0, 0, 1, 0,
            1, 0, 0, 2, 2, 68, 1, 0, 59
        };
    }

    private static byte[] svgFileBytes() {
        return (
            "<svg xmlns=\"http://www.w3.org/2000/svg\" " +
                "width=\"400\" height=\"400\">" +
                "<rect width=\"400\" height=\"400\" fill=\"#d33\"/>" +
                "</svg>"
        ).getBytes(StandardCharsets.UTF_8);
    }
}
