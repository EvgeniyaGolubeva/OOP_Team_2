package com.the_meow.blog_service.dto;

import jakarta.validation.constraints.*;

public class RatingRequest {

    @NotNull
    @Min(1)
    @Max(5)
    private Integer rating;

    public RatingRequest() {}

    public RatingRequest(Integer rating) {
        this.rating = rating;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}
