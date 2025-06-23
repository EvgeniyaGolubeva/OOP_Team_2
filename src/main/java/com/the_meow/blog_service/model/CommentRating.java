package com.the_meow.blog_service.model;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "comment_ratings")
@IdClass(CommentRating.CommentRatingId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRating {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private Comment comment;

    @Id
    private Integer userId;

    @Min(0)
    @Max(5)
    private Double rating;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CommentRatingId implements Serializable {
        private Integer commentId;
        private Integer userId;
    }
}
