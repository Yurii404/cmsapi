package com.sombra.cmsapi.businessservice.dto.courseFeedback;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCourseFeedbackRequest {
  @NotEmpty(message = "StudentId cannot be null or empty")
  private String studentId;

  @NotEmpty(message = "CourseId cannot be null or empty")
  private String courseId;
}
