package com.the_meow.blog_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.the_meow.blog_service.controller.BlogController;
import com.the_meow.blog_service.dto.*;
import com.the_meow.blog_service.service.BlogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BlogServiceApplicationTests {

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Mock
    private BlogService blogService;

    private static final String AUTH = "Bearer valid.jwt";

    @BeforeEach
    void setUp() {
        BlogController controller = new BlogController(blogService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    /* -------------------------------------------------
     * CREATE
     * ------------------------------------------------- */

    @Test
    @DisplayName("POST /blogs - happy path")
    void testCreateBlogHappyPath() throws Exception {
        BlogInfoOwner saved = new BlogInfoOwner(
                123L, "My Title", null, "Body", List.of(), OffsetDateTime.now(), null, false);

        when(blogService.createNewBlog(eq(3), any(BlogCreateRequest.class))).thenReturn(saved);

        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("My Title");
        req.setContent("Body");

        mockMvc.perform(post("/api/v1/blogs")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(123));
    }

    @Test
    void testCreateBlogMissingTitle() throws Exception {
        BlogCreateRequest req = new BlogCreateRequest();
        req.setContent("Body");

        mockMvc.perform(post("/api/v1/blogs")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateBlogDuplicateTitle() throws Exception {
        doThrow(new RuntimeException("DUPLICATE_TITLE"))
                .when(blogService).createNewBlog(anyInt(), any());

        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("Existing");
        req.setContent("Body");

        mockMvc.perform(post("/api/v1/blogs")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isConflict());
    }

    @Test
    void testCreateBlogUnauthorized() throws Exception {
        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("T");
        req.setContent("C");

        mockMvc.perform(post("/api/v1/blogs")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isUnauthorized());
    }

    /* -------------------------------------------------
     * UPDATE
     * ------------------------------------------------- */

    @Test
    @DisplayName("PATCH /blogs/{id} - happy path")
    void testUpdateBlogHappyPath() throws Exception {
        BlogInfoOwner updated = new BlogInfoOwner(
                42L, "Updated", null, "New Body", List.of("x"), OffsetDateTime.now(), OffsetDateTime.now(), false);

        when(blogService.updateBlog(eq(42), eq(3), any())).thenReturn(updated);

        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("Updated");
        req.setContent("New Body");
        req.setTags(List.of("x"));

        mockMvc.perform(patch("/api/v1/blogs/42")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.title").value("Updated"))
               .andExpect(jsonPath("$.updatedAt").exists());
    }

    @Test
    void testUpdateBlogAccessDenied() throws Exception {
        doThrow(new RuntimeException("ACCESS_DENIED"))
                .when(blogService).updateBlog(anyInt(), anyInt(), any());

        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("T");
        req.setContent("C");

        mockMvc.perform(patch("/api/v1/blogs/42")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateBlogNotFound() throws Exception {
        doThrow(new RuntimeException("BLOG_NOT_FOUND"))
                .when(blogService).updateBlog(anyInt(), anyInt(), any());

        BlogCreateRequest req = new BlogCreateRequest();
        req.setTitle("T");
        req.setContent("C");

        mockMvc.perform(patch("/api/v1/blogs/999")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isNotFound());
    }

    /* -------------------------------------------------
     * DELETE
     * ------------------------------------------------- */

    @Test
    @DisplayName("DELETE /blogs/{id} - happy path")
    void testDeleteBlogHappyPath() throws Exception {
        doNothing().when(blogService).deleteBlog(42, 3);

        mockMvc.perform(delete("/api/v1/blogs/42")
                       .header("Authorization", AUTH))
               .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteBlogAccessDenied() throws Exception {
        doThrow(new RuntimeException("ACCESS_DENIED"))
                .when(blogService).deleteBlog(anyInt(), anyInt());

        mockMvc.perform(delete("/api/v1/blogs/42")
                       .header("Authorization", AUTH))
               .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteBlogNotFound() throws Exception {
        doThrow(new RuntimeException("BLOG_NOT_FOUND"))
                .when(blogService).deleteBlog(anyInt(), anyInt());

        mockMvc.perform(delete("/api/v1/blogs/999")
                       .header("Authorization", AUTH))
               .andExpect(status().isNotFound());
    }

    /* -------------------------------------------------
     * LIST + GET
     * ------------------------------------------------- */

    @Test
    void testListBlogsHappy() throws Exception {
        Page<BlogInfoPublic> page = new PageImpl<>(List.of());
        when(blogService.getPublishedBlogs(any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/blogs?page=0&size=10"))
               .andExpect(status().isOk());
    }

    @Test
    void testGetPublishedBlog() throws Exception {
        BlogInfoPublic pub = new BlogInfoPublic(
                42L, "Title", null, "Excerpt", OffsetDateTime.now(), true);

        when(blogService.getBlog(any(), eq(42))).thenReturn(pub);

        mockMvc.perform(get("/api/v1/blogs/42"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.id").value(42));
    }

    @Test
    void testGetUnpublishedBlogByOwner() throws Exception {
        BlogInfoOwner draft = new BlogInfoOwner(
                43L, "Draft", null, "Body", List.of(), OffsetDateTime.now(), null, false);

        when(blogService.getBlog(any(), eq(43))).thenReturn(draft);

        mockMvc.perform(get("/api/v1/blogs/43")
                       .header("Authorization", AUTH))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.published").doesNotExist()); // Field not in owner model
    }

    @Test
    void testGetBlogNotFound() throws Exception {
        doThrow(new RuntimeException("BLOG_NOT_FOUND"))
                .when(blogService).getBlog(any(), eq(999));

        mockMvc.perform(get("/api/v1/blogs/999"))
               .andExpect(status().isNotFound());
    }

    /* -------------------------------------------------
     * PUBLISH STATUS
     * ------------------------------------------------- */

    @Test
    void testGetPublishStatus() throws Exception {
        when(blogService.getPublishStatus(42, 3)).thenReturn(false);

        mockMvc.perform(get("/api/v1/blogs/42/publish")
                       .header("Authorization", AUTH))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.isPublished").value(false));
    }

    @Test
    void testPublishBlogHappy() throws Exception {
        doNothing().when(blogService).publishBlog(42, 3);

        mockMvc.perform(post("/api/v1/blogs/42/publish")
                       .header("Authorization", AUTH))
               .andExpect(status().isNoContent());
    }

    @Test
    void testUnpublishBlogHappy() throws Exception {
        doNothing().when(blogService).unpublishBlog(42, 3);

        mockMvc.perform(delete("/api/v1/blogs/42/publish")
                       .header("Authorization", AUTH))
               .andExpect(status().isNoContent());
    }

    @Test
    void testTogglePublishHappy() throws Exception {
        doNothing().when(blogService).togglePublish(42, 3);

        mockMvc.perform(put("/api/v1/blogs/42/publish")
                       .header("Authorization", AUTH))
               .andExpect(status().isNoContent());
    }

    @Test
    void testPublishAccessDenied() throws Exception {
        doThrow(new RuntimeException("ACCESS_DENIED"))
                .when(blogService).publishBlog(anyInt(), anyInt());

        mockMvc.perform(post("/api/v1/blogs/41/publish")
                       .header("Authorization", AUTH))
               .andExpect(status().isForbidden());
    }
}
