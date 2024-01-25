package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
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
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.exception.NotAllowedOperationException;
import com.sombra.cmsapi.businessservice.exception.WrongSearchFieldException;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import com.sombra.cmsapi.businessservice.repository.LessonRepository;
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
class CourseServiceTest {

  @Mock private CourseRepository courseRepository;
  @Mock private UserRepository userRepository;
  @Mock private LessonRepository lessonRepository;
  @InjectMocks private CourseService courseService;

  @Test
  void Should_Save_When_ValidData_And_CourseIsPassedAndExists() {
    CreateCourseRequest request =
        CreateCourseRequest.builder()
            .description("Test description")
            .name("Test name")
            .instructorIds(Collections.singletonList("1111"))
            .lessonIds(Collections.singletonList("1111"))
            .build();

    User instructor =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    Lesson lesson = Lesson.builder().id("1111").content("Test content").title("Test title").build();

    Course savedCourse =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .instructors(Collections.singletonList(instructor))
            .lessons(Collections.singletonList(lesson))
            .build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.ofNullable(instructor));
    when(lessonRepository.findById(any())).thenReturn(Optional.ofNullable(lesson));
    when(courseRepository.save(any())).thenReturn(savedCourse);
    ArgumentCaptor<List<Lesson>> lessonArgumentCaptor = ArgumentCaptor.forClass(List.class);
    ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

    courseService.save(request);

    verify(lessonRepository, times(1)).saveAll(any());
    verify(courseRepository, times(1)).save(any());
    verify(lessonRepository).saveAll(lessonArgumentCaptor.capture());
    verify(courseRepository).save(courseArgumentCaptor.capture());
    assertEquals(savedCourse.getId(), lessonArgumentCaptor.getValue().get(0).getId());
    assertEquals(request.getDescription(), courseArgumentCaptor.getValue().getDescription());
    assertEquals(request.getName(), courseArgumentCaptor.getValue().getName());
    assertEquals(
        request.getInstructorIds().get(0),
        courseArgumentCaptor.getValue().getInstructors().get(0).getId());
    assertEquals(
        request.getLessonIds().get(0), courseArgumentCaptor.getValue().getLessons().get(0).getId());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnSave_When_InstructorNotDoesNotExist() {
    CreateCourseRequest request =
        CreateCourseRequest.builder()
            .description("Test description")
            .name("Test name")
            .instructorIds(Collections.singletonList("1111"))
            .lessonIds(Collections.singletonList("1111"))
            .build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              courseService.save(request);
            });

    Assertions.assertEquals("Instructor with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnSave_When_LessonNotDoesNotExist() {
    CreateCourseRequest request =
        CreateCourseRequest.builder()
            .description("Test description")
            .name("Test name")
            .instructorIds(Collections.singletonList("1111"))
            .lessonIds(Collections.singletonList("1111"))
            .build();

    User instructor =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(instructor));
    when(lessonRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              courseService.save(request);
            });

    Assertions.assertEquals("Lesson with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_RemoveStudent_When_ValidData() {
    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .students(new ArrayList<>(Collections.singletonList(student)))
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(student));
    ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

    courseService.removeStudent("1111", "1111");

    verify(courseRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(courseRepository, times(1)).save(any());
    verify(courseRepository, times(1)).save(courseArgumentCaptor.capture());
    assertEquals(0, courseArgumentCaptor.getValue().getStudents().size());
  }

  @Test
  void Should_RegisterStudent_When_ValidData() {
    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .students(new ArrayList<>())
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(student));
    ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

    courseService.registerStudent("1111", "1111");

    verify(courseRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(courseRepository, times(1)).save(any());
    verify(courseRepository, times(1)).save(courseArgumentCaptor.capture());
    assertEquals(1, courseArgumentCaptor.getValue().getStudents().size());
    assertTrue(courseArgumentCaptor.getValue().getStudents().contains(student));
  }

  @Test
  void Should_WithdrawInstructor_When_ValidData() {
    User firstInstructor =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    User secondInstructor =
        User.builder()
            .id("2222")
            .email("test@mail")
            .password("password")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .instructors(new ArrayList<>(List.of(firstInstructor, secondInstructor)))
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(firstInstructor));
    ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

    courseService.withdrawInstructor("1111", "1111");

    verify(courseRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(courseRepository, times(1)).save(any());
    verify(courseRepository, times(1)).save(courseArgumentCaptor.capture());
    assertEquals(1, courseArgumentCaptor.getValue().getInstructors().size());
  }

  @Test
  void Should_AssignInstructor_When_ValidData() {
    User instructor =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .instructors(new ArrayList<>())
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(instructor));
    ArgumentCaptor<Course> courseArgumentCaptor = ArgumentCaptor.forClass(Course.class);

    courseService.assignInstructor("1111", "1111");

    verify(courseRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(courseRepository, times(1)).save(any());
    verify(courseRepository, times(1)).save(courseArgumentCaptor.capture());
    assertEquals(1, courseArgumentCaptor.getValue().getInstructors().size());
    assertEquals(
        instructor.getId(), courseArgumentCaptor.getValue().getInstructors().get(0).getId());
  }

  @Test
  void Should_ThrowNotAllowedOperationOnWithdrawInstructor_WhenLimitOfInstructors() {
    User instructor =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .instructors(new ArrayList<>(List.of(instructor)))
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));

    NotAllowedOperationException thrown =
        Assertions.assertThrows(
            NotAllowedOperationException.class,
            () -> courseService.withdrawInstructor("1111", "1111"));

    Assertions.assertEquals("Course must have at least one instructor!", thrown.getMessage());
  }

  @Test
  void Should_ThrowNotAllowedOperationOnAssignInstructor_WhenAlreadyAssigned() {
    User instructor =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.INSTRUCTOR)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .instructors(new ArrayList<>(List.of(instructor)))
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(instructor));

    NotAllowedOperationException thrown =
        Assertions.assertThrows(
            NotAllowedOperationException.class,
            () -> courseService.assignInstructor("1111", "1111"));

    Assertions.assertEquals(
        "Instructor with id 1111 is already teaching course with id 1111", thrown.getMessage());
  }

  @Test
  void Should_SearchByInstructor_When_ValidSearchFiled() {
    Course course =
        Course.builder().id("1111").description("Test description").name("Test name").build();

    when(courseRepository.findByInstructorsId(any(), any()))
        .thenReturn(new PageImpl<>(Collections.singletonList(course)));

    courseService.search("instructorId", "1111", Pageable.unpaged());

    verify(courseRepository, times(1)).findByInstructorsId(any(), any());
  }

  @Test
  void Should_SearchByStudent_When_ValidSearchFiled() {
    Course course =
        Course.builder().id("1111").description("Test description").name("Test name").build();

    when(courseRepository.findByStudentsId(any(), any()))
        .thenReturn(new PageImpl<>(Collections.singletonList(course)));

    courseService.search("studentId", "1111", Pageable.unpaged());

    verify(courseRepository, times(1)).findByStudentsId(any(), any());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnSearch_When_WrongSearchField() {

    WrongSearchFieldException thrown =
        Assertions.assertThrows(
            WrongSearchFieldException.class,
            () -> courseService.search("wrongField", "test", Pageable.unpaged()));

    Assertions.assertEquals(
        "The search is not allowed for the field wrongField", thrown.getMessage());
  }

  @Test
  void
      Should_ThrowNotAllowedOperationExceptionOnValidateStudentRegistration_When_AlreadyRegistered() {

    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .students(Collections.singletonList(student))
            .build();

    NotAllowedOperationException thrown =
        Assertions.assertThrows(
            NotAllowedOperationException.class,
            () ->
                ReflectionTestUtils.invokeMethod(
                    courseService, "validateStudentRegistration", course, student));

    Assertions.assertEquals(
        "Student with id 1111 is already registered for this course with id 1111.",
        thrown.getMessage());
  }

  @Test
  void Should_NotThrowOnValidateStudentRegistration_When_NotAlreadyRegistered() {

    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    User registeredStudent =
        User.builder()
            .id("2222")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .students(Collections.singletonList(registeredStudent))
            .build();

    ReflectionTestUtils.invokeMethod(courseService, "validateStudentRegistration", course, student);
  }

  @Test
  void Should_ThrowNotAllowedOperationExceptionOnValidateStudentRemoving_When_IsNotRegistered() {

    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    User registeredStudent =
        User.builder()
            .id("2222")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .students(Collections.singletonList(registeredStudent))
            .build();

    NotAllowedOperationException thrown =
        Assertions.assertThrows(
            NotAllowedOperationException.class,
            () ->
                ReflectionTestUtils.invokeMethod(
                    courseService, "validateStudentRemoving", course, student));

    Assertions.assertEquals(
        "Student with id 1111 is not registered for the course with id 1111.", thrown.getMessage());
  }

  @Test
  void Should_ThrowNotAllowedOperationExceptionOnValidateStudentLimit_When_IsExceeded() {

    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    List<Course> studentsCourses =
        List.of(
            Course.builder().build(),
            Course.builder().build(),
            Course.builder().build(),
            Course.builder().build(),
            Course.builder().build());

    when(courseRepository.findByStudentsId(student.getId()))
        .thenReturn(Optional.of(studentsCourses));

    NotAllowedOperationException thrown =
        Assertions.assertThrows(
            NotAllowedOperationException.class,
            () -> ReflectionTestUtils.invokeMethod(courseService, "validateStudentLimit", student));

    Assertions.assertEquals(
        "Student with id 1111 has reached the maximum number of course registrations.",
        thrown.getMessage());
  }

  @Test
  void Should_NotThrowOnValidateStudentLimit_When_IsNotExceeded() {
    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    List<Course> studentsCourses = List.of(Course.builder().build());

    when(courseRepository.findByStudentsId(student.getId()))
        .thenReturn(Optional.of(studentsCourses));

    ReflectionTestUtils.invokeMethod(courseService, "validateStudentLimit", student);
  }

  @Test
  void Should_NotThrowOnValidateStudentRemoving_When_IsRegistered() {

    User registeredStudent =
        User.builder()
            .id("2222")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .students(Collections.singletonList(registeredStudent))
            .build();

    ReflectionTestUtils.invokeMethod(
        courseService, "validateStudentRemoving", course, registeredStudent);
  }

  @Test
  void Should_GetLessonById_When_Exists() {
    Course course =
        Course.builder().id("1111").description("Test description").name("Test name").build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));

    CourseDto result = courseService.getById(course.getId());

    assertNotNull(result);
    assertEquals(course.getId(), result.getId());
    verify(courseRepository, times(1)).findById(any());
  }

  @Test
  void Should_getLessonsByCourseId_When_Exists() {

    Lesson lesson = Lesson.builder().id("1111").content("Test content").title("Test title").build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .lessons(Collections.singletonList(lesson))
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));

    List<LessonDto> result = courseService.getLessonsByCourseId(course.getId());

    assertNotNull(result);
    assertEquals(lesson.getId(), result.get(0).getId());
    verify(courseRepository, times(1)).findById(any());
  }

  @Test
  void Should_getStudentsByCourseId_When_Exists() {
    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Course course =
        Course.builder()
            .id("1111")
            .description("Test description")
            .name("Test name")
            .students(Collections.singletonList(student))
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.of(course));

    List<UserDto> result = courseService.getStudentsByCourseId(course.getId());

    assertNotNull(result);
    assertEquals(student.getId(), result.get(0).getId());
    verify(courseRepository, times(1)).findById(any());
  }

  @Test
  void Should_GetAll_When_Exist() {

    Course firstCourse =
        Course.builder().id("1111").description("Test description").name("Test name").build();

    Course secondCourse =
        Course.builder().id("2222").description("Test description").name("Test name").build();

    Course thirdCourse =
        Course.builder().id("3333").description("Test description").name("Test name").build();

    List<Course> courseList = List.of(firstCourse, secondCourse, thirdCourse);
    Page<Course> coursePage = new PageImpl<>(courseList);

    when(courseRepository.findAll(any(Pageable.class))).thenReturn(coursePage);

    Page<CourseDto> result = courseService.getAll(Pageable.unpaged());

    assertNotNull(result);
    assertEquals(result.getTotalElements(), courseList.size());
    verify(courseRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetById_When_DoesNotExist() {

    when(courseRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(EntityNotFoundException.class, () -> courseService.getById("1111"));

    Assertions.assertEquals("Course with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetStudentById_When_DoesNotExist() {

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> ReflectionTestUtils.invokeMethod(courseService, "getStudentById", "1111"));

    Assertions.assertEquals("Student with id: 1111 does not exist!", thrown.getMessage());
  }
}
