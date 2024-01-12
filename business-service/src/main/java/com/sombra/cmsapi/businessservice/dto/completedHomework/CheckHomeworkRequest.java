package com.sombra.cmsapi.businessservice.dto.completedHomework;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CheckHomeworkRequest {

  @NotNull(message = "Mark cannot be null or empty")
  @Min(value = 0, message = "The mark should be in range 0 - 100")
  @Max(value = 100, message = "The mark should be in range 0 - 100")
  private Integer mark;

  @NotEmpty(message = "InstructorId cannot be null or empty")
  private String instructorId;

  private String comment;
}
