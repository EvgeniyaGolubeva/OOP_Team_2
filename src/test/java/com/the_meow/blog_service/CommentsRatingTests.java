package com.the_meow.blog_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.the_meow.blog_service.controller.CommentRatingController;
import com.the_meow.blog_service.dto.RatingRequest;
import com.the_meow.blog_service.dto.RatingResponse;
import com.the_meow.blog_service.service.CommentRatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class CommentRatingTests {

    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Mock
    private CommentRatingService ratingService;

    private static final String AUTH = "Bearer valid.jwt";

    @BeforeEach
    void setup() {
        CommentRatingController controller = new CommentRatingController(ratingService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        mapper = new ObjectMapper();
    }

    // ---------------- Happy Paths ----------------

    @Test
    @DisplayName("POST rating - happy path")
    void testSubmitRatingHappyPath() throws Exception {
        doNothing().when(ratingService).submitRating(eq(42), eq(7), eq(3), any(RatingRequest.class));

        RatingRequest req = new RatingRequest(4.0);
        mockMvc.perform(post("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET ratings - happy path")
    void testGetRatingsHappyPath() throws Exception {
        RatingResponse resp = new RatingResponse(4.25, 8, true);
        when(ratingService.getRatingsByCommentId(eq(42), eq(7), any())).thenReturn(resp);

        mockMvc.perform(get("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.average").value(4.25))
               .andExpect(jsonPath("$.count").value(8))
               .andExpect(jsonPath("$.alreadyRated").value(true));
    }

    @Test
    void testUpdateRatingHappyPath() throws Exception {
        doNothing().when(ratingService).updateRating(eq(42), eq(7), eq(3), any(RatingRequest.class));

        RatingRequest req = new RatingRequest(5.0);
        mockMvc.perform(put("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteRatingHappyPath() throws Exception {
        doNothing().when(ratingService).deleteRating(eq(42), eq(7), eq(3));

        mockMvc.perform(delete("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH))
               .andExpect(status().isNoContent());
    }

    // ---------------- Negative Paths ----------------

    @Test
    void testSubmitRatingMissingValue() throws Exception {
        RatingRequest req = new RatingRequest(null);

        mockMvc.perform(post("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isBadRequest());
    }

    @Test
    void testSubmitRatingBelowRange() throws Exception {
        RatingRequest req = new RatingRequest(-1.0);

        mockMvc.perform(post("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testSubmitRatingAboveRange() throws Exception {
        RatingRequest req = new RatingRequest(5.5);

        mockMvc.perform(post("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testSubmitRatingTooPrecise() throws Exception {
        RatingRequest req = new RatingRequest(4.55);

        mockMvc.perform(post("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testSubmitDuplicateRating() throws Exception {
        doThrow(new RuntimeException("RATING_EXISTS"))
                .when(ratingService).submitRating(anyInt(), anyInt(), anyInt(), any());

        RatingRequest req = new RatingRequest(4.0);
        mockMvc.perform(post("/api/v1/blogs/42/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isConflict());
    }

    @Test
    void testSubmitBlogNotFound() throws Exception {
        doThrow(new RuntimeException("BLOG_NOT_FOUND"))
                .when(ratingService).submitRating(anyInt(), anyInt(), anyInt(), any());

        RatingRequest req = new RatingRequest(4.0);
        mockMvc.perform(post("/api/v1/blogs/999/comments/7/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isNotFound());
    }

    @Test
    void testSubmitCommentNotFound() throws Exception {
        doThrow(new RuntimeException("COMMENT_NOT_FOUND"))
                .when(ratingService).submitRating(anyInt(), anyInt(), anyInt(), any());

        RatingRequest req = new RatingRequest(4.0);
        mockMvc.perform(post("/api/v1/blogs/42/comments/999/ratings")
                       .header("Authorization", AUTH)
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isNotFound());
    }

    @Test
    void testSubmitUnauthorized() throws Exception {
        RatingRequest req = new RatingRequest(4.0);

        mockMvc.perform(post("/api/v1/blogs/42/comments/7/ratings")
                       .contentType(MediaType.APPLICATION_JSON)
                       .content(mapper.writeValueAsString(req)))
               .andExpect(status().isUnauthorized());
    }

    @Test
    void testGetRatingsWhenNoneExist() throws Exception {
        RatingResponse resp = new RatingResponse(0.0, 0, false);
        when(ratingService.getRatingsByCommentId(eq(42), eq(7), any())).thenReturn(resp);

        mockMvc.perform(get("/api/v1/blogs/42/comments/7/ratings"))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.average").value(0.0))
               .andExpect(jsonPath("$.count").value(0))
               .andExpect(jsonPath("$.alreadyRated").value(false));
    }
}
