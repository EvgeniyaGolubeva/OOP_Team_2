package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class CommentNotFoundException extends BaseCustomException {
    public CommentNotFoundException() {
        super(
            HttpStatus.NOT_FOUND,
            "BadAuthToken",
            "The authorization token provided is either missing, malformed, expired, or does not match any known user session. Please ensure you are sending a valid and active token in the Authorization header."
        );
    }
}