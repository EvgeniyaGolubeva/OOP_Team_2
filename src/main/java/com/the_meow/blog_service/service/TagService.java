package com.the_meow.blog_service.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.repository.*;

@Service
public class TagService {

    private final TagRepository repo;
    private final BlogRepository blogRepository;

    public TagService(TagRepository repo, BlogRepository blogRepository) {
        this.repo = repo;
        this.blogRepository = blogRepository;
    }

    public List<String> getAllUniqueTags() {
        return repo.findAllUniqueTagNames();
    }

    public List<String> getUniqueTagsByBlogId(Integer blogId) {
        return repo.findUniqueTagNamesByBlogId(blogId);
    }

    public Tag addTagToBlog(Integer blogId, String tagName) {
        Blog blog = blogRepository.findById(blogId.longValue())
            .orElseThrow(() -> new BlogNotFoundException(blogId));
    
        Optional<Tag> existingTag = repo.findByBlogIdAndName(blogId, tagName);
        if (existingTag.isPresent()) {
            throw new TagAlreadyExistsException(tagName, blogId);
        }
    
        Tag tag = new Tag();
        tag.setBlog(blog);
        tag.setName(tagName);
        return repo.save(tag);
    }
    
    public void deleteTagFromBlog(Integer blogId, String tagName) {
        Tag tag = repo.findByBlogIdAndName(blogId, tagName)
            .orElseThrow(() -> new  TagNotFoundException(tagName, blogId));
    
        repo.delete(tag);
    }

    public List<String> searchTags(String name, String matchType) {
        switch (matchType.toLowerCase()) {
            case "regex":
                return repo.regexSearch(name);
            case "fuzzy":
                return repo.fuzzySearch(name);
            default:
                throw new IllegalArgumentException("Unsupported match type: " + matchType);
        }
    }
}
