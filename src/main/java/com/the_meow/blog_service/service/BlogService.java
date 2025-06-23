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

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogService {
    private final BlogRepository repo;

    public Page<BlogInfoPublic> getPublishedBlogs(BlogFilterRequest filter) {
        log.info("Fetching published blogs with filter: {}", filter);
        Specification<Blog> spec = Specification.where(BlogSpecifications.isPublished());

        if (filter.getTitle() != null && !filter.getTitle().isEmpty()) {
            spec = spec.and(BlogSpecifications.titleContains(filter.getTitle()));
            log.debug("Filtering by title containing '{}'", filter.getTitle());
        }

        if (filter.getUserId() != null) {
            spec = spec.and(BlogSpecifications.hasUserId(filter.getUserId()));
            log.debug("Filtering by userId={}", filter.getUserId());
        }

        if (filter.getTag() != null && !filter.getTag().isEmpty()) {
            spec = spec.and(BlogSpecifications.hasTag(filter.getTag()));
            log.debug("Filtering by tag '{}'", filter.getTag());
        }

        if (filter.getMinReadCount() != null) {
            spec = spec.and(BlogSpecifications.minReadCount(filter.getMinReadCount()));
            log.debug("Filtering by minReadCount={}", filter.getMinReadCount());
        }

        if (filter.getPublishedAfter() != null) {
            spec = spec.and(BlogSpecifications.publishedAfter(filter.getPublishedAfter()));
            log.debug("Filtering by publishedAfter={}", filter.getPublishedAfter());
        }

        if (filter.getPublishedBefore() != null) {
            spec = spec.and(BlogSpecifications.publishedBefore(filter.getPublishedBefore()));
            log.debug("Filtering by publishedBefore={}", filter.getPublishedBefore());
        }

        Pageable pageable = PageRequest.of(filter.getPage(), filter.getSize());
        Page<Blog> blogs = repo.findAll(spec, pageable);
        log.info("Found {} blogs matching filter", blogs.getTotalElements());

        List<BlogInfoPublic> summaries = blogs.getContent().stream()
                .map(blog -> new BlogInfoPublic(blog))
                .collect(Collectors.toList());

        return new PageImpl<>(summaries, pageable, blogs.getTotalElements());
    }

    private Blog findBlogOwnedBy(Integer blogId, Integer userId) {
        log.debug("Finding blog with id={} owned by userId={}", blogId, userId);
        Blog blog = repo.findById(blogId.longValue())
            .orElseThrow(() -> {
                log.warn("Blog with id={} not found", blogId);
                return new BlogNotFoundException(blogId);
            });

        if (!blog.getUserId().equals(userId)) {
            log.warn("User {} forbidden to access blog {}", userId, blogId);
            throw new ForbiddenBlogAccessException(blogId, userId);
        }

        return blog;
    }

    public BlogInfoOwner createNewBlog(Integer userId, BlogCreateRequest request) throws IOException {
        log.info("User {} creating new blog with title '{}'", userId, request.getTitle());
        boolean titleExists = repo.existsByTitleAndUserId(request.getTitle(), userId);
        if (titleExists) {
            log.warn("Duplicate blog title '{}' for user {}", request.getTitle(), userId);
            throw new DuplicateTitleException(request.getTitle(), userId);
        }

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
            log.debug("Setting {} tags for blog", tagEntities.size());
        }

        repo.save(blog);
        log.info("Blog '{}' created with id={}", blog.getTitle(), blog.getId());

        return new BlogInfoOwner(blog);
    }

    public BlogInfoOwner updateBlog(Integer blogId, Integer userId, BlogCreateRequest request) throws IOException {
        log.info("User {} attempting to update blog {}", userId, blogId);
    
        Blog blog = repo.findById(blogId.longValue())
            .orElseThrow(() -> new BlogNotFoundException(blogId));
    
        boolean isOwner = blog.getUserId().equals(userId);
        boolean isCollaborator = blog.getCollaborators() != null && blog.getCollaborators().contains(userId);
    
        if (!isOwner && !isCollaborator) {
            log.warn("User {} not authorized to update blog {}", userId, blogId);
            throw new ForbiddenException("You are not allowed to update this blog");
        }
    
        if (!isOwner && blog.getIsPublished() != null && blog.getIsPublished()) {
            log.warn("Collaborator {} cannot edit published blog {}", userId, blogId);
            throw new ForbiddenException("Collaborators cannot edit published blogs");
        }
    
        if (request.getTitle() != null) blog.setTitle(request.getTitle());
        if (request.getThumbnailUrl() != null) blog.setThumbnailUrl(request.getThumbnailUrl());
        if (request.getContent() != null) blog.setContent(Utils.compressText(request.getContent()));
    
        blog.setIsPublished(false);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
        log.info("Blog {} updated by user {}", blogId, userId);
    
        return new BlogInfoOwner(blog);
    }

    public void deleteBlog(Integer blogId, Integer userId) {
        log.info("User {} deleting blog {}", userId, blogId);
        Blog blog = findBlogOwnedBy(blogId, userId);
        repo.delete(blog);
        log.info("Blog {} deleted", blogId);
    }

    public void publishBlog(Integer blogId, Integer userId) {
        log.info("User {} publishing blog {}", userId, blogId);
        Blog blog = findBlogOwnedBy(blogId, userId);

        blog.setIsPublished(true);
        blog.setPublishedAt(LocalDateTime.now());
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
        log.info("Blog {} published", blogId);
    }

    public void unpublishBlog(Integer blogId, Integer userId) {
        log.info("User {} unpublishing blog {}", userId, blogId);
        Blog blog = findBlogOwnedBy(blogId, userId);

        blog.setIsPublished(false);
        blog.setPublishedAt(null);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
        log.info("Blog {} unpublished", blogId);
    }

    public void togglePublish(Integer blogId, Integer userId) {
        log.info("User {} toggling publish state for blog {}", userId, blogId);
        Blog blog = findBlogOwnedBy(blogId, userId);

        blog.setIsPublished(!blog.getIsPublished());
        blog.setPublishedAt(blog.getIsPublished() ? LocalDateTime.now() : null);
        blog.setUpdatedAt(LocalDateTime.now());
    
        repo.save(blog);
        log.info("Blog {} publish state toggled to {}", blogId, blog.getIsPublished());
    }
    
    public boolean getPublishStatus(Integer blogId, Integer userId) {
        log.debug("User {} requesting publish status for blog {}", userId, blogId);
        Blog blog = findBlogOwnedBy(blogId, userId);
        return blog.getIsPublished();
    }

    public Object getBlog(Optional<Integer> userId, Integer blogId) {
        log.debug("Fetching blog {} for user {}", blogId, userId.orElse(null));
        Blog blog = repo.findById(blogId.longValue())
            .orElseThrow(() -> {
                log.warn("Blog with id={} not found", blogId);
                return new BlogNotFoundException(blogId);
            });
    
        if (userId.isPresent()) {
            Integer uid = userId.get();
    
            if (blog.getUserId().equals(uid)) {
                log.debug("Returning owner view of blog {}", blogId);
                return new BlogOwner(blog);
            }
    
            if (blog.getCollaborators() != null && blog.getCollaborators().contains(uid)) {
                if (Boolean.FALSE.equals(blog.getIsPublished())) {
                    log.debug("Returning collaborator view of blog {}", blogId);
                    return new BlogOwner(blog);
                }
            }
        }
    
        if (Boolean.TRUE.equals(blog.getIsPublished())) {
            log.debug("Returning public view of blog {}", blogId);
            return new BlogPublic(blog);
        }
    
        log.warn("User {} forbidden to access blog {}", userId.orElse(-1), blogId);
        throw new ForbiddenBlogAccessException(blogId, userId.orElse(-1));
    }
}
