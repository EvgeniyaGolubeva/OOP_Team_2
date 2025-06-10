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

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class BlogService {
    private final BlogRepository repo;
    private final BlogRatingRepository blogRatingRepository;

    public BlogService(BlogRepository repo, BlogRatingRepository blogRatingRepository) {
        this.repo = repo;
        this.blogRatingRepository = blogRatingRepository;
    }

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
        log.info("User {} updating blog {}", userId, blogId);
        Blog blog = findBlogOwnedBy(blogId, userId);

        blog.setTitle(request.getTitle() != null ? request.getTitle() : blog.getTitle());
        blog.setThumbnailUrl(request.getThumbnailUrl() != null ? request.getThumbnailUrl() : blog.getThumbnailUrl());
        blog.setContent(request.getContent() != null ? Utils.compressText(request.getContent()) : blog.getContent());
        blog.setIsPublished(false);
        blog.setUpdatedAt(LocalDateTime.now());

        repo.save(blog);
        log.info("Blog {} updated", blogId);

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
        
        if (userId.isPresent() && blog.getUserId().equals(userId.get())) {
            log.debug("Returning owner view of blog {}", blogId);
            return new BlogOwner(blog);
        }
    
        if (Boolean.TRUE.equals(blog.getIsPublished())) {
            log.debug("Returning public view of blog {}", blogId);
            return new BlogPublic(blog);
        }
    
        log.warn("User {} forbidden to access blog {}", userId.orElse(-1), blogId);
        throw new ForbiddenBlogAccessException(blogId, userId.orElse(-1));
    }

    public RatingResponse getRating(Integer blogId, Integer userId) {
        log.debug("Getting rating for blog {} by user {}", blogId, userId);
        Double avg = blogRatingRepository.findAverageRatingByBlogId(blogId);
        Integer total = blogRatingRepository.countByBlogId(blogId);
    
        Integer userRating = null;
        if (userId != null) {
            userRating = blogRatingRepository.findByBlogIdAndUserId(blogId, userId)
                            .map(BlogRating::getRating)
                            .map(Float::intValue)
                            .orElse(null);
        }
    
        log.info("Rating for blog {}: avg={}, total={}, userRating={}", blogId, avg, total, userRating);
        return new RatingResponse(
            avg != null ? avg : 0.0,
            total != null ? total : 0,
            userRating
        );
    }

    public void submitRating(Integer blogId, Integer userId, RatingRequest ratingRequest) {
        log.info("User {} submitting rating {} for blog {}", userId, ratingRequest.getRating(), blogId);
        Blog blog = repo.findById(blogId.longValue())
            .orElseThrow(() -> {
                log.warn("Blog with id={} not found for rating submission", blogId);
                return new BlogNotFoundException(blogId);
            });
            
        boolean exists = blogRatingRepository.findByBlogIdAndUserId(blogId, userId).isPresent();
        if (exists) {
            log.warn("User {} already rated blog {}", userId, blogId);
            throw new AlreadyRatedException();
        }
    
        BlogRating rating = new BlogRating(blog, userId, ratingRequest.getRating().floatValue());
        blogRatingRepository.save(rating);
        log.info("Rating saved for blog {} by user {}", blogId, userId);
    }    

    public void updateRating(Integer blogId, Integer userId, RatingRequest ratingRequest) {
        log.info("User {} updating rating for blog {}", userId, blogId);
        BlogRating rating = blogRatingRepository.findByBlogIdAndUserId(blogId, userId)
            .orElseThrow(() -> {
                log.warn("Rating not found for user {} and blog {}", userId, blogId);
                return new RatingNotFoundException();
            });
    
        rating.setRating(ratingRequest.getRating().floatValue());
        blogRatingRepository.save(rating);
        log.info("Rating updated for blog {} by user {}", blogId, userId);
    }    

    public void deleteRating(Integer blogId, Integer userId) {
        log.info("User {} deleting rating for blog {}", userId, blogId);
        BlogRating.BlogRatingId id = new BlogRating.BlogRatingId(blogId, userId);

        if (!blogRatingRepository.existsById(id)) {
            log.warn("Rating not found for deletion: blog {}, user {}", blogId, userId);
            throw new RatingNotFoundException();
        }

        blogRatingRepository.deleteById(id);
        log.info("Rating deleted for blog {} by user {}", blogId, userId);
    }
}
