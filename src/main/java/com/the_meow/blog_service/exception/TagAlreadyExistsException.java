package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class TagAlreadyExistsException extends BaseCustomException {
    public TagAlreadyExistsException(String tagName, Integer blogId) {
        super(
            HttpStatus.CONFLICT,
            "TagAlreadyExists",
            "A tag named '" + tagName + "' already exists for blog[" + blogId + "]."
        );
    }
}
