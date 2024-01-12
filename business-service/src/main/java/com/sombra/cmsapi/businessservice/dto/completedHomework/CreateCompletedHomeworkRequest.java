package com.sombra.cmsapi.businessservice.dto.completedHomework;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCompletedHomeworkRequest {

  @NotEmpty(message = "StudentId cannot be null or empty")
  private String studentId;

  @NotEmpty(message = "HomeworkId cannot be null or empty")
  private String homeworkId;
}
