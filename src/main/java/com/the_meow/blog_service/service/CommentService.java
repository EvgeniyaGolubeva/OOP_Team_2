package com.the_meow.blog_service.service;

import com.the_meow.blog_service.dto.CommentCreate;
import com.the_meow.blog_service.dto.CommentResponse;
import com.the_meow.blog_service.exception.BlogNotFoundException;
import com.the_meow.blog_service.exception.CommentNotFoundException;
import com.the_meow.blog_service.exception.UnauthorizedException;
import com.the_meow.blog_service.model.Blog;
import com.the_meow.blog_service.model.Comment;
import com.the_meow.blog_service.repository.BlogRepository;
import com.the_meow.blog_service.repository.CommentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final BlogRepository blogRepository;

    public List<CommentResponse> getCommentsByBlogId(Integer blogId) {
        List<Comment> comments = commentRepository.findByBlogId(blogId);
        return comments.stream()
                .map(CommentResponse::new)
                .collect(Collectors.toList());
    }

    public CommentResponse createComment(Integer blogId, Integer userId, CommentCreate commentCreate) {
        Blog blog = blogRepository.findById(blogId.longValue())
                .orElseThrow(() -> new BlogNotFoundException(blogId));

        Comment comment = Comment.builder()
                .blog(blog)
                .userId(userId)
                .content(commentCreate.getContent())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Comment saved = commentRepository.save(comment);
        return new CommentResponse(saved);
    }

    public CommentResponse getCommentById(Integer blogId, Integer commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getBlog().getId().equals(blogId)) {
            throw new CommentNotFoundException();
        }

        return new CommentResponse(comment);
    }

    public CommentResponse updateComment(Integer blogId, Integer commentId, Integer userId, CommentCreate commentUpdate) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getBlog().getId().equals(blogId)) {
            throw new CommentNotFoundException();
        }

        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedException();
        }

        comment.setContent(commentUpdate.getContent());
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);
        return new CommentResponse(updated);
    }

    public void deleteComment(Integer blogId, Integer commentId, Integer userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.getBlog().getId().equals(blogId)) {
            throw new CommentNotFoundException();
        }

        if (!comment.getUserId().equals(userId)) {
            throw new UnauthorizedException();
        }

        commentRepository.delete(comment);
    }
}
