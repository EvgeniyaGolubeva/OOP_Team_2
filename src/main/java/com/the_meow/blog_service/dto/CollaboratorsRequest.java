package com.the_meow.blog_service.dto;

import java.util.List;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CollaboratorsRequest {
    @Size(min = 1, message = "At least one collaborator ID must be provided")
    List<Integer> collaborators;
}
