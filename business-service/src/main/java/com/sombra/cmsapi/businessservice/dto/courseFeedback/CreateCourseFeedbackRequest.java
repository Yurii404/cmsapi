package com.sombra.cmsapi.businessservice.dto.courseFeedback;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class CreateCourseFeedbackRequest {
  @NotEmpty(message = "StudentId cannot be null or empty")
  private String studentId;

  @NotEmpty(message = "CourseId cannot be null or empty")
  private String courseId;
}
