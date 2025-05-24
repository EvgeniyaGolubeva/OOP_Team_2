package com.the_meow.blog_service.service;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.repository.*;
import com.the_meow.blog_service.utils.Utils;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
public class BlogService {
    private final BlogRepository repo;

    public BlogService(BlogRepository repo) {
        this.repo = repo;
    }

    public Page<BlogInfoPublic> getPublishedBlogs(BlogFilterRequest filter) {
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

        List<BlogInfoPublic> summaries = blogs.getContent().stream()
                .map(blog -> new BlogInfoPublic(blog))
                .collect(Collectors.toList());

        return new PageImpl<>(summaries, pageable, blogs.getTotalElements());
    }

    private Blog findBlogOwnedBy(Integer blogId, Integer userId) {
        Blog blog = repo.findById(blogId.longValue())
            .orElseThrow(() -> new BlogNotFoundException(blogId));
    
        if (!blog.getUserId().equals(userId)) {
            throw new ForbiddenBlogAccessException(blogId, userId);
        }

        return blog;
    }    

    public BlogInfoOwner createNewBlog(Integer userId, BlogCreateRequest request) throws IOException {
        String content = Utils.compressText(Optional.ofNullable(request.getContent()).orElse(""));

        Blog blog = Blog.builder()
            .title(request.getTitle())
            .userId(userId)
            .thumbnailUrl(request.getThumbnailUrl())
            .content(content)
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

        repo.save(blog);

        return new BlogInfoOwner(blog);
    }

    public BlogInfoOwner updateBlog(Integer blogId, Integer userId, BlogCreateRequest request) throws IOException {
        Blog blog = findBlogOwnedBy(blogId, userId);
    
        blog.setTitle(request.getTitle() != null ? request.getTitle() : blog.getTitle());
        blog.setThumbnailUrl(request.getThumbnailUrl() != null ? request.getThumbnailUrl() : blog.getThumbnailUrl());
        blog.setContent(request.getContent() != null ? Utils.compressText(request.getContent()) : blog.getContent());
        blog.setIsPublished(false);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);

        return new BlogInfoOwner(blog);
    }

    public void deleteBlog(Integer blogId, Integer userId) {
        Blog blog = findBlogOwnedBy(blogId, userId);
        repo.delete(blog);
    }

    public void publishBlog(Integer blogId, Integer userId) {
        Blog blog = findBlogOwnedBy(blogId, userId);

        blog.setIsPublished(true);
        blog.setPublishedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
    }

    public void unpublishBlog(Integer blogId, Integer userId) {
        Blog blog = findBlogOwnedBy(blogId, userId);

        blog.setIsPublished(false);
        blog.setPublishedAt(null);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
    }

    public void togglePublish(Integer blogId, Integer userId) {
        Blog blog = findBlogOwnedBy(blogId, userId);

        blog.setIsPublished(!blog.getIsPublished());
        blog.setPublishedAt(blog.getIsPublished() ? LocalDateTime.now() : null);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
    }
    
    public boolean getPublishStatus(Integer blogId, Integer userId) {
        Blog blog = findBlogOwnedBy(blogId, userId);
        return blog.getIsPublished();
    }

    public Object getBlog(Optional<Integer> userId, Integer blogId) {
        Blog blog = repo.findById(blogId.longValue())
            .orElseThrow(() -> new BlogNotFoundException(blogId));
        
        if (userId.isPresent() && blog.getUserId().equals(userId.get())) {
            return new BlogOwner(blog);
        }
    
        if (Boolean.TRUE.equals(blog.getIsPublished())) {
            return new BlogPublic(blog);
        }
    
        throw new ForbiddenBlogAccessException(blogId, userId.orElse(-1));
    }
}
