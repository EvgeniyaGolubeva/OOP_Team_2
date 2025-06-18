package com.the_meow.blog_service;

import com.the_meow.blog_service.utils.PersonTagUtil;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class PersonTagUtilTest {
    @Test
    void testIsValidEmail_validEmail() {
        assertTrue(PersonTagUtil.isValidEmail("john.doe@example.com"));
    }

    @Test
    void testIsValidEmail_invalidEmailMissingAt() {
        assertFalse(PersonTagUtil.isValidEmail("john.doeexample.com"));
    }

    @Test
    void testIsValidEmail_invalidEmailBadFormat() {
        assertTrue(PersonTagUtil.isValidEmail("bad@com"));
    }

    @Test
    void testParseBlogAndNotify_withValidEmails_shouldNotifyThem() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String blogText = "Hey check out @jane.doe@example.com and @john@example.org in the post!";
        PersonTagUtil.parse_blog_and_notify(blogText);

        System.setOut(originalOut);
        String output = out.toString().trim();

        assertFalse(output.contains("jane.doe@example.com"));
        assertFalse(output.contains("john@example.org"));
    }

    @Test
    void testParseBlogAndNotify_withMixedEmails_shouldOnlyNotifyValidOnes() {
        PrintStream originalOut = System.out;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        String blogText = "Mentioned: @notanemail @bob@example.com @bad.email @alice@test.org";
        PersonTagUtil.parse_blog_and_notify(blogText);

        System.setOut(originalOut);
        String output = out.toString().trim();

        assertFalse(output.contains("bob@example.com"));
        assertFalse(output.contains("alice@test.org"));
        assertFalse(output.contains("notanemail"));
        assertFalse(output.contains("bad.email"));
    }

    @Test
    void testParseBlogAndNotify_withNoEmails_shouldNotifyNothing() {
        String blogText = "This blog has no mentions at all.";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setOut(new PrintStream(out));

        PersonTagUtil.parse_blog_and_notify(blogText);

        String output = out.toString().trim();
        assertTrue(output.contains("[]"));
    }}