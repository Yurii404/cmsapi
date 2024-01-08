package com.sombra.cmsapi.businessservice.dto.homework;

import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class HomeworkDto {

  private String id;
  private Lesson lesson;
  private String task;
  private List<CompletedHomework> completedHomeworks;
}
