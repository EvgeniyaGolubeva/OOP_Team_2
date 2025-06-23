package com.the_meow.blog_service.dto;

import java.util.Optional;

import lombok.Data;

@Data
public class BlogEditRequest {
    private Optional<String> title;
    private Optional<String> thumbnailUrl;
    private Optional<String> content;
}
