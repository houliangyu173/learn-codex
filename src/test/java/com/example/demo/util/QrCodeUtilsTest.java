package com.example.demo.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Base64;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;

class QrCodeUtilsTest {

    @Test
    void shouldGenerateValidPngBytes() throws Exception {
        Class<?> qrCodeUtilsClass = Class.forName("com.example.demo.util.QrCodeUtils");
        Method generatePngBytes = qrCodeUtilsClass.getMethod("generatePngBytes", String.class, int.class, int.class);

        byte[] pngBytes = (byte[]) generatePngBytes.invoke(null, "https://example.com", 240, 240);

        assertTrue(pngBytes.length > 0);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(pngBytes));
        assertNotNull(image);
        assertEquals(240, image.getWidth());
        assertEquals(240, image.getHeight());
    }

    @Test
    void shouldGenerateDecodableBase64Png() throws Exception {
        Class<?> qrCodeUtilsClass = Class.forName("com.example.demo.util.QrCodeUtils");
        Method generateBase64Png = qrCodeUtilsClass.getMethod("generateBase64Png", String.class, int.class, int.class);

        String base64 = (String) generateBase64Png.invoke(null, "hello world", 180, 180);

        assertNotNull(base64);
        assertTrue(base64.length() > 0);
        byte[] pngBytes = Base64.getDecoder().decode(base64);
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(pngBytes));
        assertNotNull(image);
        assertEquals(180, image.getWidth());
        assertEquals(180, image.getHeight());
    }

    @Test
    void shouldRejectBlankContent() throws Exception {
        Class<?> qrCodeUtilsClass = Class.forName("com.example.demo.util.QrCodeUtils");
        Method generatePngBytes = qrCodeUtilsClass.getMethod("generatePngBytes", String.class, int.class, int.class);

        InvocationTargetException exception = assertThrows(InvocationTargetException.class,
                () -> generatePngBytes.invoke(null, " ", 200, 200));

        assertTrue(exception.getCause() instanceof IllegalArgumentException);
    }
}
