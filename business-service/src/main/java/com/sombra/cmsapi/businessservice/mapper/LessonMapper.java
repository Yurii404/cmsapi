package com.sombra.cmsapi.businessservice.mapper;

import com.sombra.cmsapi.businessservice.dto.lesson.CreateLessonRequest;
import com.sombra.cmsapi.businessservice.dto.lesson.LessonDto;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface LessonMapper {
  LessonMapper INSTANCE = Mappers.getMapper(LessonMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "course", source = "course")
  @Mapping(target = "title", source = "createLessonRequest.title")
  @Mapping(target = "content", source = "createLessonRequest.content")
  Lesson createLessonRequestToLesson(CreateLessonRequest createLessonRequest, Course course);

  Lesson createLessonRequestToLesson(CreateLessonRequest createLessonRequest);

  LessonDto lessonToLessonDto(Lesson lesson);
}
