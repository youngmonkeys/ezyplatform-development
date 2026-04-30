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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ImageFileServiceTest {

    @Test
    public void reduceImageFileSizeShouldNotDependOnExtension()
        throws Exception {
        // given
        SettingService settingService = mock(SettingService.class);
        ImageFileService instance = new ImageFileService(settingService);
        File imageFile = File.createTempFile("image-service-", ".unknown");
        imageFile.deleteOnExit();
        String fileName = "image-file.unknown";
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
        Asserts.assertTrue(ImageIO.write(image, "jpg", imageFile));
        long originalFileSize = imageFile.length();
        Asserts.assertTrue(originalFileSize > 0);
        when(settingService.getMaxReducedImageFileSize())
            .thenReturn(originalFileSize / 2);
        when(settingService.isKeepOriginalSizeImageFile())
            .thenReturn(true);

        // when
        MediaFileSizeReductionResult result = instance.reduceImageFileSize(
            imageFile,
            fileName
        );

        // then
        Asserts.assertTrue(result.isReduced());
        Asserts.assertEquals(
            result.getOriginalSizeFileName(),
            "original_" + fileName
        );
        File originalSizeFile = new File(
            imageFile.getParentFile(),
            result.getOriginalSizeFileName()
        );
        originalSizeFile.deleteOnExit();
        Asserts.assertTrue(originalSizeFile.isFile());
        Asserts.assertEquals(originalSizeFile.length(), originalFileSize);
        Asserts.assertTrue(imageFile.length() < originalFileSize);
        ImageSize imageSize = ImageProxy.getImageSize(imageFile);
        Asserts.assertTrue(imageSize.getWidth() > 0);
        Asserts.assertTrue(imageSize.getHeight() > 0);
    }
}
