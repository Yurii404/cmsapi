package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CheckHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.BrokenFileException;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.exception.NotAllowedOperationException;
import com.sombra.cmsapi.businessservice.repository.CompletedHomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.HomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.io.IOException;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CompletedHomeworkServiceTest {

  @Mock private CompletedHomeworkRepository completedHomeworkRepository;
  @Mock private UserRepository userRepository;
  @Mock private HomeworkRepository homeworkRepository;
  @Mock private HomeworkFileService homeworkFileService;
  @InjectMocks private CompletedHomeworkService completedHomeworkService;

  @Test
  void Should_Save_When_ValidData() throws IOException {
    // SETUP
    String testUrl = "https://test.url";
    MultipartFile file =
        new MockMultipartFile("testFile", "testFile.txt", "text/plain", "Hello, World!".getBytes());
    CreateCompletedHomeworkRequest request =
        CreateCompletedHomeworkRequest.builder().homeworkId("1111").studentId("1111").build();
    User student = User.builder().id("1111").role(UserRole.STUDENT).build();
    Course course =
        Course.builder().id("1111").students(Collections.singletonList(student)).build();
    Lesson lesson = Lesson.builder().id("1111").course(course).build();
    Homework homework = Homework.builder().id("1111").lesson(lesson).build();

    when(homeworkRepository.findById(any())).thenReturn(Optional.of(homework));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(student));
    when(homeworkFileService.saveHomeworkFileToS3(any(), any(), any())).thenReturn(testUrl);

    ArgumentCaptor<CompletedHomework> argumentCaptor =
        ArgumentCaptor.forClass(CompletedHomework.class);

    // ACT
    completedHomeworkService.save(request, file);

    // VERIFY
    verify(homeworkRepository, times(1)).findById(any());
    verify(homeworkFileService, times(1)).saveHomeworkFileToS3(any(), any(), any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(completedHomeworkRepository, times(1)).save(argumentCaptor.capture());
    assertEquals(request.getHomeworkId(), argumentCaptor.getValue().getHomework().getId());
    assertEquals(request.getStudentId(), argumentCaptor.getValue().getStudent().getId());
    assertEquals(testUrl, argumentCaptor.getValue().getHomeworkFileUrl());
  }

  @Test
  void Should_ThrowBrokenFileExceptionOnSave_When_SomethingWentWrongWithUpload()
      throws IOException {
    // SETUP
    MultipartFile file =
        new MockMultipartFile("testFile", "testFile.txt", "text/plain", "Hello, World!".getBytes());
    CreateCompletedHomeworkRequest request =
        CreateCompletedHomeworkRequest.builder().homeworkId("1111").studentId("1111").build();
    User student = User.builder().id("1111").role(UserRole.STUDENT).build();
    Homework homework = Homework.builder().id("1111").build();

    when(homeworkRepository.findById(any())).thenReturn(Optional.of(homework));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(student));
    when(homeworkFileService.saveHomeworkFileToS3(any(), any(), any()))
        .thenThrow(new IOException("Something went wrong!"));

    // ACT
    BrokenFileException thrown =
        Assertions.assertThrows(
            BrokenFileException.class, () -> completedHomeworkService.save(request, file));

    // VERIFY
    Assertions.assertEquals("Something went wrong when reading file.", thrown.getMessage());
  }

  @Test
  void Should_CheckHomework_When_ValidData() {
    // SETUP
    CheckHomeworkRequest request =
        CheckHomeworkRequest.builder().mark(100).instructorId("1111").comment("Nice Job!").build();
    User instructor = User.builder().id("1111").build();

    Course course =
        Course.builder().id("1111").instructors(Collections.singletonList(instructor)).build();

    Lesson lesson = Lesson.builder().id("1111").course(course).build();
    Homework homework = Homework.builder().id("1111").lesson(lesson).build();
    CompletedHomework completedHomework = CompletedHomework.builder().homework(homework).build();

    when(completedHomeworkRepository.findById(any())).thenReturn(Optional.of(completedHomework));
    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(instructor));
    ArgumentCaptor<CompletedHomework> argumentCaptor =
        ArgumentCaptor.forClass(CompletedHomework.class);

    // ACT
    completedHomeworkService.checkHomework("1111", request);

    // VERIFY
    verify(completedHomeworkRepository, times(1)).findById(any());
    verify(userRepository, times(1)).findByIdAndRole(any(), any());
    verify(completedHomeworkRepository, times(1)).save(argumentCaptor.capture());
    assertEquals(request.getInstructorId(), argumentCaptor.getValue().getInstructor().getId());
    assertEquals(request.getMark(), argumentCaptor.getValue().getMark());
    assertEquals(request.getComment(), argumentCaptor.getValue().getComment());
  }

  @Test
  void Should_NotThrowOnValidateInstructorPermissions_When_IsTeacher() {
    // SETUP
    User instructor = User.builder().id("1111").build();

    Course course =
        Course.builder().id("1111").instructors(Collections.singletonList(instructor)).build();

    Lesson lesson = Lesson.builder().id("1111").course(course).build();
    Homework homework = Homework.builder().id("1111").lesson(lesson).build();
    CompletedHomework completedHomework = CompletedHomework.builder().homework(homework).build();

    // ACT
    ReflectionTestUtils.invokeMethod(
        completedHomeworkService, "validateInstructorPermissions", instructor, completedHomework);
  }

  @Test
  void Should_ThrowNotAllowedOperationExceptionOnValidateInstructorPermissions_When_DoNotTeach() {
    // SETUP
    User instructor = User.builder().id("1111").build();

    Course course = Course.builder().id("1111").instructors(new ArrayList<>()).build();

    Lesson lesson = Lesson.builder().id("1111").course(course).build();
    Homework homework = Homework.builder().id("1111").lesson(lesson).build();
    CompletedHomework completedHomework = CompletedHomework.builder().homework(homework).build();

    // ACT
    NotAllowedOperationException thrown =
        Assertions.assertThrows(
            NotAllowedOperationException.class,
            () ->
                ReflectionTestUtils.invokeMethod(
                    completedHomeworkService,
                    "validateInstructorPermissions",
                    instructor,
                    completedHomework));

    // VERIFY
    Assertions.assertEquals(
        "Instructor with id 1111 does not teaches the course this homework belongs.",
        thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetHomeworkById_When_DoesNotExist() {
    // SETUP
    String homeworkId = "nonExistentId";

    when(homeworkRepository.findById(any())).thenReturn(Optional.empty());

    // ACT
    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(
                  completedHomeworkService, "getHomeworkById", homeworkId);
            });

    // VERIFY
    Assertions.assertEquals("Homework with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetHomeworkById_When_Exists() {
    // SETUP
    String homeworkId = "nonExistentId";
    Homework homework = Homework.builder().id("1111").build();

    when(homeworkRepository.findById(any())).thenReturn(Optional.of(homework));

    // ACT
    Homework result =
        ReflectionTestUtils.invokeMethod(completedHomeworkService, "getHomeworkById", homeworkId);

    // VERIFY
    assertEquals(result, homework);
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetInstructorById_When_DoesNotExist() {
    // SETUP
    String instructorId = "nonExistentId";

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.empty());

    // ACT
    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(
                  completedHomeworkService, "getInstructorById", instructorId);
            });

    // VERIFY
    Assertions.assertEquals(
        "Instructor with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetInstructorById_When_Exists() {
    // SETUP
    String instructorId = "nonExistentId";
    User instructor = User.builder().id("1111").role(UserRole.INSTRUCTOR).build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(instructor));

    // ACT
    User result =
        ReflectionTestUtils.invokeMethod(
            completedHomeworkService, "getInstructorById", instructorId);

    // VERIFY
    assertEquals(result, instructor);
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetStudentById_When_DoesNotExist() {
    // SETUP
    String studentId = "nonExistentId";

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.empty());

    // ACT
    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(
                  completedHomeworkService, "getStudentById", studentId);
            });

    // VERIFY
    Assertions.assertEquals("Student with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetStudentById_When_Exists() {
    // SETUP
    String studentId = "nonExistentId";
    User student = User.builder().id("1111").role(UserRole.STUDENT).build();

    when(userRepository.findByIdAndRole(any(), any())).thenReturn(Optional.of(student));

    // ACT
    User result =
        ReflectionTestUtils.invokeMethod(completedHomeworkService, "getStudentById", studentId);

    // VERIFY
    assertEquals(result, student);
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetCompletedHomeworkById_When_DoesNotExist() {
    // SETUP
    String completedHomeworkId = "nonExistentId";

    when(completedHomeworkRepository.findById(any())).thenReturn(Optional.empty());

    // ACT
    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              ReflectionTestUtils.invokeMethod(
                  completedHomeworkService, "getCompletedHomeworkById", completedHomeworkId);
            });

    // VERIFY
    Assertions.assertEquals(
        "Completed homework with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetCompletedHomeworkById_When_Exists() {
    // SETUP
    String completedHomeworkId = "nonExistentId";
    CompletedHomework completedHomework = CompletedHomework.builder().id("1111").build();

    when(completedHomeworkRepository.findById(any())).thenReturn(Optional.of(completedHomework));

    // ACT
    CompletedHomework result =
        ReflectionTestUtils.invokeMethod(
            completedHomeworkService, "getCompletedHomeworkById", completedHomeworkId);

    // VERIFY
    assertEquals(result, completedHomework);
  }

  @Test
  void Should_GetAll_When_Exist() {
    // SETUP
    CompletedHomework firstCompletedHomework = CompletedHomework.builder().id("1111").build();
    CompletedHomework secondCompletedHomework = CompletedHomework.builder().id("2222").build();
    CompletedHomework thirdCompletedHomework = CompletedHomework.builder().id("3333").build();

    List<CompletedHomework> completedHomeworkList =
        List.of(firstCompletedHomework, secondCompletedHomework, thirdCompletedHomework);
    Page<CompletedHomework> completedHomeworkPage = new PageImpl<>(completedHomeworkList);

    when(completedHomeworkRepository.findAll(any(Pageable.class)))
        .thenReturn(completedHomeworkPage);

    // ACT
    Page<CompletedHomeworkDto> result = completedHomeworkService.getAll(Pageable.unpaged());

    // VERIFY
    assertNotNull(result);
    assertEquals(result.getTotalElements(), completedHomeworkList.size());
    verify(completedHomeworkRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetById_When_DoesNotExist() {
    // SETUP
    String completedHomeworkId = "nonExistentId";

    when(completedHomeworkRepository.findById(any())).thenReturn(Optional.empty());

    // ACT
    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> completedHomeworkService.getById(completedHomeworkId));

    // VERIFY
    Assertions.assertEquals(
        "Completed homework with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetById_When_Exists() {
    // SETUP
    String completedHomeworkId = "nonExistentId";
    CompletedHomework completedHomework = CompletedHomework.builder().id("1111").build();

    when(completedHomeworkRepository.findById(any())).thenReturn(Optional.of(completedHomework));

    // ACT
    CompletedHomeworkDto result = completedHomeworkService.getById(completedHomeworkId);

    // VERIFY
    assertNotNull(result);
    assertEquals(result.getId(), completedHomework.getId());
  }
}
