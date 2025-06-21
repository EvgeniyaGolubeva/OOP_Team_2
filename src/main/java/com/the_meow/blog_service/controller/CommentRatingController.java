package com.the_meow.blog_service.controller;

import com.the_meow.blog_service.model.CommentRating;
import com.the_meow.blog_service.service.CommentRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class CommentRatingController {

    private final CommentRatingService ratingService;

    @PostMapping
    public ResponseEntity<CommentRating> createRating(@RequestBody CommentRating rating) {
        return ResponseEntity.ok(ratingService.save(rating));
    }

    @GetMapping("/comment/{commentId}")
    public ResponseEntity<List<CommentRating>> getRatingsByComment(@PathVariable Integer commentId) {
        return ResponseEntity.ok(ratingService.findAllByCommentId(commentId));
    }

    @GetMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<CommentRating> getRatingByCommentAndUser(@PathVariable Integer commentId,
                                                                   @PathVariable Integer userId) {
        Optional<CommentRating> rating = ratingService.findByCommentIdAndUserId(commentId, userId);
        return rating.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<CommentRating> updateRating(@PathVariable Integer commentId,
                                                      @PathVariable Integer userId,
                                                      @RequestBody CommentRating updatedRating) {
        updatedRating.setComment(ratingService.findByCommentIdAndUserId(commentId, userId)
                .map(CommentRating::getComment)
                .orElseThrow());
        updatedRating.setUserId(userId);
        return ResponseEntity.ok(ratingService.save(updatedRating));
    }

    @DeleteMapping("/comment/{commentId}/user/{userId}")
    public ResponseEntity<Void> deleteRating(@PathVariable Integer commentId,
                                             @PathVariable Integer userId) {
        ratingService.deleteByCommentIdAndUserId(commentId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/comment/{commentId}/average")
    public ResponseEntity<Float> getAverageRating(@PathVariable Integer commentId) {
        return ResponseEntity.ok(ratingService.getAverageRatingForComment(commentId));
    }
}