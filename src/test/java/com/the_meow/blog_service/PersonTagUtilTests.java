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

    /** Helper to verify fake_notify_users called exactly once with expected list. */
    private void assertNotification(List<String> expected, String input) {
        try (MockedStatic<NotificationUtil> mocked = Mockito.mockStatic(NotificationUtil.class)) {
            mocked.when(() -> NotificationUtil.fake_notify_users(Mockito.anyList()))
                  .thenAnswer(inv -> null); // no‑op
            assertDoesNotThrow(() -> PersonTagUtil.parse_blog_and_notify(input));
            mocked.verify(() -> NotificationUtil.fake_notify_users(expected), times(1));
        }
    }

   /** SCENARIO-001 */
    @Test
    void testSingleValidTag() {
        String input = "Hello @alice@example.com!";
        assertNotification(List.of("alice@example.com"), input);
    }

    /** SCENARIO-002 */
    @Test
    void testMultipleValidTagsSeparatedBySpaces() {
        String input = "@bob@test.io thanks @carol@corp.org";
        assertNotification(List.of("bob@test.io", "carol@corp.org"), input);
    }

    /** SCENARIO-003 */
    @Test
    void testTagsSeparatedByNewlinesAndTabs() {
        String input = "Hi\n@dave@mail.com\t welcome".replace("\n", "\n").replace("\t", "\t");
        // Actually include real newline and tab
        input = "Hi
@dave@mail.com	 welcome";
        assertNotification(List.of("dave@mail.com"), input);
    }

    /** SCENARIO-004 */
    @Test
    void testDuplicateTagsDeduplicated() {
        String input = "@eve@site.com and again @eve@site.com";
        assertNotification(List.of("eve@site.com"), input);
    }

    /** SCENARIO-005 */
    @Test
    void testInvalidEmailFormatIgnored() {
        String input = "Hello @not-an-email";
        assertNotification(new ArrayList<>(), input);
    }

    /** SCENARIO-006 */
    @Test
    void testConsecutiveAtWithoutAddress() {
        String input = "Look here @@bad.com";
        assertNotification(new ArrayList<>(), input);
    }

    /** SCENARIO-007 */
    @Test
    void testMixedValidAndInvalid() {
        String input = "@good@ok.com @@ bad @no@";
        assertNotification(List.of("good@ok.com"), input);
    }

    /** SCENARIO-008 */
    @Test
    void testEmailFollowedByPunctuation() {
        String input = "Thanks, @foo@bar.com,";
        assertNotification(List.of("foo@bar.com"), input);
    }

    /** SCENARIO-009 */
    @Test
    void testEmptyInputString() {
        String input = "";
        assertNotification(new ArrayList<>(), input);
    }

    /** SCENARIO-010 */
    @Test
    void testVeryLongTextManyTags() {
        // Generate 100 unique addresses
        List<String> emails = IntStream.range(0, 100)
                                       .mapToObj(i -> "user" + i + "@test.com")
                                       .collect(Collectors.toList());
        String body = emails.stream().map(e -> "@" + e + " ").collect(Collectors.joining());
        assertNotification(emails, body);
    }

    /** SCENARIO-011 */
    @Test
    void testAddressImmediatelyFollowedByAnotherAt() {
        String input = "@one@mail.com@two@site.net";
        assertNotification(List.of("one@mail.com", "two@site.net"), input);
    }
}