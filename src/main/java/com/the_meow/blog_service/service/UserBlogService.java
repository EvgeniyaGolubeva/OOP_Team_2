package com.the_meow.blog_service.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.the_meow.blog_service.dto.BlogInfoOwner;
import com.the_meow.blog_service.dto.BlogInfoPublic;
import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.repository.BlogRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserBlogService {
    private BlogRepository repo;

        public List<?> getBlogs(Integer ownerId, Optional<Integer> viewerId) {
        log.info("Fetching blogs for ownerId={} requested by user={}", ownerId, viewerId.orElse(null));

        List<Blog> blogs = repo.findAllByUserId(ownerId);

        if (viewerId.isPresent() && !viewerId.get().equals(ownerId)) {
            List<Blog> collabBlogs = repo.findAllByCollaboratorsContains(viewerId.get());

            Set<Blog> combined = new HashSet<>(blogs);
            combined.addAll(collabBlogs);
            blogs = new ArrayList<>(combined);
        }

        if (viewerId.isPresent() && viewerId.get().equals(ownerId)) {
            return blogs.stream()
                    .map(BlogInfoOwner::new)
                    .collect(Collectors.toList());
        }

        return blogs.stream()
                .filter(Blog::getIsPublished)
                .map(BlogInfoPublic::new)
                .collect(Collectors.toList());
    }
}
