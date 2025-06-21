package com.the_meow.blog_service.service;

import com.the_meow.blog_service.model.Comment;
import com.the_meow.blog_service.repository.CommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;

    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByBlogId(Integer blogId) {
        return commentRepository.findAll()
                .stream()
                .filter(c -> c.getBlog().getId().equals(blogId))
                .toList();
    }

    public void deleteById(Integer id) {
        commentRepository.deleteById(id);
    }
}
