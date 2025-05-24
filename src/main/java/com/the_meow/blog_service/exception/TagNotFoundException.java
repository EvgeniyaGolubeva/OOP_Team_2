package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class TagNotFoundException extends BaseCustomException {

    public TagNotFoundException(String tagName, Integer blogId) {
        super(
            HttpStatus.NOT_FOUND,
            "TagNotFound",
            "No tag named '" + tagName + "' exists for blog[" + blogId + "]"
        );
    }
}
