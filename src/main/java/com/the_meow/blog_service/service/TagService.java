package com.the_meow.blog_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.repository.*;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TagService {

    private final TagRepository repo;
    private final BlogRepository blogRepository;

    public TagService(TagRepository repo, BlogRepository blogRepository) {
        this.repo = repo;
        this.blogRepository = blogRepository;
    }

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

    public Tag addTagToBlog(Integer blogId, String tagName) {
        log.info("Adding tag '{}' to blog {}", tagName, blogId);
        Blog blog = blogRepository.findById(blogId.longValue())
            .orElseThrow(() -> {
                log.warn("Blog not found: id={}", blogId);
                return new BlogNotFoundException(blogId);
            });

        Optional<Tag> existingTag = repo.findByBlogIdAndName(blogId, tagName);
        if (existingTag.isPresent()) {
            log.warn("Tag '{}' already exists for blog {}", tagName, blogId);
            throw new TagAlreadyExistsException(tagName, blogId);
        }

        Tag tag = new Tag();
        tag.setBlog(blog);
        tag.setName(tagName);
        Tag saved = repo.save(tag);
        log.info("Tag '{}' added to blog {}", tagName, blogId);
        return saved;
    }
    
    public void deleteTagFromBlog(Integer blogId, String tagName) {
        log.info("Deleting tag '{}' from blog {}", tagName, blogId);
        Tag tag = repo.findByBlogIdAndName(blogId, tagName)
            .orElseThrow(() -> {
                log.warn("Tag '{}' not found for blog {}", tagName, blogId);
                return new TagNotFoundException(tagName, blogId);
            });

        repo.delete(tag);
        log.info("Tag '{}' deleted from blog {}", tagName, blogId);
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
