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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;

import static org.youngmonkeys.ezyplatform.constant.CommonConstants.NULL_STRING;
import static org.youngmonkeys.ezyplatform.constant.CommonConstants.ZERO;

@AllArgsConstructor
public class ImageFileService extends EzyLoggable {

    private final SettingService settingService;

    public MediaFileSizeReductionResult reduceImageFileSize(
        File imageFile
    ) {
        if (imageFile == null
            || !imageFile.isFile()
        ) {
            return MediaFileSizeReductionResult.NO;
        }
        long maxFileSize = settingService.getMaxReducedImageFileSize();
        if (maxFileSize <= ZERO
            || imageFile.length() <= maxFileSize
        ) {
            return MediaFileSizeReductionResult.NO;
        }
        return reduceImageFileSize(imageFile, maxFileSize);
    }

    private MediaFileSizeReductionResult reduceImageFileSize(
        File imageFile,
        long maxFileSize
    ) {
        File bestFile = null;
        File candidateFile = null;
        File originalSizeFile = null;
        try {
            ImageData imageData = readImageData(imageFile);
            if (imageData == null) {
                return MediaFileSizeReductionResult.NO;
            }
            File parentFile = imageFile.getAbsoluteFile().getParentFile();
            bestFile = File.createTempFile(
                "image-best-",
                ".tmp",
                parentFile
            );
            candidateFile = File.createTempFile(
                "image-candidate-",
                ".tmp",
                parentFile
            );
            boolean keepOriginalSizeImageFile = settingService
                .isKeepOriginalSizeImageFile();
            if (keepOriginalSizeImageFile) {
                originalSizeFile = saveOriginalSizeFile(imageFile);
            }
            ReducedFile reducedFile = reduceImageFileSize(
                imageData,
                maxFileSize,
                imageFile.length(),
                bestFile,
                candidateFile
            );
            if (reducedFile.reduced) {
                Files.move(
                    bestFile.toPath(),
                    imageFile.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
                );
                return MediaFileSizeReductionResult.builder()
                    .reduced(true)
                    .originalSizeFileName(
                        originalSizeFile != null
                            ? originalSizeFile.getName()
                            : NULL_STRING
                    )
                    .newFileSize(imageFile.length())
                    .build();
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

    private ReducedFile reduceImageFileSize(
        ImageData imageData,
        long maxFileSize,
        long originalFileSize,
        File bestFile,
        File candidateFile
    ) throws IOException {
        long bestFileSize = originalFileSize;
        boolean reduced = false;
        float scale = 1.0F;
        while (scale > 0.05F) {
            BufferedImage image = resizeImage(imageData.image, scale);
            ReducedFile reducedFile = reduceImageFileSize(
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
                || (image.getWidth() == 1 && image.getHeight() == 1)
            ) {
                break;
            }
            scale *= 0.85F;
        }
        return new ReducedFile(bestFileSize, reduced);
    }

    private ReducedFile reduceImageFileSize(
        BufferedImage image,
        String formatName,
        long maxFileSize,
        long bestFileSize,
        File bestFile,
        File candidateFile
    ) throws IOException {
        boolean reduced = false;
        float quality = 0.85F;
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
                reduced = true;
            }
            if (bestFileSize <= maxFileSize
                || !isLossyFormat(formatName)
            ) {
                break;
            }
            quality -= 0.1F;
        } while (quality >= 0.25F);
        return new ReducedFile(bestFileSize, reduced);
    }

    private File saveOriginalSizeFile(
        File imageFile
    ) throws IOException {
        File parentFile = imageFile.getAbsoluteFile().getParentFile();
        String originalSizeFileName = "original_" + imageFile.getName();
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

    private static BufferedImage resizeImage(
        BufferedImage image,
        float scale
    ) {
        if (scale >= 1.0F) {
            return image;
        }
        int width = Math.max(1, Math.round(image.getWidth() * scale));
        int height = Math.max(1, Math.round(image.getHeight() * scale));
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
            graphics.drawImage(image, 0, 0, width, height, null);
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
                        isLossyFormat(formatName) ? quality : 0.0F
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
            graphics.drawImage(image, 0, 0, null);
        } finally {
            graphics.dispose();
        }
        return convertedImage;
    }

    private boolean isLossyFormat(
        String formatName
    ) {
        return "jpg".equalsIgnoreCase(formatName)
            || "jpeg".equalsIgnoreCase(formatName);
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
        private final long fileSize;
        private final boolean reduced;

        private ReducedFile(
            long fileSize,
            boolean reduced
        ) {
            this.fileSize = fileSize;
            this.reduced = reduced;
        }
    }
}
