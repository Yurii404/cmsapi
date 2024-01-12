package com.sombra.cmsapi.businessservice.dto.courseFeedback;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class LeaveCommentRequest {

  @NotEmpty(message = "InstructorId cannot be null or empty")
  private String instructorId;

  @NotEmpty(message = "Comment cannot be null or empty")
  private String comment;
}
