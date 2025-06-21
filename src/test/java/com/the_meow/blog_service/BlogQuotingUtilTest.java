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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        List<Integer> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(1, 10, 21), result);
    }

    @Test
    void testMultipleValidQuotesFromSameBlog() {
        Blog blog = new Blog();
        blog.setContent("first second third");

        when(blogRepository.findById(1L)).thenReturn(Optional.of(blog));

        String input = "[[(1) first]] and [[(1) second]] and [[(1) third]]";
        List<Integer> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(1, 0, 5, 1, 6, 12, 1, 13, 18), result);
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
        List<Integer> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(1, 0, 5, 2, 4, 7), result);
    }

    @Test
    void testValidQuoteAtStartOfBlog() {
        Blog blog = new Blog();
        blog.setContent("Start and middle and end.");

        when(blogRepository.findById(3L)).thenReturn(Optional.of(blog));

        String input = "[[(3) Start]]";
        List<Integer> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(3, 0, 5), result);
    }

    @Test
    void testValidQuoteWithExtraSpacesInInput() {
        Blog blog = new Blog();
        blog.setContent("The final quote is here.");

        when(blogRepository.findById(4L)).thenReturn(Optional.of(blog));

        String input = "  something  [[(4) quote is]]  ";
        List<Integer> result = quotingUtil.validateQuotedBlogs(input);

        assertEquals(List.of(4, 11, 18), result);
    }
}