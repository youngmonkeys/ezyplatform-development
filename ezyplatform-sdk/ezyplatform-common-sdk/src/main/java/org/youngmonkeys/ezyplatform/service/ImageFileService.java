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

package org.youngmonkeys.ezyplatform.service;

import com.tvd12.ezyfox.util.EzyLoggable;
import lombok.AllArgsConstructor;
import org.youngmonkeys.ezyplatform.data.MediaFileSizeReductionResult;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_FILE;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO_LONG;

@AllArgsConstructor
public class ImageFileService extends EzyLoggable {

    private final SettingService settingService;

    private static final String TEMP_BEST_FILE_PREFIX = "image-best-";
    private static final String TEMP_CANDIDATE_FILE_PREFIX =
        "image-candidate-";
    private static final String TEMP_FILE_SUFFIX = ".tmp";
    private static final String ORIGINAL_SIZE_FILE_PREFIX = "original_";
    private static final String IMAGE_FORMAT_JPG = "jpg";
    private static final String IMAGE_FORMAT_JPEG = "jpeg";
    private static final String IMAGE_FORMAT_PNG = "png";
    private static final String JPG_FILE_EXTENSION = ".jpg";
    private static final String IMAGE_JPEG_MIME_TYPE = "image/jpeg";
    private static final float FULL_SCALE = 1.0F;
    private static final float MIN_SCALE = 0.05F;
    private static final float SCALE_REDUCTION_RATIO = 0.85F;
    private static final float INITIAL_QUALITY = 0.85F;
    private static final float QUALITY_REDUCTION_STEP = 0.1F;
    private static final float MIN_QUALITY = 0.25F;
    private static final float MIN_COMPRESSION_QUALITY = 0.0F;
    private static final int MIN_IMAGE_SIZE = 1;

    @SuppressWarnings("MethodLength")
    public MediaFileSizeReductionResult reduceImageFileSize(
        File imageFile,
        long expectedFileSize
    ) {
        if (imageFile == null
            || !imageFile.isFile()
        ) {
            return MediaFileSizeReductionResult.NO;
        }
        long maxFileSize = expectedFileSize > ZERO_LONG
            ? expectedFileSize
            : settingService.getMaxReducedImageFileSize();
        if (maxFileSize <= ZERO
            || imageFile.length() <= maxFileSize
        ) {
            return MediaFileSizeReductionResult.NO;
        }
        File bestFile = null;
        File candidateFile = null;
        File originalSizeFile = null;
        try {
            ImageData imageData = readImageData(imageFile);
            if (imageData == null) {
                return MediaFileSizeReductionResult.NO;
            }
            if (!isSupportedFormat(imageData.formatName)) {
                return MediaFileSizeReductionResult.NO;
            }
            File parentFile = imageFile.getAbsoluteFile().getParentFile();
            bestFile = createTempImageFile(
                TEMP_BEST_FILE_PREFIX,
                parentFile
            );
            candidateFile = createTempImageFile(
                TEMP_CANDIDATE_FILE_PREFIX,
                parentFile
            );
            boolean keepOriginalSizeImageFile = settingService
                .isKeepOriginalSizeImageFile();
            if (keepOriginalSizeImageFile) {
                originalSizeFile = saveOriginalSizeFile(imageFile);
            }
            ReducedFile reducedFile = reduceImageDataFileSize(
                imageData,
                maxFileSize,
                imageFile.length(),
                imageFile,
                bestFile,
                candidateFile
            );
            if (reducedFile.reduced) {
                MediaFileSizeReductionResult result = saveReducedImageFile(
                    imageFile,
                    bestFile,
                    originalSizeFile,
                    reducedFile
                );
                originalSizeFile = null;
                return result;
            }
        } catch (Throwable e) {
            logger.info(
                "can not reduce size of image: {}",
                imageFile,
                e
            );
        } finally {
            deleteQuietly(originalSizeFile);
            deleteQuietly(bestFile);
            deleteQuietly(candidateFile);
        }
        return MediaFileSizeReductionResult.NO;
    }

    private MediaFileSizeReductionResult saveReducedImageFile(
        File imageFile,
        File bestFile,
        File originalSizeFile,
        ReducedFile reducedFile
    ) throws IOException {
        File newImageFile = reducedFile.outputFile != null
            ? reducedFile.outputFile
            : imageFile;
        Files.move(
            bestFile.toPath(),
            newImageFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );
        if (!newImageFile.equals(imageFile)) {
            Files.deleteIfExists(imageFile.toPath());
        }
        return MediaFileSizeReductionResult.builder()
            .reduced(Boolean.TRUE)
            .originalSizeFileName(
                originalSizeFile != null
                    ? originalSizeFile.getName()
                    : NULL_STRING
            )
            .newFileName(toNewFileName(imageFile, newImageFile))
            .newFileMimeType(toNewFileMimeType(reducedFile))
            .newFileSize(newImageFile.length())
            .build();
    }

    private String toNewFileName(
        File imageFile,
        File newImageFile
    ) {
        return newImageFile.equals(imageFile)
            ? NULL_STRING
            : newImageFile.getName();
    }

    private String toNewFileMimeType(ReducedFile reducedFile) {
        return reducedFile.newFileMimeType != null
            ? reducedFile.newFileMimeType
            : NULL_STRING;
    }

    private ReducedFile reduceImageDataFileSize(
        ImageData imageData,
        long maxFileSize,
        long originalFileSize,
        File imageFile,
        File bestFile,
        File candidateFile
    ) throws IOException {
        if (isPngFormat(imageData.formatName)) {
            ReducedFile reducedFile = reduceLossyImageFileSize(
                new ImageData(imageData.image, IMAGE_FORMAT_JPG),
                maxFileSize,
                originalFileSize,
                bestFile,
                candidateFile
            );
            return reducedFile.reduced
                ? new ReducedFile(
                    Boolean.TRUE,
                    reducedFile.fileSize,
                    toJpgFile(imageFile),
                    IMAGE_JPEG_MIME_TYPE
                )
                : reducedFile;
        }
        return reduceLossyImageFileSize(
            imageData,
            maxFileSize,
            originalFileSize,
            bestFile,
            candidateFile
        );
    }

    private File toJpgFile(
        File imageFile
    ) {
        return new File(
            imageFile.getAbsoluteFile().getParentFile(),
            toJpgFileName(imageFile.getName())
        );
    }

    private String toJpgFileName(
        String fileName
    ) {
        int dotIndex = fileName.lastIndexOf('.');
        String fileNameWithoutExtension = dotIndex > ZERO
            ? fileName.substring(ZERO, dotIndex)
            : fileName;
        return fileNameWithoutExtension + JPG_FILE_EXTENSION;
    }

    private ReducedFile reduceLossyImageFileSize(
        ImageData imageData,
        long maxFileSize,
        long bestFileSize,
        File bestFile,
        File candidateFile
    ) throws IOException {
        boolean reduced = Boolean.FALSE;
        float scale = FULL_SCALE;
        while (scale > MIN_SCALE) {
            BufferedImage image = resizeImage(imageData.image, scale);
            ReducedFile reducedFile = reduceBufferedImageFileSize(
                image,
                imageData.formatName,
                maxFileSize,
                bestFileSize,
                bestFile,
                candidateFile
            );
            bestFileSize = reducedFile.fileSize;
            reduced = reduced || reducedFile.reduced;
            if (bestFileSize <= maxFileSize
                || (
                    image.getWidth() == MIN_IMAGE_SIZE
                        && image.getHeight() == MIN_IMAGE_SIZE
                )
            ) {
                break;
            }
            scale *= SCALE_REDUCTION_RATIO;
        }
        return new ReducedFile(reduced, bestFileSize);
    }

    private ReducedFile reduceBufferedImageFileSize(
        BufferedImage image,
        String formatName,
        long maxFileSize,
        long bestFileSize,
        File bestFile,
        File candidateFile
    ) throws IOException {
        boolean reduced = Boolean.FALSE;
        float quality = INITIAL_QUALITY;
        do {
            writeImage(image, formatName, quality, candidateFile);
            long candidateFileSize = candidateFile.length();
            if (candidateFileSize < bestFileSize) {
                Files.copy(
                    candidateFile.toPath(),
                    bestFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                );
                bestFileSize = candidateFileSize;
                reduced = Boolean.TRUE;
            }
            if (bestFileSize <= maxFileSize
                || !isLossyFormat(formatName)
            ) {
                break;
            }
            quality -= QUALITY_REDUCTION_STEP;
        } while (quality >= MIN_QUALITY);
        return new ReducedFile(reduced, bestFileSize);
    }

    private File createTempImageFile(
        String prefix,
        File parentFile
    ) throws IOException {
        return File.createTempFile(prefix, TEMP_FILE_SUFFIX, parentFile);
    }

    private File saveOriginalSizeFile(
        File imageFile
    ) throws IOException {
        File parentFile = imageFile.getAbsoluteFile().getParentFile();
        String originalSizeFileName =
            ORIGINAL_SIZE_FILE_PREFIX + imageFile.getName();
        File originalSizeFile = new File(parentFile, originalSizeFileName);
        Files.copy(
            imageFile.toPath(),
            originalSizeFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );
        return originalSizeFile;
    }

    private ImageData readImageData(
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
                    BufferedImage image = reader.read(ZERO);
                    return new ImageData(
                        image,
                        reader.getFormatName()
                    );
                } finally {
                    reader.dispose();
                }
            }
        }
        return null;
    }

    @SuppressWarnings("MethodLength")
    private BufferedImage resizeImage(
        BufferedImage image,
        float scale
    ) {
        if (scale >= FULL_SCALE) {
            return image;
        }
        int width = Math.max(
            MIN_IMAGE_SIZE,
            Math.round(image.getWidth() * scale)
        );
        int height = Math.max(
            MIN_IMAGE_SIZE,
            Math.round(image.getHeight() * scale)
        );
        int type = image.getTransparency() == Transparency.OPAQUE
            ? BufferedImage.TYPE_INT_RGB
            : BufferedImage.TYPE_INT_ARGB;
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D graphics = resizedImage.createGraphics();
        try {
            graphics.setRenderingHint(
                RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BILINEAR
            );
            graphics.setRenderingHint(
                RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY
            );
            graphics.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON
            );
            graphics.drawImage(
                image,
                ZERO,
                ZERO,
                width,
                height,
                (
                    drewImage,
                    infoFlags,
                    x,
                    y,
                    drewWidth,
                    drewHeight
                ) -> {
                    if ((infoFlags & ImageObserver.ERROR) != ZERO
                        || (infoFlags & ImageObserver.ABORT) != ZERO
                    ) {
                        logger.warn(
                            "resize image draw update error, flags: {}",
                            infoFlags
                        );
                    }
                    return (infoFlags
                        & (
                            ImageObserver.ALLBITS
                                | ImageObserver.ERROR
                                | ImageObserver.ABORT
                            )
                        ) == ZERO;
                }
            );
        } finally {
            graphics.dispose();
        }
        return resizedImage;
    }

    private void writeImage(
        BufferedImage image,
        String formatName,
        float quality,
        File outputFile
    ) throws IOException {
        Iterator<ImageWriter> writers =
            ImageIO.getImageWritersByFormatName(formatName);
        if (!writers.hasNext()) {
            throw new IOException("no image writer for format: " + formatName);
        }
        ImageWriter writer = writers.next();
        try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
             ImageOutputStream outputStream = ImageIO.createImageOutputStream(fileOutputStream)
        ) {
            writer.setOutput(outputStream);
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            if (writeParam.canWriteCompressed()) {
                try {
                    writeParam.setCompressionMode(
                        ImageWriteParam.MODE_EXPLICIT
                    );
                    writeParam.setCompressionQuality(
                        isLossyFormat(formatName)
                            ? quality
                            : MIN_COMPRESSION_QUALITY
                    );
                } catch (RuntimeException e) {
                    writeParam = writer.getDefaultWriteParam();
                }
            }
            writer.write(
                null,
                new javax.imageio.IIOImage(
                    toWritableImage(image, formatName),
                    null,
                    null
                ),
                writeParam
            );
        } finally {
            writer.dispose();
        }
    }

    private BufferedImage toWritableImage(
        BufferedImage image,
        String formatName
    ) {
        if (!isLossyFormat(formatName)
            || image.getType() == BufferedImage.TYPE_INT_RGB
        ) {
            return image;
        }
        BufferedImage convertedImage = new BufferedImage(
            image.getWidth(),
            image.getHeight(),
            BufferedImage.TYPE_INT_RGB
        );
        Graphics2D graphics = convertedImage.createGraphics();
        try {
            graphics.setColor(Color.WHITE);
            graphics.fillRect(
                ZERO,
                ZERO,
                convertedImage.getWidth(),
                convertedImage.getHeight()
            );
            graphics.drawImage(image, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return convertedImage;
    }

    private boolean isPngFormat(
        String formatName
    ) {
        return IMAGE_FORMAT_PNG.equalsIgnoreCase(formatName);
    }

    private boolean isSupportedFormat(
        String formatName
    ) {
        return isLossyFormat(formatName)
            || isPngFormat(formatName);
    }

    private boolean isLossyFormat(
        String formatName
    ) {
        return IMAGE_FORMAT_JPG.equalsIgnoreCase(formatName)
            || IMAGE_FORMAT_JPEG.equalsIgnoreCase(formatName);
    }

    private void deleteQuietly(
        File file
    ) {
        if (file != null && file.isFile() && !file.delete()) {
            logger.warn(
                "can not delete temporary image file: {}",
                file
            );
        }
    }

    private static class ImageData {
        private final BufferedImage image;
        private final String formatName;

        private ImageData(
            BufferedImage image,
            String formatName
        ) {
            this.image = image;
            this.formatName = formatName;
        }
    }

    private static class ReducedFile {
        private final boolean reduced;
        private final long fileSize;
        private final File outputFile;
        private final String newFileMimeType;

        private ReducedFile(
            boolean reduced,
            long fileSize
        ) {
            this(reduced, fileSize, NULL_FILE, NULL_STRING);
        }

        private ReducedFile(
            boolean reduced,
            long fileSize,
            File outputFile,
            String newFileMimeType
        ) {
            this.reduced = reduced;
            this.fileSize = fileSize;
            this.outputFile = outputFile;
            this.newFileMimeType = newFileMimeType;
        }
    }
}
