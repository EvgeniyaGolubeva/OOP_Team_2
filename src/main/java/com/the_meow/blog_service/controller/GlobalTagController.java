package com.the_meow.blog_service.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.the_meow.blog_service.service.TagService;

@RestController
@RequestMapping("/api/v1/tags")
public class GlobalTagController {

    private final TagService service;

    public GlobalTagController(TagService service) {
        this.service = service;
    }

    @GetMapping
    public List<String> getTags(
        @RequestParam(required = false) String name,
        @RequestParam(defaultValue = "fuzzy") String match
    ) {
        if (name != null && !name.isBlank()) {
            return service.searchTags(name, match);
        } else {
            return service.getAllUniqueTags();
        }
    }
}
