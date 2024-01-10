package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.course.AssignInstructorRequest;
import com.sombra.cmsapi.businessservice.dto.course.CourseDto;
import com.sombra.cmsapi.businessservice.dto.course.CreateCourseRequest;
import com.sombra.cmsapi.businessservice.dto.course.WithdrawInstructorRequest;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.exception.NotAllowedOperationException;
import com.sombra.cmsapi.businessservice.mapper.CourseMapper;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import com.sombra.cmsapi.businessservice.repository.LessonRepository;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class CourseService {

  private final CourseRepository courseRepository;
  private final UserRepository userRepository;
  private final LessonRepository lessonRepository;
  private final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);
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

  public CourseDto assignInstructor(AssignInstructorRequest requestDto) {
    Course course = getById(requestDto.getCourseId());
    User instructor = getInstructorById(requestDto.getInstructorId());

    if (!course.getInstructors().contains(instructor)) {
      course.getInstructors().add(instructor);

      Course savedCourse = courseRepository.save(course);

      return courseMapper.courseToCourseDto(savedCourse);
    } else {
      LOGGER.warn(
          String.format(
              "Instructor with id %s is already teaching course with id %s",
              instructor.getId(), course.getId()));
      throw new NotAllowedOperationException(
          String.format(
              "Instructor with id %s is already teaching course with id %s",
              instructor.getId(), course.getId()));
    }
  }

  public CourseDto withdrawInstructor(WithdrawInstructorRequest requestDto) {
    Course course = getById(requestDto.getCourseId());

    if (course.getInstructors().size() > 1) {
      User instructor = getInstructorById(requestDto.getInstructorId());

      course.getInstructors().remove(instructor);

      Course savedCourse = courseRepository.save(course);

      return courseMapper.courseToCourseDto(savedCourse);
    } else {
      LOGGER.warn("Course must have at least one instructor!");
      throw new NotAllowedOperationException("Course must have at least one instructor!");
    }
  }

  public List<Course> getAll() {
    return courseRepository.findAll();
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
