package com.sombra.cmsapi.businessservice.dto.homework;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateHomeworkRequest {

  @NotEmpty(message = "LessonId cannot be null or empty")
  private String lessonId;

  @NotEmpty(message = "LessonId cannot be null or empty")
  private String task;
}
