package com.the_meow.blog_service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.model.Tag;
import com.the_meow.blog_service.utils.Utils;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
public class BlogOwner {
    private Integer id;
    private String title;
    private String content;
    private Integer ownerId;
    private String thumbnailUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isPublished;
    private Optional<LocalDateTime> publishedAt;
    private Integer readCount;
    private List<String> tags;
    private Double averageRating;

    public BlogOwner(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.content = blog.getContent();
        this.ownerId = blog.getUserId();
        this.thumbnailUrl = blog.getThumbnailUrl();
        this.createdAt = blog.getCreatedAt();
        this.updatedAt = blog.getUpdatedAt();
        this.isPublished = blog.getIsPublished();
        this.publishedAt = Optional.ofNullable(blog.getPublishedAt());
        this.readCount = blog.getReadCount();
        this.tags = blog.getTags().stream().map(Tag::getName).toList();
        this.averageRating = Utils.getAvgBlogRating(blog.getRatings());
    }
}

