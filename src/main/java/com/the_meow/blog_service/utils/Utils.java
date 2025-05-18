package com.the_meow.blog_service.utils;

public class Utils {
    public static Integer getUserId(String token) {
        try {
            return Integer.parseInt(token);
        }
        catch (NumberFormatException e) {
            return null;
        }
    }
}
