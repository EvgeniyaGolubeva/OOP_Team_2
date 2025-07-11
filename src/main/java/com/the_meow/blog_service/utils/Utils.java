package com.the_meow.blog_service.utils;

import java.util.Collection;
import java.util.Optional;

import com.the_meow.blog_service.model.BlogRating;
import com.the_meow.blog_service.model.CommentRating;

public class Utils {
    public static Optional<Integer> getUserId(String token) {
        if (token == null) {
            return Optional.empty();
        }

        if (token.isBlank()) {
            return Optional.empty();
        }

        try {
            return Optional.of(Integer.parseInt(token));
        }
        catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static Double getAvgBlogRating(Collection<BlogRating> ratings) {
        return ratings.stream()
            .map(BlogRating::getRating)
            .map(Double::valueOf)
            .mapToDouble(Double::doubleValue)
            .average().orElse(0.0);
    }

    public static Double getAvgCommentRating(Collection<CommentRating> ratings) {
        return ratings.stream()
            .map(CommentRating::getRating)
            .map(Double::valueOf)
            .mapToDouble(Double::doubleValue)
            .average().getAsDouble();
    }
}
