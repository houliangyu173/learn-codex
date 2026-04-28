package com.example.demo.controller;

import com.example.demo.util.QrCodeUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestController
@RequestMapping("/api/qrcode")
public class QrCodeController {

    @GetMapping(value = "/image", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImage(@RequestParam("content") String content,
                                                @RequestParam(value = "size", defaultValue = "300") int size) {
        if (content == null || content.trim().isEmpty()) {
            throw new ResponseStatusException(BAD_REQUEST, "content must not be blank");
        }
        if (size <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "size must be greater than 0");
        }

        byte[] imageBytes = QrCodeUtils.generatePngBytes(content, size, size);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
