package com.the_meow.blog_service.controller;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.service.BlogService;
import com.the_meow.blog_service.utils.Utils;

import jakarta.validation.Valid;

import java.util.Map;

import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// TODO: make it better
// TODO: this is shitty, I know
// TODO: Will be fixed, I promise

@RestController
@RequestMapping("/api/blog")
public class BlogController {

    private final BlogService service;

    public BlogController(BlogService service) {
        this.service = service;
    }

    @GetMapping
    public Page<BlogSummaryResponse> getAllPublishedBlogs(@ModelAttribute BlogFilterRequest filter) {
        return service.getPublishedBlogs(filter);

    }

    @PostMapping
    public ResponseEntity<BlogCreateResponse> createBlog(
        @Valid @RequestBody BlogCreateRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        BlogCreateResponse savedBlog = service.createNewBlog(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogCreateResponse> updateBlog(
            @PathVariable Integer id,
            @RequestBody BlogCreateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) throws BadRequestException {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        BlogCreateResponse updatedBlog = service.updateBlog(id, userId, request);
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


    @PatchMapping("/{id}/publish")
    public ResponseEntity<Void> togglePublishStatus(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);

        service.togglePublish(id, userId);
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
}
