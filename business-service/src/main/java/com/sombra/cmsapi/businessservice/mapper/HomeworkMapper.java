package com.sombra.cmsapi.businessservice.mapper;

import com.sombra.cmsapi.businessservice.dto.homework.CreateHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.homework.HomeworkDto;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface HomeworkMapper {
  HomeworkMapper INSTANCE = Mappers.getMapper(HomeworkMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "lesson", source = "lesson")
  @Mapping(target = "task", source = "createHomeworkRequest.task")
  Homework createHomeworkRequestToHomework(
      CreateHomeworkRequest createHomeworkRequest, Lesson lesson);

  HomeworkDto homeworkToHomeworkDto(Homework homework);
}
