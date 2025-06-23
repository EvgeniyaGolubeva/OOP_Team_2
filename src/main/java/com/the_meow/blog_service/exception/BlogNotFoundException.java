package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class BlogNotFoundException extends BaseCustomException {
    public BlogNotFoundException(Integer blogId) {
        super(
            HttpStatus.NOT_FOUND,
            "BlogNotFound",
            "Blog with ID " + blogId + " does not exist."
        );
    }
}