package com.the_meow.blog_service.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.service.UserBlogService;
import com.the_meow.blog_service.utils.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/")
public class UserBlogController {
    private UserBlogService service;

    @GetMapping("/{ownerId}/blogs/")
    public ResponseEntity<List<?>> getBlogs(
        @PathVariable Integer ownerId,
        @RequestHeader("Authorization") String authHeader
    ) {
        Optional<Integer> userId = Utils.getUserId(authHeader);
        List<?> blogs = service.getBlogs(ownerId, userId);
        return ResponseEntity.ok(blogs);
    }

    @GetMapping("/me/blogs/")
    public ResponseEntity<List<?>> getBlogs(
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        List<?> blogs = service.getBlogs(userId, Optional.ofNullable(userId));
        return ResponseEntity.ok(blogs);
    }
}
