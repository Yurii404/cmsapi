package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.lesson.CreateLessonRequest;
import com.sombra.cmsapi.businessservice.dto.lesson.LessonDto;
import com.sombra.cmsapi.businessservice.dto.lesson.UpdateLessonRequest;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import com.sombra.cmsapi.businessservice.repository.LessonRepository;
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

@ExtendWith(MockitoExtension.class)
class LessonServiceTest {

  @Mock private LessonRepository lessonRepository;
  @Mock private CourseRepository courseRepository;
  @InjectMocks private LessonService lessonService;

  @Test
  void Should_Save_When_ValidData_And_CourseIsPassedAndExists() {
    CreateLessonRequest request =
        CreateLessonRequest.builder()
            .courseId("1111")
            .content("Test content")
            .title("Test tittle")
            .build();

    Course course =
        Course.builder().id("1111").name("Test name").description("Test description").build();

    Lesson savedLesson =
        Lesson.builder().id("1").content("Test content").title("Test title").course(course).build();

    when(courseRepository.findById(any())).thenReturn(Optional.ofNullable(course));
    when(lessonRepository.save(any())).thenReturn(savedLesson);
    ArgumentCaptor<Lesson> argumentCaptor = ArgumentCaptor.forClass(Lesson.class);

    lessonService.save(request);

    verify(lessonRepository, times(1)).save(any());
    verify(lessonRepository).save(argumentCaptor.capture());
    assertEquals(request.getContent(), argumentCaptor.getValue().getContent());
    assertEquals(request.getTitle(), argumentCaptor.getValue().getTitle());
    assertEquals(request.getCourseId(), argumentCaptor.getValue().getCourse().getId());
  }

  @Test
  void Should_Save_When_ValidData_And_CourseIsNotPassed() {
    CreateLessonRequest request =
        CreateLessonRequest.builder().content("Test content").title("Test tittle").build();

    Lesson savedLesson =
        Lesson.builder().id("1").content("Test content").title("Test title").build();

    when(lessonRepository.save(any())).thenReturn(savedLesson);
    ArgumentCaptor<Lesson> argumentCaptor = ArgumentCaptor.forClass(Lesson.class);

    lessonService.save(request);

    verify(lessonRepository, times(1)).save(any());
    verify(lessonRepository).save(argumentCaptor.capture());
    assertEquals(request.getContent(), argumentCaptor.getValue().getContent());
    assertEquals(request.getTitle(), argumentCaptor.getValue().getTitle());
  }

  @Test
  void Should_Update_When_ValidData_And_CourseIsNotPassed() {
    UpdateLessonRequest request =
        UpdateLessonRequest.builder()
            .id("1111")
            .content("Test content updated")
            .title("Test tittle updated")
            .build();

    Lesson lessonToUpdate =
        Lesson.builder().id("1111").content("Test content").title("Test title").build();

    when(lessonRepository.findById(any())).thenReturn(Optional.ofNullable(lessonToUpdate));
    ArgumentCaptor<Lesson> argumentCaptor = ArgumentCaptor.forClass(Lesson.class);

    lessonService.update(request);

    verify(lessonRepository).save(argumentCaptor.capture());
    verify(lessonRepository, times(1)).save(any());
    assertEquals(request.getId(), argumentCaptor.getValue().getId());
    assertEquals(request.getContent(), argumentCaptor.getValue().getContent());
    assertEquals(request.getTitle(), argumentCaptor.getValue().getTitle());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnUpdate_When_CourseIsPassedAndNotDoesNotExist() {
    UpdateLessonRequest request =
        UpdateLessonRequest.builder()
            .id("1111")
            .content("Test content updated")
            .title("Test tittle updated")
            .courseId("1111")
            .build();

    Lesson lessonToUpdate =
        Lesson.builder().id("1111").content("Test content").title("Test title").build();

    when(lessonRepository.findById(any())).thenReturn(Optional.ofNullable(lessonToUpdate));
    when(courseRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              lessonService.update(request);
            });

    Assertions.assertEquals("Course with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnUpdate_When_LessonDoesNotExist() {
    UpdateLessonRequest request =
        UpdateLessonRequest.builder()
            .id("1111")
            .content("Test content updated")
            .title("Test tittle updated")
            .build();

    when(lessonRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              lessonService.update(request);
            });

    Assertions.assertEquals("Lesson with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_Update_When_ValidData_And_CourseIsPassedAndExists() {
    UpdateLessonRequest request =
        UpdateLessonRequest.builder()
            .id("1111")
            .content("Test content updated")
            .title("Test tittle updated")
            .courseId("1111")
            .build();

    Course course =
        Course.builder().id("1111").name("Test name").description("Test description").build();

    Lesson lessonToUpdate =
        Lesson.builder().id("1111").content("Test content").title("Test title").build();

    when(lessonRepository.findById(any())).thenReturn(Optional.ofNullable(lessonToUpdate));
    when(courseRepository.findById(any())).thenReturn(Optional.ofNullable(course));
    ArgumentCaptor<Lesson> argumentCaptor = ArgumentCaptor.forClass(Lesson.class);

    lessonService.update(request);

    verify(lessonRepository).save(argumentCaptor.capture());
    verify(lessonRepository, times(1)).save(any());
    assertEquals(request.getId(), argumentCaptor.getValue().getId());
    assertEquals(request.getContent(), argumentCaptor.getValue().getContent());
    assertEquals(request.getTitle(), argumentCaptor.getValue().getTitle());
    assertEquals(request.getCourseId(), argumentCaptor.getValue().getCourse().getId());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnSave_When_CourseIsPassedAndNotDoesNotExist() {
    CreateLessonRequest request =
        CreateLessonRequest.builder()
            .courseId("1111")
            .content("Test content")
            .title("Test tittle")
            .build();

    when(courseRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              lessonService.save(request);
            });

    Assertions.assertEquals("Course with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetAll_When_Exist() {
    Lesson firstLesson =
        Lesson.builder().id("1").content("Test content").title("Test title").build();

    Lesson secondLesson =
        Lesson.builder().id("2").content("Test content").title("Test title").build();

    Lesson thirdLesson =
        Lesson.builder().id("3").content("Test content").title("Test title").build();

    List<Lesson> homewrokList = List.of(firstLesson, secondLesson, thirdLesson);
    Page<Lesson> homewrokpage = new PageImpl<>(homewrokList);

    when(lessonRepository.findAll(any(Pageable.class))).thenReturn(homewrokpage);

    Page<LessonDto> result = lessonService.getAll(Pageable.unpaged());

    assertNotNull(result);
    assertEquals(result.getTotalElements(), homewrokList.size());
    verify(lessonRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void Should_GetById_When_Exists() {
    String lessonId = "1";
    Lesson lessonFromRepository =
        Lesson.builder().id(lessonId).content("Test content").title("Test title").build();

    when(lessonRepository.findById(any())).thenReturn(Optional.of(lessonFromRepository));

    LessonDto result = lessonService.getById(lessonId);

    assertNotNull(result);
    assertEquals(lessonFromRepository.getId(), result.getId());
    verify(lessonRepository, times(1)).findById(any());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetById_When_DoesNotExist() {
    String lessonId = "nonExistentId";

    when(lessonRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              lessonService.getById(lessonId);
            });

    Assertions.assertEquals("Lesson with id: nonExistentId does not exist!", thrown.getMessage());
  }
}
