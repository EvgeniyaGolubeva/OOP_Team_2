package com.the_meow.blog_service.utils;

import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BlogQuotingUtil {

    private final BlogRepository blogRepository;

    @Autowired
    public BlogQuotingUtil(BlogRepository blogRepository) {
        this.blogRepository = blogRepository;
    }

    private static final Pattern QUOTE_PATTERN = Pattern.compile("\\[\\[\\((\\d+)\\)\\s(.+?)]]");

    /**
     * Parses and validates all quoted blog references in the format [[(blogId) substring]] in the given input text.
     * For each valid quote:
     * <ul>
     *     <li>It checks whether the blog with the given ID exists.</li>
     *     <li>It checks whether the quoted substring exists within the blog content.</li>
     *     <li>If valid, it records the blog ID, the start index, and the end index of the quoted substring inside that blog.</li>
     * </ul>
     * <p>
     * Invalid or malformed quotes (e.g. nonexistent blog, null content, or substring mismatch) are skipped silently.
     * <p>
     * @param inputText the blog content potentially containing quote references
     * @return a list of longs in groups of 3: blog ID, start index (1 based), and end index (1 based) of each valid quote,
     *         or {@code null} if no valid quotes are found
     */
    public ArrayList<Long> validateQuotedBlogs(String inputText) {
        Matcher matcher = QUOTE_PATTERN.matcher(inputText);
        ArrayList<Long> positions = new ArrayList<>();

        while (matcher.find()) {
            long blogId = Long.parseLong(matcher.group(1));
            String quotedText = matcher.group(2);

            Blog referencedBlog = blogRepository.findById(blogId).orElse(null);
            if (referencedBlog == null || referencedBlog.getContent() == null) {
                continue;
            }

            long startIndex = referencedBlog.getContent().indexOf(quotedText) + 1;
            if (startIndex == 0) { // -1 + 1 = 0
                continue;
            }

            long endIndex = startIndex + quotedText.length();
            positions.add(blogId);
            positions.add(startIndex);
            positions.add(endIndex);
        }

        return positions.isEmpty() ? null : positions;
    }
}
