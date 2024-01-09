package com.sombra.cmsapi.businessservice.dto.course;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WithdrawInstructorRequest {
  @NotEmpty(message = "CourseId cannot be null or empty")
  private String courseId;

  @NotEmpty(message = "InstructorId cannot be null or empty")
  private String instructorId;
}