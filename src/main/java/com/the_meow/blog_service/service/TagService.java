package com.the_meow.blog_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagService {
    private final TagRepository repo;
    private final BlogRepository blogRepository;

    public List<String> getAllUniqueTags() {
        log.info("Fetching all unique tags");
        List<String> tags = repo.findAllUniqueTagNames();
        log.debug("Found {} unique tags", tags.size());
        return tags;
    }

    public List<String> getUniqueTagsByBlogId(Integer blogId) {
        log.info("Fetching unique tags for blogId={}", blogId);
        List<String> tags = repo.findUniqueTagNamesByBlogId(blogId);
        log.debug("Found {} tags for blogId={}", tags.size(), blogId);
        return tags;
    }

    public void addTagToBlog(Integer blogId, String tagName, Integer userId) {
        log.info("User {} is adding tag '{}' to blog {}", userId, tagName, blogId);
    
        Blog blog = blogRepository.findById(blogId.longValue())
            .orElseThrow(() -> {
                log.warn("Blog not found: id={}", blogId);
                return new BlogNotFoundException(blogId);
            });
    
        if (!blog.getUserId().equals(userId)) {
            throw new BadAuthTokenException();
        }
    
        Optional<Tag> existingTag = repo.findByBlogIdAndName(blogId, tagName);
        if (existingTag.isPresent()) {
            log.warn("Tag '{}' already exists for blog {}", tagName, blogId);
            throw new TagAlreadyExistsException(tagName, blogId);
        }
    
        Tag tag = new Tag();
        tag.setBlog(blog);
        tag.setName(tagName);
        repo.save(tag);
    
        log.info("User {} added tag '{}' to blog {}", userId, tagName, blogId);
    }
    

    public void deleteTagFromBlog(Integer blogId, String tagName, Integer userId) {
        log.info("User {} is deleting tag '{}' from blog {}", userId, tagName, blogId);
    
        Tag tag = repo.findByBlogIdAndName(blogId, tagName)
            .orElseThrow(() -> {
                log.warn("Tag '{}' not found for blog {}", tagName, blogId);
                return new TagNotFoundException(tagName, blogId);
            });
    
        if (!tag.getBlog().getUserId().equals(userId)) {
            throw new BadAuthTokenException();
        }
    
        repo.delete(tag);
        log.info("User {} deleted tag '{}' from blog {}", userId, tagName, blogId);
    }
    

    public List<String> searchTags(String name, String matchType) {
        log.info("Searching tags with name='{}' and matchType='{}'", name, matchType);
        switch (matchType.toLowerCase()) {
            case "regex":
                List<String> regexResults = repo.regexSearch(name);
                log.debug("Regex search found {} results", regexResults.size());
                return regexResults;
            case "fuzzy":
                List<String> fuzzyResults = repo.fuzzySearch(name);
                log.debug("Fuzzy search found {} results", fuzzyResults.size());
                return fuzzyResults;
            default:
                log.error("Unsupported match type: {}", matchType);
                throw new IllegalArgumentException("Unsupported match type: " + matchType);
        }
    }
}
