package com.sombra.cmsapi.businessservice.dto.courseFeedback;

import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.CourseStatus;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CourseFeedbackDto {

  private String id;
  private User student;
  private User instructor;
  private Course course;
  private CourseStatus status;
  private int finalMark;
  private String content;
  private ZonedDateTime created;
}
