package com.sombra.cmsapi.businessservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.course.CourseDto;
import com.sombra.cmsapi.businessservice.dto.course.CreateCourseRequest;
import com.sombra.cmsapi.businessservice.dto.lesson.LessonDto;
import com.sombra.cmsapi.businessservice.dto.user.UserDto;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.service.CourseService;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class CourseControllerTest {

  @Mock private CourseService courseService;

  @InjectMocks private CourseController courseController;

  @Test
  void Should_Create_When_ValidRequest() {
    // SETUP
    CreateCourseRequest createCourseRequest =
        CreateCourseRequest.builder()
            .name("Name")
            .description("Description")
            .instructorIds(Collections.singletonList("1"))
            .lessonIds(Collections.singletonList("1"))
            .build();

    CourseDto expectedCourseDto =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    when(courseService.save(createCourseRequest)).thenReturn(expectedCourseDto);

    // ACT
    ResponseEntity<CourseDto> responseEntity = courseController.create(createCourseRequest);

    // VERIFY
    verify(courseService, times(1)).save(createCourseRequest);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(expectedCourseDto, responseEntity.getBody());
  }

  @Test
  void Should_GetById_When_ValidRequest() {
    // SETUP
    CourseDto expectedCourseDto =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    when(courseService.getById("1")).thenReturn(expectedCourseDto);

    // ACT
    ResponseEntity<CourseDto> responseEntity = courseController.getById("1");

    // VERIFY
    verify(courseService, times(1)).getById("1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCourseDto, responseEntity.getBody());
  }

  @Test
  void Should_GetAll_When_ValidRequest() {
    // SETUP
    CourseDto expectedCourseDtoFirst =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();
    CourseDto expectedCourseDtoSecond =
        CourseDto.builder()
            .id("2")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    Page<CourseDto> pageCourse =
        new PageImpl<>(List.of(expectedCourseDtoFirst, expectedCourseDtoSecond));

    when(courseService.getAll(any())).thenReturn(pageCourse);

    // ACT
    ResponseEntity<Page<CourseDto>> responseEntity = courseController.getAll(Pageable.unpaged());

    // VERIFY
    verify(courseService, times(1)).getAll(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(pageCourse, responseEntity.getBody());
  }

  @Test
  void Should_GetStudentsByCourseId_When_ValidRequest() {
    // SETUP
    UserDto expectedUserDtoFirst =
        UserDto.builder()
            .id("1")
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .password("password")
            .build();

    UserDto expectedUserDtoSecond =
        UserDto.builder()
            .id("2")
            .email("test@mail.com")
            .firstName("John")
            .lastName("Doe")
            .role(UserRole.STUDENT)
            .password("password")
            .build();

    List<UserDto> students = List.of(expectedUserDtoFirst, expectedUserDtoSecond);

    when(courseService.getStudentsByCourseId(any())).thenReturn(students);

    // ACT
    ResponseEntity<List<UserDto>> responseEntity = courseController.getStudentsByCourseId("1");

    // VERIFY
    verify(courseService, times(1)).getStudentsByCourseId(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(students, responseEntity.getBody());
  }

  @Test
  void Should_GetLessonsByCourseId_When_ValidRequest() {
    // SETUP
    LessonDto expectedLessonDtoFirst =
        LessonDto.builder()
            .id("1")
            .title("Title")
            .content("Content")
            .course(mock(Course.class))
            .build();

    LessonDto expectedLessonDtoSecond =
        LessonDto.builder()
            .id("2")
            .title("Title")
            .content("Content")
            .course(mock(Course.class))
            .build();

    List<LessonDto> lessons = List.of(expectedLessonDtoFirst, expectedLessonDtoSecond);

    when(courseService.getLessonsByCourseId(any())).thenReturn(lessons);

    // ACT
    ResponseEntity<List<LessonDto>> responseEntity = courseController.getLessonsByCourseId("1");

    // VERIFY
    verify(courseService, times(1)).getLessonsByCourseId(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(lessons, responseEntity.getBody());
  }

  @Test
  void Should_GetAllBySearch_When_ValidRequest() {
    // SETUP
    CourseDto expectedCourseDtoFirst =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();
    CourseDto expectedCourseDtoSecond =
        CourseDto.builder()
            .id("2")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    Page<CourseDto> pageCourse =
        new PageImpl<>(List.of(expectedCourseDtoFirst, expectedCourseDtoSecond));

    when(courseService.search(any(), any(), any())).thenReturn(pageCourse);

    // ACT
    ResponseEntity<Page<CourseDto>> responseEntity =
        courseController.getAllBySearch(Pageable.unpaged(), "test", "test");

    // VERIFY
    verify(courseService, times(1)).search(any(), any(), any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(pageCourse, responseEntity.getBody());
  }

  @Test
  void Should_AssignInstructor_When_ValidRequest() {
    // SETUP
    CourseDto expectedCourseDto =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    when(courseService.assignInstructor("1", "1")).thenReturn(expectedCourseDto);

    // ACT
    ResponseEntity<CourseDto> responseEntity = courseController.assignInstructor("1", "1");

    // VERIFY
    verify(courseService, times(1)).assignInstructor("1", "1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCourseDto, responseEntity.getBody());
  }

  @Test
  void Should_WithdrawInstructor_When_ValidRequest() {
    // SETUP
    CourseDto expectedCourseDto =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    when(courseService.withdrawInstructor("1", "1")).thenReturn(expectedCourseDto);

    // ACT
    ResponseEntity<CourseDto> responseEntity = courseController.withdrawInstructor("1", "1");

    // VERIFY
    verify(courseService, times(1)).withdrawInstructor("1", "1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCourseDto, responseEntity.getBody());
  }

  @Test
  void Should_RegisterStudent_When_ValidRequest() {
    // SETUP
    CourseDto expectedCourseDto =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    when(courseService.registerStudent("1", "1")).thenReturn(expectedCourseDto);

    // ACT
    ResponseEntity<CourseDto> responseEntity = courseController.registerStudent("1", "1");

    // VERIFY
    verify(courseService, times(1)).registerStudent("1", "1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCourseDto, responseEntity.getBody());
  }

  @Test
  void Should_RemoveStudent_When_ValidRequest() {
    // SETUP
    CourseDto expectedCourseDto =
        CourseDto.builder()
            .id("1")
            .description("Description")
            .name("Name")
            .lessons(Collections.singletonList(mock(Lesson.class)))
            .instructors(Collections.singletonList(mock(User.class)))
            .build();

    when(courseService.removeStudent("1", "1")).thenReturn(expectedCourseDto);

    // ACT
    ResponseEntity<CourseDto> responseEntity = courseController.removeStudent("1", "1");

    // VERIFY
    verify(courseService, times(1)).removeStudent("1", "1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCourseDto, responseEntity.getBody());
  }
}
