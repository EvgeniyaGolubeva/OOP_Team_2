package com.the_meow.blog_service.dto;

import java.util.List;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class BlogCreateRequest {
    @NotBlank(message = "Title is required")
    @Size(max = 120, message = "Title must not exceed 120 characters")
    @Pattern(regexp = "^[a-zA-Z0-9\\-\\s]+$", message = "Title contains invalid characters")
    private String title;

    @Size(min = 10, max = 50000, message = "Content must be between 10 and 50000 characters")
    private String content;

    @Size(max = 2048, message = "Thumbnail URL must not exceed 2048 characters")
    private String thumbnailUrl;

    @Size(max = 20, message = "Tags array must have 0-20 elements")
    private List<
        @Size(min = 1, max = 30, message = "Tags must be 1-30 characters")
        @Pattern(regexp = "^[a-zA-Z0-9\\-]+$", message = "Tags must be alphanumeric or hyphen")
        String
    > tags;
}
