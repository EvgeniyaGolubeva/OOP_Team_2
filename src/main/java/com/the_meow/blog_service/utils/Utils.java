package com.the_meow.blog_service.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class Utils {
    public static Integer getUserId(String token) {
        try {
            return Integer.parseInt(token);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public String compressText(String text) throws IOException {
        if (text.length() <= 100) {
            return text;
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        gzipOutputStream.write(text.getBytes());
        gzipOutputStream.close();

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public String decompressText(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(text);
        } catch (IllegalArgumentException e) {
            return null;
        }
    
        ByteArrayInputStream byteStream = new ByteArrayInputStream(decoded);
        GZIPInputStream gzip;
        try {
            gzip = new GZIPInputStream(byteStream);
        } catch (IOException e) {
            return null;
        }
    
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] buffer = new byte[10_000];
        int len;
    
        try {
            while ((len = gzip.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            return null;
        }
    
        return out.toString(StandardCharsets.UTF_8);
    }
}
