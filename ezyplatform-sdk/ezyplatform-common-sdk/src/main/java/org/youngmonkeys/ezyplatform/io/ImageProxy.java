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

import org.youngmonkeys.ezyplatform.data.ImageSize;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public final class ImageProxy {

    private ImageProxy() {}

    public static ImageSize getImageSize(
        Path imageFilePath
    ) throws IOException {
        return getImageSize(imageFilePath.toFile());
    }

    public static ImageSize getImageSize(
        String imageFilePath
    ) throws IOException {
        return getImageSize(new File(imageFilePath));
    }

    public static ImageSize getImageSize(
        File imageFile
    ) throws IOException {
        try (ImageInputStream inputStream =
                ImageIO.createImageInputStream(imageFile)
        ) {
            Iterator<ImageReader> readers =
                ImageIO.getImageReaders(inputStream);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(inputStream);
                    return new ImageSize(
                        reader.getWidth(0),
                        reader.getHeight(0),
                        imageFile.length()
                    );
                } finally {
                    reader.dispose();
                }
            }
        }
        throw new IOException("there is no reader for image file: " + imageFile);
    }
}
