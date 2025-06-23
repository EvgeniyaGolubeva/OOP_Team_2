package com.the_meow.blog_service.repository;

import com.the_meow.blog_service.model.CommentRating;
import com.the_meow.blog_service.model.CommentRating.CommentRatingId;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRatingRepository extends JpaRepository<CommentRating, CommentRatingId> {
    @Query("SELECT AVG(r.rating) FROM CommentRating r WHERE r.comment.id = :commentId")
    Double getAverageRatingByCommentId(@Param("commentId") Integer commentId);

    @Query("SELECT COUNT(r) FROM CommentRating r WHERE r.comment.id = :commentId")
    Integer countByCommentId(@Param("commentId") Integer commentId);

    Optional<CommentRating> findByCommentIdAndUserId(Integer commentId, Integer userId);
}