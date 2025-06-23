package com.the_meow.blog_service;

import com.the_meow.blog_service.utils.CompressionUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Base64;

class CompressionUtilTest {

    @Test
    void testHappyPathCompressDecompress() throws IOException {
        String longText = "ABC".repeat(100); // >100 characters
        String compressed = CompressionUtils.compressText(longText);
        String decompressed = CompressionUtils.decompressText(compressed);

        assertNotEquals(longText, compressed);
        assertEquals(longText, decompressed);
    }

    @Test
    void testShortInputNotCompressed() throws IOException {
        String shortText = "Short text under 100 characters.";
        String compressed = CompressionUtils.compressText(shortText);

        assertEquals(shortText, compressed); // should return as-is
    }

    @Test
    void testInvalidBase64ThrowsException() {
        String badBase64 = "this_is_not_base64!";
        assertThrows(IllegalArgumentException.class, () -> {
            CompressionUtils.decompressText(badBase64);
        });
    }

    @Test
    void testBase64ButNotGzipThrowsIOException() {
        // This string is Base64 but decodes to plain text, not gzip
        String base64Text = Base64.getEncoder().encodeToString("not compressed".getBytes());
        assertThrows(IOException.class, () -> {
            CompressionUtils.decompressText(base64Text);
        });
    }
}
