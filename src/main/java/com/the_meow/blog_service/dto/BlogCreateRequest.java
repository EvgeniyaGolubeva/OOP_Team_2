package com.the_meow.blog_service.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class BlogCreateRequest {
    @NotBlank(message = "Title is required")
    private String title;

    private String thumbnailUrl;

    @NotBlank(message = "Content is required")
    private String content;

    private List<String> tags;
}
