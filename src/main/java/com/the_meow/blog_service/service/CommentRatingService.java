package com.the_meow.blog_service.service;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentRatingService {
    private final BlogRepository blogRepo;
    private final CommentRatingRepository repo;
    private final CommentRepository commentRepo;

    private Comment getValidatedComment(Integer blogId, Integer commentId) {
        Blog blog = blogRepo.findById(blogId.longValue())
                .orElseThrow(() -> new BlogNotFoundException(blogId));

        Comment comment = commentRepo.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException());

        if (!comment.getBlog().getId().equals(blog.getId())) {
            throw new CommentNotFoundException();
        }

        return comment;
    }

    public RatingResponse getRatingsByCommentId(Integer blogId, Integer commentId, Integer userId) {
        Comment comment = getValidatedComment(blogId, commentId);

        Double avg = repo.getAverageRatingByCommentId(comment.getId());
        Integer total = repo.countByCommentId(comment.getId());

        Double userRating = null;
        if (userId != null) {
            userRating = repo.findByCommentIdAndUserId(comment.getId(), userId)
                .map(CommentRating::getRating)
                .orElse(null);
        }

        return new RatingResponse(
            avg != null ? avg : 0.0,
            total != null ? total : 0,
            userRating
        );
    }

    public void submitRating(Integer blogId, Integer commentId, Integer userId, RatingRequest ratingRequest) {
        Comment comment = getValidatedComment(blogId, commentId);
        CommentRating.CommentRatingId id = new CommentRating.CommentRatingId(comment.getId(), userId);

        if (repo.existsById(id)) {
            throw new AlreadyRatedException();
        }

        CommentRating rating = new CommentRating(comment, userId, ratingRequest.getRating());
        repo.save(rating);
    }


    public void updateRating(Integer blogId, Integer commentId, Integer userId, RatingRequest ratingRequest) {
        Comment comment = getValidatedComment(blogId, commentId);
        CommentRating.CommentRatingId id = new CommentRating.CommentRatingId(comment.getId(), userId);

        CommentRating rating = repo.findById(id)
            .orElseThrow(() -> {
                return new RatingNotFoundException();
            });

        rating.setRating(ratingRequest.getRating());
        repo.save(rating);
    }

    public void deleteRating(Integer blogId, Integer commentId, Integer userId) {
        Comment comment = getValidatedComment(blogId, commentId);
        CommentRating.CommentRatingId id = new CommentRating.CommentRatingId(comment.getId(), userId);

        if (!repo.existsById(id)) {
            throw new RatingNotFoundException();
        }

        repo.deleteById(id);
    }
}
