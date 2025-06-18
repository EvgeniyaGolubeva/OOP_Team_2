package com.the_meow.blog_service;

import com.the_meow.blog_service.utils.CompressionUtil;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.Base64;

class CompressionUtilTest {

    @Test
    void testHappyPathCompressDecompress() throws IOException {
        String longText = "ABC".repeat(100); // >70 characters
        String compressed = CompressionUtil.compress(longText);
        String decompressed = CompressionUtil.decompress(compressed);

        assertNotEquals(longText, compressed);
        assertEquals(longText, decompressed);
    }

    @Test
    void testShortInputNotCompressed() throws IOException {
        String shortText = "Short text under 70 characters.";
        String compressed = CompressionUtil.compress(shortText);

        assertEquals(shortText, compressed); // should return as-is
    }

    @Test
    void testInvalidBase64ThrowsException() {
        String badBase64 = "this_is_not_base64!";
        assertThrows(IllegalArgumentException.class, () -> {
            CompressionUtil.decompress(badBase64);
        });
    }

    @Test
    void testBase64ButNotGzipThrowsIOException() {
        // This string is Base64 but decodes to plain text, not gzip
        String base64Text = Base64.getEncoder().encodeToString("not compressed".getBytes());
        assertThrows(IOException.class, () -> {
            CompressionUtil.decompress(base64Text);
        });
    }
}
