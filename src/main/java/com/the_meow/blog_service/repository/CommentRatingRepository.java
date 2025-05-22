package com.the_meow.blog_service.repository;

import com.the_meow.blog_service.model.CommentRating;
import com.the_meow.blog_service.model.CommentRating.CommentRatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRatingRepository extends JpaRepository<CommentRating, CommentRatingId> {}