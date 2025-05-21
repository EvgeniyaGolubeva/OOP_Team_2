package com.the_meow.blog_service.utils;

import java.util.Optional;

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
}
