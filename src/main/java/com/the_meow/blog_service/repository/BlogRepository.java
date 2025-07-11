package com.the_meow.blog_service.repository;

import com.the_meow.blog_service.model.Blog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long>, JpaSpecificationExecutor<Blog> {
    boolean existsByTitleAndUserId(String title, Integer userId);
    List<Blog> findAllByCollaboratorsContains(Integer userId);
    List<Blog> findAllByUserId(Integer userId);
}
