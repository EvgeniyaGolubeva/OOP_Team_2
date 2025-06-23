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
}
