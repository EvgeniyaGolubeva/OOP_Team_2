package com.the_meow.blog_service.controller;

import com.the_meow.blog_service.dto.RatingRequest;
import com.the_meow.blog_service.dto.RatingResponse;
import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.service.CommentRatingService;
import com.the_meow.blog_service.utils.Utils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blogs/{blogId}/comments/{commentId}/ratings")
public class CommentRatingController {
    private final CommentRatingService service;

    @GetMapping
    public ResponseEntity<RatingResponse> getRatings(
        @PathVariable Integer blogId,
        @PathVariable Integer commentId,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElse(null);
        RatingResponse rating = service.getRatingsByCommentId(blogId, commentId, userId);
        return ResponseEntity.ok(rating);
    }

    @PostMapping
    public ResponseEntity<Void> submitRating(
        @PathVariable Integer blogId,
        @PathVariable Integer commentId,
        @RequestBody RatingRequest ratingRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.submitRating(blogId, commentId, userId, ratingRequest);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping
    public ResponseEntity<Void> updateRating(
        @PathVariable Integer blogId,
        @PathVariable Integer commentId,
        @Valid @RequestBody RatingRequest ratingRequest,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.updateRating(blogId, commentId, userId, ratingRequest);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> deleteRating(
        @PathVariable Integer blogId,
        @PathVariable Integer commentId,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.deleteRating(blogId, commentId, userId);
        return ResponseEntity.noContent().build();
    }
}