package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.courseFeedback.CourseFeedbackDto;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CreateCourseFeedbackRequest;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.LeaveCommentRequest;
import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.CourseFeedback;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.CourseStatus;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.repository.CompletedHomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.CourseFeedbackRepository;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class CourseFeedbackServiceTest {

  @Mock private CourseFeedbackRepository courseFeedbackRepository;
  @Mock private CourseRepository courseRepository;
  @Mock private UserRepository userRepository;
  @Mock private CompletedHomeworkRepository completedHomeworkRepository;

  @InjectMocks private CourseFeedbackService courseFeedbackService;

  @Test
  void Should_SavePassed_When_ValidData_And_FinalMarkAboveOrEquals80() {
    CreateCourseFeedbackRequest request =
        CreateCourseFeedbackRequest.builder().courseId("1111").studentId("1111").build();

    Homework homework = Homework.builder().id("1111").task("Test task").build();

    Lesson lesson =
        Lesson.builder()
            .id("1111")
            .homeworks(new ArrayList<>(Collections.singletonList(homework)))
            .build();

    User student = User.builder().id("1111").build();

    Course course =
        Course.builder()
            .id("1111")
            .lessons(new ArrayList<>(Collections.singletonList(lesson)))
            .build();

    CompletedHomework completedHomework = CompletedHomework.builder().mark(80).build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.ofNullable(student));
    when(courseRepository.findById(any())).thenReturn(Optional.ofNullable(course));
    when(completedHomeworkRepository
            .findTopByStudentAndHomeworkAndMarkIsNotNullOrderBySubmissionDateDesc(any(), any()))
        .thenReturn(Optional.ofNullable(completedHomework));
    ArgumentCaptor<CourseFeedback> courseFeedbackArgumentCaptor =
        ArgumentCaptor.forClass(CourseFeedback.class);

    courseFeedbackService.save(request);

    verify(courseRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(completedHomeworkRepository, times(1))
        .findTopByStudentAndHomeworkAndMarkIsNotNullOrderBySubmissionDateDesc(any(), any());
    verify(courseFeedbackRepository, times(1)).save(courseFeedbackArgumentCaptor.capture());
    assertEquals(course.getId(), courseFeedbackArgumentCaptor.getValue().getCourse().getId());
    assertEquals(student.getId(), courseFeedbackArgumentCaptor.getValue().getStudent().getId());
    assertEquals(80, courseFeedbackArgumentCaptor.getValue().getFinalMark());
    assertEquals(CourseStatus.PASSED, courseFeedbackArgumentCaptor.getValue().getStatus());
  }

  @Test
  void Should_SaveFailed_When_ValidData_And_FinalMarkBelow80() {
    CreateCourseFeedbackRequest request =
        CreateCourseFeedbackRequest.builder().courseId("1111").studentId("1111").build();

    Homework homework = Homework.builder().id("1111").task("Test task").build();

    Lesson lesson =
        Lesson.builder()
            .id("1111")
            .homeworks(new ArrayList<>(Collections.singletonList(homework)))
            .build();

    User student = User.builder().id("1111").build();

    Course course =
        Course.builder()
            .id("1111")
            .lessons(new ArrayList<>(Collections.singletonList(lesson)))
            .build();

    CompletedHomework completedHomework = CompletedHomework.builder().mark(75).build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.ofNullable(student));
    when(courseRepository.findById(any())).thenReturn(Optional.ofNullable(course));
    when(completedHomeworkRepository
            .findTopByStudentAndHomeworkAndMarkIsNotNullOrderBySubmissionDateDesc(any(), any()))
        .thenReturn(Optional.ofNullable(completedHomework));
    ArgumentCaptor<CourseFeedback> courseFeedbackArgumentCaptor =
        ArgumentCaptor.forClass(CourseFeedback.class);

    courseFeedbackService.save(request);

    verify(courseRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(completedHomeworkRepository, times(1))
        .findTopByStudentAndHomeworkAndMarkIsNotNullOrderBySubmissionDateDesc(any(), any());
    verify(courseFeedbackRepository, times(1)).save(courseFeedbackArgumentCaptor.capture());
    assertEquals(course.getId(), courseFeedbackArgumentCaptor.getValue().getCourse().getId());
    assertEquals(student.getId(), courseFeedbackArgumentCaptor.getValue().getStudent().getId());
    assertEquals(75, courseFeedbackArgumentCaptor.getValue().getFinalMark());
    assertEquals(CourseStatus.FAILED, courseFeedbackArgumentCaptor.getValue().getStatus());
  }

  @Test
  void Should_LeaveFeedback_When_ValidData() {
    LeaveCommentRequest request =
        LeaveCommentRequest.builder().instructorId("1111").comment("Nice job!").build();

    User instructor = User.builder().id("1111").role(UserRole.INSTRUCTOR).build();

    CourseFeedback courseFeedback = CourseFeedback.builder().id("1111").build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.ofNullable(instructor));
    when(courseFeedbackRepository.findById(any())).thenReturn(Optional.ofNullable(courseFeedback));
    ArgumentCaptor<CourseFeedback> courseFeedbackArgumentCaptor =
        ArgumentCaptor.forClass(CourseFeedback.class);

    courseFeedbackService.leaveFeedback("1111", request);

    verify(courseFeedbackRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(courseFeedbackRepository, times(1)).save(courseFeedbackArgumentCaptor.capture());
    assertEquals(request.getComment(), courseFeedbackArgumentCaptor.getValue().getComment());
    assertEquals(
        request.getInstructorId(), courseFeedbackArgumentCaptor.getValue().getInstructor().getId());
  }

  @Test
  void
      Should_ThrowEntityNotFoundExceptionOnGetCompletedHomeworkByStudentAndHomework_When_DoNotHaveMark() {
    Homework homework = Homework.builder().id("1111").build();
    User student = User.builder().id("1111").build();

    when(completedHomeworkRepository
            .findTopByStudentAndHomeworkAndMarkIsNotNullOrderBySubmissionDateDesc(any(), any()))
        .thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(
                  courseFeedbackService,
                  "getCompletedHomeworkByStudentAndHomework",
                  student,
                  homework);
            });

    Assertions.assertEquals(
        "The user with id 1111 does not have a completed homework for homework with id 1111",
        thrown.getMessage());
  }

  @Test
  void Should_CountFinalGrade_When_ValidData() {

    CompletedHomework firstCompletedHomework = CompletedHomework.builder().mark(80).build();
    CompletedHomework secondCompletedHomework = CompletedHomework.builder().mark(100).build();

    Homework firstHomework =
        Homework.builder()
            .id("1111")
            .completedHomeworks(new ArrayList<>(List.of(firstCompletedHomework)))
            .build();
    Homework secondHomework =
        Homework.builder()
            .id("2222")
            .completedHomeworks(new ArrayList<>(List.of(firstCompletedHomework)))
            .build();

    Lesson lesson =
        Lesson.builder()
            .id("1111")
            .homeworks(new ArrayList<>(List.of(firstHomework, secondHomework)))
            .build();

    User student = User.builder().id("1111").build();

    Course course =
        Course.builder()
            .id("1111")
            .lessons(new ArrayList<>(Collections.singletonList(lesson)))
            .build();

    when(completedHomeworkRepository
            .findTopByStudentAndHomeworkAndMarkIsNotNullOrderBySubmissionDateDesc(
                student, firstHomework))
        .thenReturn(Optional.of(firstCompletedHomework));
    when(completedHomeworkRepository
            .findTopByStudentAndHomeworkAndMarkIsNotNullOrderBySubmissionDateDesc(
                student, secondHomework))
        .thenReturn(Optional.of(secondCompletedHomework));

    Double result =
        ReflectionTestUtils.invokeMethod(courseFeedbackService, "countFinalGrade", student, course);

    assertEquals(90, result);
  }

  @Test
  void Should_GetAll_When_Exist() {
    CourseFeedback firstCourseFeedback = CourseFeedback.builder().id("1111").build();
    CourseFeedback secondCourseFeedback = CourseFeedback.builder().id("2222").build();
    CourseFeedback thirdCourseFeedback = CourseFeedback.builder().id("3333").build();

    List<CourseFeedback> courseFeedbackList =
        List.of(firstCourseFeedback, secondCourseFeedback, thirdCourseFeedback);
    Page<CourseFeedback> courseFeedbackPage = new PageImpl<>(courseFeedbackList);

    when(courseFeedbackRepository.findAll(any(Pageable.class))).thenReturn(courseFeedbackPage);

    Page<CourseFeedbackDto> result = courseFeedbackService.getAll(Pageable.unpaged());

    assertNotNull(result);
    assertEquals(result.getTotalElements(), courseFeedbackList.size());
    verify(courseFeedbackRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void Should_GetById_When_Exists() {
    CourseFeedback courseFeedback = CourseFeedback.builder().id("1111").build();

    when(courseFeedbackRepository.findById(any())).thenReturn(Optional.of(courseFeedback));

    CourseFeedbackDto result = courseFeedbackService.getById("1111");

    assertNotNull(result);
    assertEquals(courseFeedback.getId(), result.getId());
    verify(courseFeedbackRepository, times(1)).findById(any());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetById_When_DoesNotExist() {
    String courseFeedbackId = "nonExistentId";

    when(courseFeedbackRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              courseFeedbackService.getById(courseFeedbackId);
            });

    Assertions.assertEquals(
        "Course feedback with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetCourseById_When_DoesNotExist() {
    String courseId = "nonExistentId";

    when(courseRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(courseFeedbackService, "getCourseById", courseId);
            });

    Assertions.assertEquals("Course with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetStudentById_When_DoesNotExist() {
    String studentId = "nonExistentId";

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(courseFeedbackService, "getStudentById", studentId);
            });

    Assertions.assertEquals("Student with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetInstructorById_When_DoesNotExist() {
    String instructorId = "nonExistentId";

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(
                  courseFeedbackService, "getInstructorById", instructorId);
            });

    Assertions.assertEquals(
        "Instructor with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetInstructorById_When_Exists() {
    String instructorId = "nonExistentId";
    User instructor = User.builder().id("1111").role(UserRole.INSTRUCTOR).build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(instructor));

    User result =
        ReflectionTestUtils.invokeMethod(courseFeedbackService, "getInstructorById", instructorId);

    assertEquals(result, instructor);
  }
}
