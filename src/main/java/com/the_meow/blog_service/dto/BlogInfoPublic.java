package com.the_meow.blog_service.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.model.Tag;
import com.the_meow.blog_service.utils.Utils;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlogInfoPublic {
    private Integer id;
    private String title;
    private Integer ownerId;
    private String thumbnailUrl;
    private LocalDateTime publishedAt;
    private List<String> tags;
    private Double averageRating;

    public BlogInfoPublic(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.ownerId = blog.getUserId();
        this.thumbnailUrl = blog.getThumbnailUrl();
        this.publishedAt = blog.getPublishedAt();
        this.tags = blog.getTags().stream().map(Tag::getName).toList();
        this.averageRating = Utils.getAvgBlogRating(blog.getRatings());
    }
}
