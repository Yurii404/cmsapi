package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.course.CourseDto;
import com.sombra.cmsapi.businessservice.dto.course.CreateCourseRequest;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.CourseMapper;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import com.sombra.cmsapi.businessservice.repository.LessonRepository;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CourseService {

  private final CourseRepository courseRepository;
  private final UserRepository userRepository;
  private final LessonRepository lessonRepository;
  private final CourseMapper courseMapper = CourseMapper.INSTANCE;

  public CourseDto save(CreateCourseRequest createCourseRequest) {
    List<User> instructors =
        createCourseRequest.getInstructorIds().stream().map(this::getInstructorById).toList();

    List<Lesson> lessons =
        createCourseRequest.getLessonIds().stream().map(this::getLessonById).toList();

    Course courseToSave =
        courseMapper.createCourseRequestToCourse(createCourseRequest, instructors, lessons);

    Course savedCourse = courseRepository.save(courseToSave);

    lessons.forEach(lesson -> lesson.setCourse(savedCourse));
    lessonRepository.saveAll(lessons);

    return courseMapper.courseToCourseDto(savedCourse);
  }

  public Course getById(String courseId) {
    return courseRepository
        .findById(courseId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Course with id: %s does not exist!", courseId)));
  }

  private User getInstructorById(String userId) {
    return userRepository
        .findByIdAndRole(userId, UserRole.INSTRUCTOR)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Instructor with id: %s does not exist!", userId)));
  }

  private Lesson getLessonById(String lessonId) {
    return lessonRepository
        .findById(lessonId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Lesson with id: %s does not exist!", lessonId)));
  }
}
