package com.the_meow.blog_service.controller;

import com.the_meow.blog_service.dto.CommentCreate;
import com.the_meow.blog_service.dto.CommentResponse;
import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.service.CommentService;
import com.the_meow.blog_service.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blogs/{blogId}/comments")
public class CommentController {
    private final CommentService service;

    @GetMapping
    public ResponseEntity<List<CommentResponse>> getComments(
        @PathVariable Integer blogId
    ) {
        List<CommentResponse> comments = service.getCommentsByBlogId(blogId);
        return ResponseEntity.ok(comments);
    }

    @PostMapping
    public ResponseEntity<CommentResponse> createComment(
        @PathVariable Integer blogId,
        @RequestBody CommentCreate commentCreate,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        CommentResponse comment = service.createComment(blogId, userId, commentCreate);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentResponse> getCommentById(
        @PathVariable Integer blogId,
        @PathVariable Integer commentId
    ) {
        CommentResponse comment = service.getCommentById(blogId, commentId);
        return ResponseEntity.ok(comment);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentResponse> updateComment(
        @PathVariable Integer blogId,
        @PathVariable Integer commentId,
        @RequestBody CommentCreate commentUpdate,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        CommentResponse comment = service.updateComment(blogId, commentId, userId, commentUpdate);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
        @PathVariable Integer blogId,
        @PathVariable Integer commentId,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.deleteComment(blogId, commentId, userId);
        return ResponseEntity.noContent().build();
    }
}
