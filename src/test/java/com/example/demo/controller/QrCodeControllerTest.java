package com.example.demo.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
class QrCodeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnPngQrCodeImage() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/qrcode/image")
                        .param("content", "https://example.com")
                        .param("size", "160"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("image/png"))
                .andReturn();

        byte[] imageBytes = result.getResponse().getContentAsByteArray();
        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));

        assertNotNull(image);
        assertEquals(160, image.getWidth());
        assertEquals(160, image.getHeight());
    }

    @Test
    void shouldReturnBadRequestWhenContentIsMissing() throws Exception {
        mockMvc.perform(get("/api/qrcode/image"))
                .andExpect(status().isBadRequest());
    }
}
