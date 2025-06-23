package com.the_meow.blog_service.service;

import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.model.*;
import com.the_meow.blog_service.exception.*;
import com.the_meow.blog_service.repository.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollaboratorsService {
    private final BlogRepository blogRepo;

    public void addCollaborators(Integer blogId, CollaboratorsRequest request, Integer requestingUserId) {
        Blog blog = blogRepo.findById(blogId.longValue())
            .orElseThrow(() -> new BlogNotFoundException(blogId));
    
        if (!blog.getUserId().equals(requestingUserId)) {
            throw new UnauthorizedException();
        }
    
        for (Integer collaborator : request.getCollaborators()) {
            if (!blog.getCollaborators().contains(collaborator)) {
                blog.getCollaborators().add(collaborator);
            }
        }
    
        blogRepo.save(blog);
    }

    public void removeCollaborators(Integer blogId, CollaboratorsRequest request, Integer requestingUserId) {
        Blog blog = blogRepo.findById(blogId.longValue())
            .orElseThrow(() -> new BlogNotFoundException(blogId));
    
        if (!blog.getUserId().equals(requestingUserId)) {
            throw new UnauthorizedException();
        }
    
        blog.getCollaborators().removeAll(request.getCollaborators());
        blogRepo.save(blog);
    }
}
