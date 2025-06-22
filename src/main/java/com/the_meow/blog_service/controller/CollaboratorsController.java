package com.the_meow.blog_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.the_meow.blog_service.dto.CollaboratorsRequest;
import com.the_meow.blog_service.exception.BadAuthTokenException;
import com.the_meow.blog_service.service.CollaboratorsService;
import com.the_meow.blog_service.utils.Utils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/blogs/{blogId}/collaborators")
public class CollaboratorsController {
    private CollaboratorsService service;
    
    @GetMapping
    public ResponseEntity<Void> addCollaborator(
        @PathVariable Integer blogId,
        @Valid @RequestBody CollaboratorsRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.addCollaborators(blogId, request, userId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> removeCollaborator(
        @PathVariable Integer blogId,
        @Valid @RequestBody CollaboratorsRequest request,
        @RequestHeader("Authorization") String authHeader
    ) {
        Integer userId = Utils.getUserId(authHeader).orElseThrow(BadAuthTokenException::new);
        service.removeCollaborators(blogId, request, userId);
        return ResponseEntity.noContent().build();
    }
}
