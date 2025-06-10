package com.the_meow.blog_service.dto;


public class RatingResponse {

    private double averageRating;
    private int totalRatings;
    private Integer userRating;

    public RatingResponse() {}

    public RatingResponse(double averageRating, int totalRatings, Integer userRating) {
        this.averageRating = averageRating;
        this.totalRatings = totalRatings;
        this.userRating = userRating;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    public void setTotalRatings(int totalRatings) {
        this.totalRatings = totalRatings;
    }

    public Integer getUserRating() {
        return userRating;
    }

    public void setUserRating(Integer userRating) {
        this.userRating = userRating;
    }
}
