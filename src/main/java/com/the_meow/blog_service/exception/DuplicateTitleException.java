package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class DuplicateTitleException extends BaseCustomException {

    public DuplicateTitleException(String title, Integer userId) {
        super(HttpStatus.CONFLICT,
        "DuplicateTitle",
        "A blog with the title '" + title + "' already exists for this user[" + userId +"].");
    }

}
