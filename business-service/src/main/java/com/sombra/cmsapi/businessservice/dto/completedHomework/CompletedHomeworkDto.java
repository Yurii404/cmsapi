package com.sombra.cmsapi.businessservice.dto.completedHomework;

import com.sombra.cmsapi.businessservice.entity.CourseFeedback;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import java.time.ZonedDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@AllArgsConstructor
@Builder
public class CompletedHomeworkDto {

  private String id;
  private User student;
  private User instructor;
  private Homework homework;
  private byte[] homeworkFile;
  private int mark;
  private String comment;
  private ZonedDateTime submissionDate;
  private ZonedDateTime reviewDate;
}
