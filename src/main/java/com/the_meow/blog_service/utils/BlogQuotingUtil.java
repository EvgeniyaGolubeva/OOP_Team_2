package com.the_meow.blog_service.utils;

import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.repository.BlogRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BlogQuotingUtil {
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
     * @return a list of integers in groups of 3: blog ID, start index, and end index of each valid quote,
     *         or {@code null} if no valid quotes are found
     */
    public ArrayList<Integer> validateQuotedBlogs(String inputText, BlogRepository blogRepository) {
        Matcher matcher = QUOTE_PATTERN.matcher(inputText);
        ArrayList<Integer> positions = new ArrayList<>();

        while (matcher.find()) {
            long blogId = Long.parseLong(matcher.group(1));
            String quotedText = matcher.group(2);

            Blog referencedBlog = blogRepository.findById(blogId).orElse(null);
            if (referencedBlog == null || referencedBlog.getContent() == null) {
                continue;
            }

            int startIndex = referencedBlog.getContent().indexOf(quotedText);
            if (startIndex == -1) {
                continue;
            }

            int endIndex = startIndex + quotedText.length();
            positions.add(Math.toIntExact(blogId));
            positions.add(startIndex);
            positions.add(endIndex);
        }

        return positions.isEmpty() ? null : positions;
    }
}
