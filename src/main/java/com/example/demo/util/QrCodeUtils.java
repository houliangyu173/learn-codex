package com.example.demo.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.EnumMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * 二维码生成工具类。
 */
public final class QrCodeUtils {

    private static final int DEFAULT_SIZE = 300;

    private QrCodeUtils() {
        // utility class
    }

    public static byte[] generatePngBytes(String content) {
        return generatePngBytes(content, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public static byte[] generatePngBytes(String content, int width, int height) {
        validate(content, width, height);

        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, buildHints());
            BufferedImage image = toBufferedImage(bitMatrix);
            return writePng(image);
        } catch (WriterException e) {
            throw new IllegalStateException("Failed to generate QR code.", e);
        }
    }

    public static String generateBase64Png(String content) {
        return generateBase64Png(content, DEFAULT_SIZE, DEFAULT_SIZE);
    }

    public static String generateBase64Png(String content, int width, int height) {
        return Base64.getEncoder().encodeToString(generatePngBytes(content, width, height));
    }

    private static void validate(String content, int width, int height) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("QR code content must not be blank.");
        }
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("QR code width and height must be greater than 0.");
        }
    }

    private static Map<EncodeHintType, Object> buildHints() {
        Map<EncodeHintType, Object> hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
        hints.put(EncodeHintType.MARGIN, 1);
        return hints;
    }

    private static BufferedImage toBufferedImage(BitMatrix bitMatrix) {
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, bitMatrix.get(x, y) ? Color.BLACK.getRGB() : Color.WHITE.getRGB());
            }
        }
        return image;
    }

    private static byte[] writePng(BufferedImage image) {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            boolean written = ImageIO.write(image, "png", outputStream);
            if (!written) {
                throw new IllegalStateException("No PNG writer found.");
            }
            return outputStream.toByteArray();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to write QR code image.", e);
        }
    }
}
