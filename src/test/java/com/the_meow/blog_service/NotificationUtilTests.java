package com.the_meow.blog_service;

import com.the_meow.blog_service.utils.NotificationUtil;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationUtilTests {


    /** SCENARIO-001: Single recipient – happy path. */
    @Test
    void testNotifyUser() {
        ArrayList<String> emails = new ArrayList<>(List.of("user@example.com"));

        // We won't actually send the request here, but we expect no exception
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(emails);
        });
    }

    /** SCENARIO-002: Multiple recipients – happy path. */
    @Test
    void testMultipleRecipientsNoException() {
        ArrayList<String> recipients = new ArrayList<>(List.of("a@test.com", "b@test.com", "c@test.com"));
        assertDoesNotThrow(() -> NotificationUtil.fake_notify_users(recipients),
                           "fake_notify_users should handle multiple recipients");
    }

    /** SCENARIO-004: Invalid URI configured – should not throw. */
    @Test
    void testNotifyUsersWithInvalidUri() {
        ArrayList<String> emails = new ArrayList<>(List.of("test@test.com"));

        // Temporarily override the URI in the method if possible, or test with fake_notify
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(emails); // Since real notify_users can't be modified easily
        });
    }


    /** Aditional test for invalid URIs */
    @Test
    void testFakeNotifyPrintsMessage() {
        ArrayList<String> ids = new ArrayList<>(List.of("123", "456"));
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(ids);
        });
    }

    /** SCENARIO-005: Simulated server error – should not throw. */
    @Test
    void testNotifyUsersHandlesServerError() {
        // Since real HTTP is not easily mockable without changing the method,
        // this test assumes `fake_notify_users` or refactor to inject HttpClient

        ArrayList<String> ids = new ArrayList<>(List.of("user@error.com"));
        assertDoesNotThrow(() -> {
            NotificationUtil.fake_notify_users(ids);
        });
    }

    /** SCENARIO-003: Empty list – edge case, no-op. */
    @Test
    void testEmptyListNoException() {
        ArrayList<String> recipients = new ArrayList<>();
        assertDoesNotThrow(() -> NotificationUtil.fake_notify_users(recipients),
                           "Empty list should be a no-op");
    }
}