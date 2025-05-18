package com.the_meow.blog_service.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class BlogFilterRequest {
    private String title;
    private Integer userId;
    private String tag;
    private Integer minReadCount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedAfter;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime publishedBefore;

    private int page = 0;
    private int size = 10;
}
