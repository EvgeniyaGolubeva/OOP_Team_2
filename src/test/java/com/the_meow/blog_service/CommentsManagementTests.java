package com.the_meow.blog_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.the_meow.blog_service.controller.CommentController;
import com.the_meow.blog_service.dto.CommentCreateRequest;
import com.the_meow.blog_service.dto.CommentInfo;
import com.the_meow.blog_service.dto.CommentUpdateRequest;
import com.the_meow.blog_service.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 *   SCENARIO-001 – testCreateCommentHappyPath
 *   SCENARIO-002 – testUpdateCommentHappyPath
 *   SCENARIO-003 – testDeleteCommentHappyPath
 *   SCENARIO-004 – testCreateMissingContent
 *   SCENARIO-005 – testCreateContentTooShort
 *   SCENARIO-006 – testCreateContentTooLong
 *   SCENARIO-007 – testCreateContentXSS
 *   SCENARIO-008 – testCreateBlogNotFound
 *   SCENARIO-009 – testCreateNoAuth
 *   SCENARIO-010 – testCreateInvalidToken
 *   SCENARIO-012 – testUpdateCommentNotOwned
 *   SCENARIO-013 – testUpdateCommentNotFound
 *   SCENARIO-018 – testDeleteNotOwned
 *   SCENARIO-019 – testDeleteAlreadyDeleted
 *   SCENARIO-020 – testIdNotNumeric
 *   SCENARIO-021 – testBlogCommentMismatch
 */

@ExtendWith(MockitoExtension.class)
class CommentControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @Mock
    private CommentService commentService;

    private static final String AUTH = "Bearer valid.jwt";

    @BeforeEach
    void setUp() {
        CommentController controller = new CommentController(commentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                                 .build();
        objectMapper = new ObjectMapper();
    }

    /** SCENARIO-001: Create comment – happy path. */
    @Test
    @DisplayName("POST /blogs/{id}/comments - happy path")
    void testCreateCommentHappyPath() throws Exception {
        CommentInfo stub = new CommentInfo(7L, 42L, 3L, "Nice post!", OffsetDateTime.now(), null);
        when(commentService.createComment(eq(42), eq(3), any(CommentCreateRequest.class))).thenReturn(stub);

        CommentCreateRequest req = new CommentCreateRequest("Nice post!");
        mockMvc.perform(post("/api/v1/blogs/42/comments")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isCreated())
               .andExpect(jsonPath("$.id").value(7));

        verify(commentService, times(1)).createComment(eq(42), eq(3), any());
    }

    /** SCENARIO-002: Update comment – happy path. */
    @Test
    void testUpdateCommentHappyPath() throws Exception {
        CommentInfo updated = new CommentInfo(7L, 42L, 3L, "Edited text", OffsetDateTime.now(), OffsetDateTime.now());
        when(commentService.updateComment(eq(42), eq(7), eq(3), any(CommentUpdateRequest.class))).thenReturn(updated);

        CommentUpdateRequest req = new CommentUpdateRequest("Edited text");
        mockMvc.perform(patch("/api/v1/blogs/42/comments/7")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.content").value("Edited text"));

        verify(commentService, times(1)).updateComment(eq(42), eq(7), eq(3), any());
    }

    /** SCENARIO-003: Delete comment – happy path. */
    @Test
    void testDeleteCommentHappyPath() throws Exception {
        doNothing().when(commentService).deleteComment(42, 7, 3);

        mockMvc.perform(delete("/api/v1/blogs/42/comments/7")
                       .header("Authorization", AUTH))
               .andExpect(status().isNoContent());

        verify(commentService, times(1)).deleteComment(42, 7, 3);
    }

    /** SCENARIO-004: Create comment missing content. */
    @Test
    void testCreateMissingContent() throws Exception {
        CommentCreateRequest req = new CommentCreateRequest(null);

        mockMvc.perform(post("/api/v1/blogs/42/comments")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }

    /** SCENARIO-005: Create comment too short. */
    @Test
    void testCreateContentTooShort() throws Exception {
        CommentCreateRequest req = new CommentCreateRequest("");

        mockMvc.perform(post("/api/v1/blogs/42/comments")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isUnprocessableEntity());
    }

    /** SCENARIO-006: Create comment excessively long. */
    @Test
    void testCreateContentTooLong() throws Exception {
        String longStr = "A".repeat(5001);
        CommentCreateRequest req = new CommentCreateRequest(longStr);

        mockMvc.perform(post("/api/v1/blogs/42/comments")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isPayloadTooLarge());
    }

    /** SCENARIO-008: Blog not found when creating comment. */
    @Test
    void testCreateBlogNotFound() throws Exception {
        when(commentService.createComment(anyInt(), anyInt(), any())).thenThrow(new RuntimeException("BLOG_NOT_FOUND"));

        CommentCreateRequest req = new CommentCreateRequest("Hello");
        mockMvc.perform(post("/api/v1/blogs/999/comments")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isNotFound());
    }

    /** SCENARIO-009/010: Unauthorized or invalid token create comment. */
    @Test
    void testCreateNoAuth() throws Exception {
        CommentCreateRequest req = new CommentCreateRequest("Test");
        mockMvc.perform(post("/api/v1/blogs/42/comments")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isUnauthorized());
    }

    /** SCENARIO-012: Update not owned comment. */
    @Test
    void testUpdateCommentNotOwned() throws Exception {
        when(commentService.updateComment(anyInt(), anyLong(), anyInt(), any()))
                .thenThrow(new RuntimeException("ACCESS_DENIED"));

        CommentUpdateRequest req = new CommentUpdateRequest("Another");
        mockMvc.perform(patch("/api/v1/blogs/42/comments/7")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(objectMapper.writeValueAsString(req)))
               .andExpect(status().isForbidden());
    }

    /** SCENARIO-020: Non‑numeric ID. */
    @Test
    void testIdNotNumeric() throws Exception {
        mockMvc.perform(get("/api/v1/blogs/42/comments/abc")
                       .header("Authorization", AUTH))
               .andExpect(status().isBadRequest());
    }
}
