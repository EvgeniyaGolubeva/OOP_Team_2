package com.the_meow.blog_service.service;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.repository.*;

import org.apache.coyote.BadRequestException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlogService {
    private final BlogRepository repo;

    public BlogService(BlogRepository repo) {
        this.repo = repo;
    }

    public Page<BlogSummaryResponse> getPublishedBlogs(BlogFilterRequest filter) {
        Specification<Blog> spec = Specification.where(BlogSpecifications.isPublished());

        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            spec = spec.and(BlogSpecifications.titleContains(filter.getTitle()));
        }
        if (filter.getUserId() != null) {
            spec = spec.and(BlogSpecifications.hasUserId(filter.getUserId()));
        }
        if (filter.getTag() != null && !filter.getTag().isEmpty()) {
            spec = spec.and(BlogSpecifications.hasTag(filter.getTag()));
        }
        if (filter.getMinReadCount() != null) {
            spec = spec.and(BlogSpecifications.minReadCount(filter.getMinReadCount()));
        }
        if (filter.getPublishedAfter() != null) {
            spec = spec.and(BlogSpecifications.publishedAfter(filter.getPublishedAfter()));
        }
        if (filter.getPublishedBefore() != null) {
            spec = spec.and(BlogSpecifications.publishedBefore(filter.getPublishedBefore()));
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Blog> blogs = repo.findAll(spec, pageable);

        List<BlogSummaryResponse> summaries = blogs.getContent().stream()
                .map(blog -> {
                    double avgRating = blog.getRatings() == null || blog.getRatings().isEmpty()
                            ? 0.0
                            : blog.getRatings().stream()
                                .mapToDouble(BlogRating::getRating)
                                .average()
                                .orElse(0.0);

                    return new BlogSummaryResponse(
                            blog.getTitle(),
                            blog.getUserId(),
                            blog.getThumbnailUrl(),
                            blog.getPublishedAt(),
                            blog.getReadCount(),
                            avgRating
                    );
                })
                .collect(Collectors.toList());

        return new PageImpl<>(summaries, pageable, blogs.getTotalElements());
    }


    public BlogCreateResponse createNewBlog(BlogCreateRequest request, Integer user_id) {
        Blog blog = Blog.builder()
            .title(request.getTitle())
            .userId(user_id)
            .thumbnailUrl(request.getThumbnailUrl())
            .content(request.getContent())
            .isPublished(false)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .publishedAt(null)
            .readCount(0)
            .build();

        if (request.getTags() != null && !request.getTags().isEmpty()) {
            List<Tag> tagEntities = request.getTags().stream()
                .map(name -> {
                    Tag tag = new Tag();
                    tag.setName(name);
                    tag.setBlog(blog);
                    return tag;
                })
                .toList();
            blog.setTags(tagEntities);
        }

        try {
            repo.save(blog);
            return new BlogCreateResponse(
                blog.getId(),
                blog.getTitle(),
                blog.getThumbnailUrl(),
                request.getTags()
            );        
        }
        catch (Exception e) {
            return null;
        }
    }

    public BlogCreateResponse updateBlog(Integer blogId, Integer userId, BlogCreateRequest request) throws BadRequestException {
        Blog blog = repo.findById(blogId.longValue()).orElse(null);

        if (blog == null) {
            throw new BadRequestException("No blog found");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new BadRequestException("Not yours, sorry :(");
        }
    
        // Update fields
        blog.setTitle(request.getTitle() != null ? request.getTitle() : blog.getTitle());
        blog.setThumbnailUrl(request.getThumbnailUrl() != null ? request.getThumbnailUrl() : blog.getThumbnailUrl());
        blog.setContent(request.getContent() != null ? request.getContent() : blog.getContent());
        blog.setIsPublished(false);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);

        return new BlogCreateResponse(
            blog.getId(),
            blog.getTitle(),
            blog.getThumbnailUrl(),
            blog.getTags().stream()
                .map(tag -> {
                    return tag.getName();
                })
                .toList()
        );
    }

    public void deleteBlog(Integer blogId, Integer userId) throws BadRequestException {
        Blog blog = repo.findById(blogId.longValue()).orElse(null);

        if (blog == null) {
            throw new BadRequestException("No blog found");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new BadRequestException("Not yours, sorry :(");
        }
    
        repo.delete(blog);
    }

    public void togglePublish(Integer blogId, Integer userId) throws BadRequestException {
        Blog blog = repo.findById(blogId.longValue()).orElse(null);

        if (blog == null) {
            throw new BadRequestException("No blog found");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new BadRequestException("Not yours, sorry :(");
        }
    
        blog.setIsPublished(!blog.getIsPublished());
        blog.setPublishedAt(blog.getIsPublished() ? LocalDateTime.now() : null);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
    }
    
    public boolean getPublishStatus(Integer blogId, Integer userId) throws BadRequestException {
        Blog blog = repo.findById(blogId.longValue()).orElse(null);

        if (blog == null) {
            throw new BadRequestException("No blog found");
        }
        
        if (!blog.getUserId().equals(userId)) {
            throw new BadRequestException("Not yours, sorry :(");
        }
    
        return blog.getIsPublished();
    }
}
