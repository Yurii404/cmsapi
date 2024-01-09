package com.sombra.cmsapi.businessservice.dto.course;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AssignInstructorRequest {
  @NotEmpty(message = "CourseId cannot be null or empty")
  private String courseId;

  @NotEmpty(message = "InstructorId cannot be null or empty")
  private String instructorId;
}