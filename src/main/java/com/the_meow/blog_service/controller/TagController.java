package com.the_meow.blog_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.the_meow.blog_service.model.Tag;
import com.the_meow.blog_service.service.TagService;

@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @GetMapping("/")
    public List<String> getTags(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "fuzzy") String match
    ) {
        if (name != null && !name.isBlank()) {
            return service.searchTags(name, match);
        } else {
            return service.getAllUniqueTags();
        }
    }

    @GetMapping("/blog/{blogId}")
    public List<String> getTagsByBlogId(@PathVariable Integer blogId) {
        return service.getUniqueTagsByBlogId(blogId);
    }

    @PostMapping("/blog/{blogId}/{tagName}")
    public ResponseEntity<Tag> addTag(
        @PathVariable Integer blogId,
        @PathVariable String tagName
    ) {
        Tag createdTag = service.addTagToBlog(blogId, tagName);
        return new ResponseEntity<>(createdTag, HttpStatus.CREATED);
    }
    
    @DeleteMapping("/blog/{blogId}/{tagName}")
    public ResponseEntity<Void> deleteTag(
        @PathVariable Integer blogId,
        @PathVariable String tagName
    ) {
        service.deleteTagFromBlog(blogId, tagName);
        return ResponseEntity.noContent().build();
    }
}
