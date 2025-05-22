package com.the_meow.blog_service.service;

import com.the_meow.blog_service.model.CommentRating;
import com.the_meow.blog_service.model.CommentRating.CommentRatingId;
import com.the_meow.blog_service.repository.CommentRatingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentRatingService {

    private final CommentRatingRepository commentRatingRepository;

    public CommentRating save(CommentRating commentRating) {
        return commentRatingRepository.save(commentRating);
    }

    public Optional<CommentRating> findByCommentIdAndUserId(Integer commentId, Integer userId) {
        return commentRatingRepository.findById(new CommentRatingId(commentId, userId));
    }

    public List<CommentRating> findAllByCommentId(Integer commentId) {
        return commentRatingRepository.findAll().stream()
                .filter(r -> r.getComment().getId().equals(commentId))
                .toList();
    }

    public void deleteByCommentIdAndUserId(Integer commentId, Integer userId) {
        commentRatingRepository.deleteById(new CommentRatingId(commentId, userId));
    }

    public Float getAverageRatingForComment(Integer commentId) {
        List<CommentRating> ratings = findAllByCommentId(commentId);
        if (ratings.isEmpty()) return 0f;

        float sum = 0f;
        for (CommentRating rating : ratings) {
            sum += rating.getRating();
        }
        return sum / ratings.size();
    }
}