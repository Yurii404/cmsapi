package com.sombra.cmsapi.businessservice.dto.course;

import com.sombra.cmsapi.businessservice.entity.CourseFeedback;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CourseDto {

  private String id;
  private String name;
  private String description;
  private List<User> students;
  private List<User> instructors;
  private List<Lesson> lessons;
  private List<CourseFeedback> feedbacks;
}
