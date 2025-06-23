package com.the_meow.blog_service.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RatingResponse {
    private Double averageRating;
    private int totalRatings;
    private Double userRating;
}
