package com.the_meow.blog_service.controller;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.service.BlogService;
import com.the_meow.blog_service.utils.CompressionUtils;
import com.the_meow.blog_service.utils.MarkdownUtils;
import com.the_meow.blog_service.utils.Utils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blogs")
public class BlogController {
    private final BlogService service;

    @GetMapping
    public Page<BlogInfoPublic> getAllPublishedBlogs(@ModelAttribute BlogFilterRequest filter) {
        return service.getPublishedBlogs(filter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Optional<Integer> userId = Utils.getUserId(authHeader);
        Object blog = service.getBlog(userId, id);
        return ResponseEntity.ok(blog);
    }

    @GetMapping("/{blogId}/html")
    public ResponseEntity<String> getBlogHTML(
        @PathVariable Integer blogId,
        @RequestHeader("Authorization") String authHeader
    ) {
        Optional<Integer> userId = Utils.getUserId(authHeader);
        Object blog = service.getBlog(userId, blogId);

        String html = null;
        if (blog instanceof BlogOwner b) {
            html = """
                <html>
                    <head><title>%s</title></head>
                    <body>
                        <h1>%s</h1>
                        <p><em>Published: %s</em></p>
                        <div>%s</div>
                    </body>
                </html>
            """.formatted(
                b.getTitle(),
                b.getTitle(),
                b.getIsPublished() != null ? b.getPublishedAt().toString() : "Unpublished",
                MarkdownUtils.markdownToHtml(
                    CompressionUtils.decompressText(
                        b.getContent()
                    )
                )
            );
        }
        else if (blog instanceof BlogPublic b) {
            html = """
                <html>
                    <head><title>%s</title></head>
                    <body>
                        <h1>%s</h1>
                        <p><em>Published: %s</em></p>
                        <div>%s</div>
                    </body>
                </html>
            """.formatted(
                b.getTitle(),
                b.getTitle(),
                b.getPublishedAt().toString(),
                MarkdownUtils.markdownToHtml(
                    CompressionUtils.decompressText(
                        b.getContent()
                    )
                )
            );
        }

        return ResponseEntity
        .ok()
        .header("Content-Type", MediaType.TEXT_HTML_VALUE)
        .body(html);
    }

    @PostMapping
    public ResponseEntity<BlogInfoOwner> createBlog(
        @Valid @RequestBody BlogCreateRequest request,
        @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        BlogInfoOwner savedBlog = service.createNewBlog(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBlog);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<BlogInfoOwner> updateBlog(
            @PathVariable Integer id,
            @RequestBody BlogCreateRequest request,
            @RequestHeader("Authorization") String authHeader
    ) throws IOException {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        BlogInfoOwner updatedBlog = service.updateBlog(id, userId, request);
        return ResponseEntity.ok(updatedBlog);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.deleteBlog(id, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/publish")
    public ResponseEntity<Map<String, Boolean>> getPublishStatus(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        boolean isPublished = service.getPublishStatus(id, userId);
        return ResponseEntity.ok(Map.of("isPublished", isPublished));
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<Void> publishBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.publishBlog(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<Void> togglePublish(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.togglePublish(id, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/publish")
    public ResponseEntity<Void> unpublishBlog(
        @PathVariable Integer id,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.unpublishBlog(id, userId);
        return ResponseEntity.noContent().build();
    }
}
