package com.the_meow.blog_service.controller;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.service.BlogService;
import com.the_meow.blog_service.utils.Utils;

import jakarta.validation.Valid;

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
        Integer user_id = Utils.getUserId(authHeader);
        if (user_id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BlogCreateResponse savedBlog = service.createNewBlog(request, user_id);
        if (savedBlog != null) {
            return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
        }
        else {
            return ResponseEntity.status(HttpStatus.I_AM_A_TEAPOT).build();
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogCreateResponse> updateBlog(
            @PathVariable Integer id,
            @RequestBody BlogCreateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) throws BadRequestException {
        Integer user_id = Utils.getUserId(authHeader);
        if (user_id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        BlogCreateResponse response = service.updateBlog(id, user_id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) throws BadRequestException {
        Integer user_id = Utils.getUserId(authHeader);
        if (user_id == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        service.deleteBlog(id, user_id);
        return ResponseEntity.noContent().build();
    }

    
}
