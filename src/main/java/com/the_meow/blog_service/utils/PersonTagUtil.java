package com.the_meow.blog_service.utils;

import com.the_meow.blog_service.model.Blog;
import jakarta.mail.internet.InternetAddress;

import java.util.ArrayList;

public abstract class PersonTagUtil {
    public static void parse_blog_and_notify(String text) {
        ArrayList<String> validEmails = new ArrayList<>();

        int i = 0;
        while (i < text.length()) {
            if (text.charAt(i) == '@') {
                int start = i + 1;
                int end = start;

                while (end < text.length()) {
                    char c = text.charAt(end);
                    if (c == ' ' || c == '\t' || c == '\n' || c == '@') break;
                    end++;
                }

                String possibleEmail = text.substring(start, end);

                if (isValidEmail(possibleEmail)) {
                    validEmails.add(possibleEmail);
                }

                i = end;
            } else {
                i++;
            }
        }

        NotificationUtil.fake_notify_users(validEmails);
    }

    public static boolean isValidEmail(String email) {
        try {
            InternetAddress address = new InternetAddress(email);
            address.validate();
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
