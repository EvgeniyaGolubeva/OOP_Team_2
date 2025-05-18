package com.the_meow.blog_service.model;

import jakarta.validation.constraints.*;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Entity
@Table(name = "blog_ratings")
@IdClass(BlogRating.BlogRatingId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BlogRating {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @Id
    private Integer userId;

    @Min(0)
    @Max(5)
    private Float rating;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BlogRatingId implements Serializable {
        private Integer blog;
        private Integer userId;
    }
}
