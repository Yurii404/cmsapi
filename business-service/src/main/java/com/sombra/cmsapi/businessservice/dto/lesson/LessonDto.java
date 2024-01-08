package com.sombra.cmsapi.businessservice.dto.lesson;

import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Homework;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LessonDto {

  private String id;
  private String title;
  private String content;
  private Course course;
  private List<Homework> homeworks;
}
