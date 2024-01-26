package com.sombra.cmsapi.businessservice.dto.lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateLessonRequest {
  @NotEmpty(message = "Title cannot be null or empty")
  private String title;

  @NotEmpty(message = "Content cannot be null or empty")
  private String content;

  private String courseId;
}
