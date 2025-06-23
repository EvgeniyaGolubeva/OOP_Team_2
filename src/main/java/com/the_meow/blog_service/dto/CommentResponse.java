package com.the_meow.blog_service.dto;

import java.time.LocalDateTime;

import com.the_meow.blog_service.model.Comment;
import com.the_meow.blog_service.utils.Utils;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class CommentResponse {
    private Integer id;
    private Integer ownerId;
    private Integer blogId;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;

    public CommentResponse(Comment comment) {
        this.id = comment.getId();
        this.ownerId = comment.getUserId();
        this.blogId = comment.getBlog().getId();
        this.content = comment.getContent();
        this.createdAt = comment.getCreatedAt();
        this.updatedAt = comment.getUpdatedAt();
        this.averageRating = Utils.getAvgCommentRating(comment.getCommentRatings());
    }
}
