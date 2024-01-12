package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CheckHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CourseFeedbackDto;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CreateCourseFeedbackRequest;
import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.CourseFeedback;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.CourseStatus;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.CourseFeedbackMapper;
import com.sombra.cmsapi.businessservice.repository.CompletedHomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.CourseFeedbackRepository;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class CourseFeedbackService {

  private final CourseFeedbackRepository courseFeedbackRepository;
  private final CourseRepository courseRepository;
  private final UserRepository userRepository;
  private final CompletedHomeworkRepository completedHomeworkRepository;
  private final Logger LOGGER = LoggerFactory.getLogger(CourseFeedbackService.class);
  private final CourseFeedbackMapper courseFeedbackMapper = CourseFeedbackMapper.INSTANCE;

  private static final int MINIMUM_GRADE_TO_PASS = 80;

  public CourseFeedbackDto save(CreateCourseFeedbackRequest requestDto) {
    User student = getStudentById(requestDto.getStudentId());
    Course course = getCourseById(requestDto.getCourseId());

    double finalGrade = countFinalGrade(student, course);

    CourseFeedback courseFeedback =
        CourseFeedback.builder()
            .course(course)
            .student(student)
            .finalMark((int) Math.round(finalGrade))
            .status(finalGrade >= MINIMUM_GRADE_TO_PASS ? CourseStatus.PASSED : CourseStatus.FAILED)
            .build();

    CourseFeedback savedCourseFeedback = courseFeedbackRepository.save(courseFeedback);

    return courseFeedbackMapper.courseFeedbackToCourseFeedbackDto(savedCourseFeedback);
  }

  public CourseFeedbackDto leaveFeedback(String courseFeedbackId, CheckHomeworkRequest requestDto) {
    CourseFeedback courseFeedback = getById(courseFeedbackId);
    User instructor = getInstructorById(requestDto.getInstructorId());

    courseFeedback.setComment(requestDto.getComment());
    courseFeedback.setInstructor(instructor);

    CourseFeedback savedCourseFeedback = courseFeedbackRepository.save(courseFeedback);

    return courseFeedbackMapper.courseFeedbackToCourseFeedbackDto(savedCourseFeedback);
  }

  private double countFinalGrade(User student, Course course) {
    List<Homework> allHomeworks =
        course.getLessons().stream().flatMap(lesson -> lesson.getHomeworks().stream()).toList();

    return allHomeworks.stream()
        .map(it -> getCompletedHomeworkByStudentAndHomework(student, it))
        .mapToInt(CompletedHomework::getMark)
        .average()
        .getAsDouble();
  }

  public List<CourseFeedback> getAll() {
    return courseFeedbackRepository.findAll();
  }

  public CourseFeedback getById(String id) {
    return courseFeedbackRepository
        .findById(id)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Course feedback with id: %s does not exist!", id)));
  }

  private Course getCourseById(String courseId) {
    return courseRepository
        .findById(courseId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Course with id: %s does not exist!", courseId)));
  }

  private CompletedHomework getCompletedHomeworkByStudentAndHomework(User user, Homework homework) {
    return completedHomeworkRepository
        .findFirsByStudentAndHomeworkOrderBySubmissionDateDesc(user, homework)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format(
                        "The user with id %s does not have a completed homework for homework with id %s",
                        user.getId(), homework.getId())));
  }

  private User getStudentById(String userId) {
    return userRepository
        .findByIdAndRole(userId, UserRole.STUDENT)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Student with id: %s does not exist!", userId)));
  }

  private User getInstructorById(String userId) {
    return userRepository
        .findByIdAndRole(userId, UserRole.INSTRUCTOR)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Instructor with id: %s does not exist!", userId)));
  }
}
