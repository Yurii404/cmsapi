package com.sombra.cmsapi.businessservice.dto.course;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateCourseRequest {
  @NotEmpty(message = "Name cannot be null or empty")
  private String name;

  @NotEmpty(message = "Description cannot be null or empty")
  private String description;

  @Size(min = 1, message = "Course should have at least one instructor")
  @NotEmpty(message = "Course should have at least one instructor")
  private List<String> instructorIds;

  @Size(min = 5, message = "Course should have at least five lessons")
  @NotEmpty(message = "Course should have at least five lessons")
  private List<String> lessonIds;
}
