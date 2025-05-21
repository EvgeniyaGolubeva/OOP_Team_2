package com.the_meow.blog_service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@Data
@AllArgsConstructor
public class CompressionUtil {
    private String blog;

    public String compress() throws IOException {
        if (this.blog.length() <= 70) return this.blog;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        gzipOutputStream.write(this.blog.getBytes());
        gzipOutputStream.close();

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public String decompress() throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(Base64.getDecoder().decode(this.blog));
        GZIPInputStream gzip = new GZIPInputStream(byteStream);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        byte[] buffer = new byte[10_000];
        int len;

        while ((len = gzip.read(buffer)) != -1) {
            out.write(buffer, 0, len);
        }

        return out.toString();
    }
}