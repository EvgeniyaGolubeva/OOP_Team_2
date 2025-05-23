package com.the_meow.blog_service.controller;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.service.BlogService;
import com.the_meow.blog_service.utils.Utils;

import jakarta.validation.Valid;

import java.util.Map;
import java.util.Optional;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/blogs")
public class BlogController {

    private final BlogService service;

    public BlogController(BlogService service) {
        this.service = service;
    }

    @GetMapping
    public Page<BlogInfoPublic> getAllPublishedBlogs(@ModelAttribute BlogFilterRequest filter) {
        return service.getPublishedBlogs(filter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Optional<Integer> userId = Utils.getUserId(authHeader);
        Object blog = service.getBlog(userId, id);
        return ResponseEntity.ok(blog);
    }

    @PostMapping
    public ResponseEntity<BlogInfoOwner> createBlog(
        @Valid @RequestBody BlogCreateRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        BlogInfoOwner savedBlog = service.createNewBlog(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogInfoOwner> updateBlog(
            @PathVariable Integer id,
            @RequestBody BlogCreateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) throws BadRequestException {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        BlogInfoOwner updatedBlog = service.updateBlog(id, userId, request);
        return ResponseEntity.ok(updatedBlog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.deleteBlog(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/publish")
    public ResponseEntity<Map<String, Boolean>> getPublishStatus(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        boolean isPublished = service.getPublishStatus(id, userId);
        return ResponseEntity.ok(Map.of("isPublished", isPublished));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publishBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.publishBlog(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Void> togglePublish(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.togglePublish(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/publish")
    public ResponseEntity<Void> unpublishBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.unpublishBlog(id, userId);
        return ResponseEntity.noContent().build();
    }
}
