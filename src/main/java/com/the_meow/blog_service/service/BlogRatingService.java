package com.the_meow.blog_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.repository.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BlogRatingService {
    private final BlogRatingRepository repo;
    private final BlogRepository blogRepository;

    public RatingResponse getRating(Integer blogId, Integer userId) {
        log.debug("Getting rating for blog {} by user {}", blogId, userId);
        Double avg = repo.findAverageRatingByBlogId(blogId);
        Integer total = repo.countByBlogId(blogId);

        Integer userRating = null;
        if (userId != null) {
            userRating = repo.findByBlogIdAndUserId(blogId, userId)
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
        Blog blog = blogRepository.findById(blogId.longValue())
            .orElseThrow(() -> {
                log.warn("Blog with id={} not found for rating submission", blogId);
                return new BlogNotFoundException(blogId);
            });

        boolean exists = repo.findByBlogIdAndUserId(blogId, userId).isPresent();
        if (exists) {
            log.warn("User {} already rated blog {}", userId, blogId);
            throw new AlreadyRatedException();
        }

        BlogRating rating = new BlogRating(blog, userId, ratingRequest.getRating().floatValue());
        repo.save(rating);
        log.info("Rating saved for blog {} by user {}", blogId, userId);
    }

    public void updateRating(Integer blogId, Integer userId, RatingRequest ratingRequest) {
        log.info("User {} updating rating for blog {}", userId, blogId);
        BlogRating rating = repo.findByBlogIdAndUserId(blogId, userId)
            .orElseThrow(() -> {
                log.warn("Rating not found for user {} and blog {}", userId, blogId);
                return new RatingNotFoundException();
            });

        rating.setRating(ratingRequest.getRating().floatValue());
        repo.save(rating);
        log.info("Rating updated for blog {} by user {}", blogId, userId);
    }

    public void deleteRating(Integer blogId, Integer userId) {
        log.info("User {} deleting rating for blog {}", userId, blogId);
        BlogRating.BlogRatingId id = new BlogRating.BlogRatingId(blogId, userId);

        if (!repo.existsById(id)) {
            log.warn("Rating not found for deletion: blog {}, user {}", blogId, userId);
            throw new RatingNotFoundException();
        }

        repo.deleteById(id);
        log.info("Rating deleted for blog {} by user {}", blogId, userId);
    }
}