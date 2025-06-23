package com.the_meow.blog_service;

import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.repository.BlogRepository;
import com.the_meow.blog_service.utils.BlogQuotingUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BlogQuotingUtilTest {

    @Mock
    private BlogRepository blogRepository;

    private BlogQuotingUtil quotingUtil;

    @BeforeEach
    void setup() {
        quotingUtil = new BlogQuotingUtil(blogRepository);
    }

    @Test
    void testSingleValidQuote() {
        Blog blog = new Blog();
        blog.setContent("This is a quoted part.");

        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));

        String input = "[[(1) quoted part]]";
        List<Long> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(1L, 11L, 22L), result);
    }

    @Test
    void testMultipleValidQuotesFromSameBlog() {
        Blog blog = new Blog();
        blog.setContent("first second third");

        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));

        String input = "[[(1) first]] and [[(1) second]] and [[(1) third]]";
        List<Long> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(1L, 1L, 6L, 1L, 7L, 13L, 1L, 14L, 19L), result);
    }

    @Test
    void testMultipleValidQuotesFromDifferentBlogs() {
        Blog blog1 = new Blog();
        blog1.setContent("apple banana");
        Blog blog2 = new Blog();
        blog2.setContent("cat dog");

        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog1));
        when(blogRepository.findById(2L)).thenReturn(Optional.of(blog2));

        String input = "[[(1) apple]] then [[(2) dog]]";
        List<Long> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(1L, 1L, 6L, 2L, 5L, 8L), result);
    }

    @Test
    void testValidQuoteAtStartOfBlog() {
        Blog blog = new Blog();
        blog.setContent("Start and middle and end.");

        when(blogRepository.findById(3L)).thenReturn(Optional.of(blog));

        String input = "[[(3) Start]]";
        List<Long> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(3L, 1L, 6L), result);
    }

    @Test
    void testValidQuoteWithExtraSpacesInInput() {
        Blog blog = new Blog();
        blog.setContent("The final quote is here.");

        when(blogRepository.findById(4L)).thenReturn(Optional.of(blog));

        String input = "  something  [[(4) quote is]]  ";
        List<Long> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(4L, 11L, 19L), result);
    }

    // NEGATIVE TEST CASES

    @Test
    void testNonExistentBlog() {
        when(blogRepository.findById(999L)).thenReturn(Optional.empty());
        String input = "[[(999) whatever]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testBlogWithNullContent() {
        Blog blog = new Blog();
        blog.setContent(null);
        when(blogRepository.findById(5L)).thenReturn(Optional.of(blog));

        String input = "[[(5) anything]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testQuotedTextNotInContent() {
        Blog blog = new Blog();
        blog.setContent("This blog does not contain it.");
        when(blogRepository.findById(6L)).thenReturn(Optional.of(blog));

        String input = "[[(6) not found text]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testMalformedQuoteMissingParentheses() {
        String input = "[[6) text]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testMalformedQuoteNoId() {
        String input = "[[( ) text]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testEmptyInput() {
        String input = "";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testMalformedQuoteNoSpaceAfterId() {
        String input = "[[(7)text]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testMalformedQuoteExtraBracket() {
        String input = "[[[(8) text]]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testQuoteWithInvalidIdFormat() {
        String input = "[[(abc) text]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testBlogIdPresentButContentIsEmpty() {
        Blog blog = new Blog();
        blog.setContent("");
        when(blogRepository.findById(9L)).thenReturn(Optional.of(blog));

        String input = "[[(9) something]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testMultipleQuotesOneInvalid() {
        Blog blog = new Blog();
        blog.setContent("hello world");
        when(blogRepository.findById(10L)).thenReturn(Optional.of(blog));

        String input = "[[(10) hello]] and [[(10) notfound]]";
        List<Long> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(10L, 1L, 6L), result);
    }

    @Test
    void testAllQuotesInvalid() {
        Blog blog = new Blog();
        blog.setContent("nothing matches");
        when(blogRepository.findById(11L)).thenReturn(Optional.of(blog));

        String input = "[[(11) nope]] [[(11) neither]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testQuoteWithOnlyWhitespace() {
        Blog blog = new Blog();
        blog.setContent("anything");
        when(blogRepository.findById(12L)).thenReturn(Optional.of(blog));

        String input = "[[(12)     ]]";
        assertNull(quotingUtil.validateQuotedBlogs(input));
    }

    @Test
    void testQuoteOverlappingAnotherQuote() {
        Blog blog = new Blog();
        blog.setContent("one two three");
        when(blogRepository.findById(13L)).thenReturn(Optional.of(blog));

        String input = "[[(13) one two]] and [[(13) two three]]";
        List<Long> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(13L, 1L, 8L, 13L, 5L, 14L), result);
    }
}
