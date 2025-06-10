package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class AlreadyRatedException extends BaseCustomException {

    public AlreadyRatedException() {
        super(
            HttpStatus.CONFLICT,
            "AlreadyRated",
            "The material is already rated"
        );
    }
    
}
