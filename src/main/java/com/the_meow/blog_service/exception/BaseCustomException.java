package com.the_meow.blog_service.exception;

import org.springframework.http.HttpStatus;

public class BaseCustomException extends RuntimeException {
    private HttpStatus status;
    private String detail;

    public BaseCustomException(HttpStatus status, String message, String detail) {
        super(message);
        this.status = status;
        this.detail = detail;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDetail() {
        return detail;
    }

    public String getError() {
        return super.getMessage();
    }
}
