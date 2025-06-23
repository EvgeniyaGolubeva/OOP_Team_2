package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class ForbiddenBlogAccessException extends BaseCustomException {
    public ForbiddenBlogAccessException(Integer blogId, Integer userId) {
        super(
            HttpStatus.FORBIDDEN,
            "ForbiddenAccess",
            "User " + userId + " does not have permission to update blog ID " + blogId
        );
    }
}
