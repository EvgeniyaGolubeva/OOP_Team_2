package com.the_meow.blog_service.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import com.the_meow.blog_service.model.Tag;

public interface TagRepository extends JpaRepository<Tag, Integer> {

    @Query("SELECT DISTINCT t.name FROM Tag t")
    List<String> findAllUniqueTagNames();

    @Query("SELECT DISTINCT t.name FROM Tag t WHERE t.blog.id = :blogId")
    List<String> findUniqueTagNamesByBlogId(@Param("blogId") Integer blogId);

    Optional<Tag> findByBlogIdAndName(Integer blogId, String name);

    @Query("SELECT DISTINCT t.name FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<String> fuzzySearch(@Param("keyword") String keyword);

    @Query(value = "SELECT DISTINCT name FROM tags WHERE name ~* :pattern", nativeQuery = true)
    List<String> regexSearch(@Param("pattern") String regex);

}
