package com.the_meow.blog_service.dto;

import java.util.List;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogCreateResponse {
    private Integer blogId;
    private String title;
    private String thumbnailUrl;
    private List<String> tags;
}