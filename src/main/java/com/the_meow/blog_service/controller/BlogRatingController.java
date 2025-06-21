package com.the_meow.blog_service.controller;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.service.*;
import com.the_meow.blog_service.utils.*;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/blogs/{blogId}/ratings")
public class BlogRatingController {
    private final BlogRatingService service;

    public BlogRatingController(BlogRatingService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<RatingResponse> getRating(
        @PathVariable Integer blogId,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElse(null);
        RatingResponse rating = service.getRating(blogId, userId);
        return ResponseEntity.ok(rating);
    }

    @PostMapping
    public ResponseEntity<Void> submitRating(
        @PathVariable Integer blogId,
        @Valid @RequestBody RatingRequest ratingRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.submitRating(blogId, userId, ratingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateRating(
        @PathVariable Integer blogId,
        @Valid @RequestBody RatingRequest ratingRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.updateRating(blogId, userId, ratingRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRating(
        @PathVariable Integer blogId,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.deleteRating(blogId, userId);
        return ResponseEntity.noContent().build();
    }
}
