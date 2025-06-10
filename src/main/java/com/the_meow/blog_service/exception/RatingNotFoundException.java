package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class RatingNotFoundException extends BaseCustomException {
    public RatingNotFoundException() {
        super(
            HttpStatus.NOT_FOUND,
            "RatingNotFound",
            "No such rating was found"
        );
    }
}
