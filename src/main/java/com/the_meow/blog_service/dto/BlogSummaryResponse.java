package com.the_meow.blog_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BlogSummaryResponse {
    private String title;
    private Integer userId;
    private String thumbnailUrl;
    private LocalDateTime publishedAt;
    private Integer readCount;
    private Double averageRating;
}
