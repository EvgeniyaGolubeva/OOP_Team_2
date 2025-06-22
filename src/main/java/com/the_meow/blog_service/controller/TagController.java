package com.the_meow.blog_service.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.model.Tag;
import com.the_meow.blog_service.service.TagService;
import com.the_meow.blog_service.utils.Utils;

@RestController
@RequestMapping("/api/v1/blogs/{blogId}/tags")
public class TagController {

    private final TagService service;

    public TagController(TagService service) {
        this.service = service;
    }

    @GetMapping("/")
    public List<String> getTagsByBlogId(
        @PathVariable Integer blogId
    ) {
        return service.getUniqueTagsByBlogId(blogId);
    }

    @PostMapping("/{tagName}")
    public ResponseEntity<Void> addTag(
        @PathVariable Integer blogId,
        @PathVariable String tagName,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.addTagToBlog(blogId, tagName, userId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    
    @DeleteMapping("/{tagName}")
    public ResponseEntity<Void> deleteTag(
        @PathVariable Integer blogId,
        @PathVariable String tagName,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.deleteTagFromBlog(blogId, tagName, userId);
        return ResponseEntity.noContent().build();
    }
}
