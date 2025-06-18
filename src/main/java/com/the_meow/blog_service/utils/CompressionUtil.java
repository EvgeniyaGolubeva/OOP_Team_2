package com.the_meow.blog_service.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public abstract class CompressionUtil {

    public static String compress(String blog) throws IOException {
        if (blog.length() <= 70) return blog;

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);

        gzipOutputStream.write(blog.getBytes());
        gzipOutputStream.close();

        return Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }

    public static String decompress(String blog) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(Base64.getDecoder().decode(blog));
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