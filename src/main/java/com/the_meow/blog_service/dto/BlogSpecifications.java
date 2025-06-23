package com.the_meow.blog_service.dto;

import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.model.Tag;

import java.time.LocalDateTime;

public class BlogSpecifications {

    public static Specification<Blog> isPublished() {
        return (root, query, builder) -> builder.isTrue(root.get("isPublished"));
    }

    public static Specification<Blog> titleContains(String title) {
        return (root, query, builder) -> 
            builder.like(builder.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    public static Specification<Blog> hasUserId(Integer userId) {
        return (root, query, builder) -> builder.equal(root.get("userId"), userId);
    }

    public static Specification<Blog> hasTag(String tagName) {
        return (root, query, builder) -> {
            Join<Blog, Tag> tags = root.join("tags", JoinType.INNER);
            return builder.equal(builder.lower(tags.get("name")), tagName.toLowerCase());
        };
    }

    public static Specification<Blog> minReadCount(Integer minReadCount) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("readCount"), minReadCount);
    }

    public static Specification<Blog> publishedAfter(LocalDateTime after) {
        return (root, query, builder) -> builder.greaterThanOrEqualTo(root.get("publishedAt"), after);
    }

    public static Specification<Blog> publishedBefore(LocalDateTime before) {
        return (root, query, builder) -> builder.lessThanOrEqualTo(root.get("publishedAt"), before);
    }
}
