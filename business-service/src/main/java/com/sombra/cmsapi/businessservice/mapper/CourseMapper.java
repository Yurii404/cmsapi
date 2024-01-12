package com.sombra.cmsapi.businessservice.mapper;

import com.sombra.cmsapi.businessservice.dto.course.CourseDto;
import com.sombra.cmsapi.businessservice.dto.course.CreateCourseRequest;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseMapper {
  CourseMapper INSTANCE = Mappers.getMapper(CourseMapper.class);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "instructors", source = "instructors")
  @Mapping(target = "lessons", source = "lessons")
  @Mapping(target = "name", source = "createLessonRequest.name")
  @Mapping(target = "description", source = "createLessonRequest.description")
  Course createCourseRequestToCourse(
      CreateCourseRequest createLessonRequest, List<User> instructors, List<Lesson> lessons);

  CourseDto courseToCourseDto(Course course);
}
