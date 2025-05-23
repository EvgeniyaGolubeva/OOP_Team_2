package com.the_meow.blog_service.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Collection;
import java.util.Optional;

import com.the_meow.blog_service.model.BlogRating;
import com.the_meow.blog_service.model.CommentRating;

public class Utils {
    public static Optional<Integer> getUserId(String token) {
        if (token == null) {
            return Optional.of(null);
        }

        if (token.isBlank()) {
            return Optional.of(null);
        }

        try {
            return Optional.of(Integer.parseInt(token));
        }
        catch (NumberFormatException e) {
            return Optional.of(null);
        }
    }

    public static Double getAvgBlogRating(Collection<BlogRating> ratings) {
        return ratings.stream()
            .map(BlogRating::getRating)
            .map(Double::valueOf)
            .mapToDouble(Double::doubleValue)
            .average().getAsDouble();
    }

    public static Double getAvgCommentRating(Collection<CommentRating> ratings) {
        return ratings.stream()
            .map(CommentRating::getRating)
            .map(Double::valueOf)
            .mapToDouble(Double::doubleValue)
            .average().getAsDouble();
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
