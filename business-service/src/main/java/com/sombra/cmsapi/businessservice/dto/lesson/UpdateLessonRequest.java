package com.sombra.cmsapi.businessservice.dto.lesson;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UpdateLessonRequest {
  @NotEmpty(message = "Id cannot be null or empty")
  private String id;

  @NotEmpty(message = "Title cannot be null or empty")
  private String title;

  @NotEmpty(message = "Content cannot be null or empty")
  private String content;

  private String courseId;
}
