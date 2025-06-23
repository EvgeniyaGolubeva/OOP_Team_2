package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenException extends BaseCustomException {
    public ForbiddenException(String message) {
        super(
            HttpStatus.FORBIDDEN,
            "Forbidden",
            message != null ? message : "You do not have permission to perform this action."
        );
    }

    public ForbiddenException() {
        this(null);
    }
}