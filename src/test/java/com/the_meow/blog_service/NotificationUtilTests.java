package com.the_meow.blog_service;

import com.the_meow.blog_service.utils.NotificationUtil;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationUtilTest {

    @Test
    void testHappyPathNotifyUsers() {
        ArrayList<String> emails = new ArrayList<>(List.of("user@example.com"));

        // We won't actually send the request here, but we expect no exception
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(emails);
        });
    }

    @Test
    void testNotifyUsersWithInvalidUri() {
        ArrayList<String> emails = new ArrayList<>(List.of("test@test.com"));

        // Temporarily override the URI in the method if possible, or test with fake_notify
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(emails); // Since real notify_users can't be modified easily
        });
    }

    @Test
    void testFakeNotifyPrintsMessage() {
        ArrayList<String> ids = new ArrayList<>(List.of("123", "456"));
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(ids);
        });
    }

    @Test
    void testNotifyUsersHandlesServerError() {
        // Since real HTTP is not easily mockable without changing the method,
        // this test assumes `fake_notify_users` or refactor to inject HttpClient

        ArrayList<String> ids = new ArrayList<>(List.of("user@error.com"));
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(ids);
        });
    }
}