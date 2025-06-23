package com.the_meow.blog_service.repository;

import com.the_meow.blog_service.model.BlogRating;
import com.the_meow.blog_service.model.BlogRating.BlogRatingId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface BlogRatingRepository extends JpaRepository<BlogRating, BlogRatingId> {
    @Query("SELECT AVG(r.rating) FROM BlogRating r WHERE r.blog.id = :blogId")
    Double findAverageRatingByBlogId(@Param("blogId") Integer blogId);

    @Query("SELECT COUNT(r) FROM BlogRating r WHERE r.blog.id = :blogId")
    Integer countByBlogId(@Param("blogId") Integer blogId);

    Optional<BlogRating> findByBlogIdAndUserId(Integer blogId, Integer userId);
}
