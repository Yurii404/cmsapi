package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.homework.CreateHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.homework.HomeworkDto;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.repository.HomeworkRepository;
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
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class HomeworkServiceTest {

  @Mock private HomeworkRepository homeworkRepository;
  @Mock private LessonRepository lessonRepository;

  @InjectMocks private HomeworkService homeworkService;

  @Test
  void Should_Save_When_ValidData() {
    CreateHomeworkRequest request =
        CreateHomeworkRequest.builder().lessonId("1111").task("Test task").build();

    Lesson lesson = Lesson.builder().id("1111").content("Test content").title("Test title").build();

    when(lessonRepository.findById(any())).thenReturn(Optional.ofNullable(lesson));
    ArgumentCaptor<Homework> argumentCaptor = ArgumentCaptor.forClass(Homework.class);

    homeworkService.save(request);

    verify(lessonRepository, times(1)).findById(any());
    verify(homeworkRepository, times(1)).save(any());
    verify(homeworkRepository).save(argumentCaptor.capture());
    assertEquals(request.getLessonId(), argumentCaptor.getValue().getLesson().getId());
    assertEquals(request.getTask(), argumentCaptor.getValue().getTask());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnSave_When_LessonDoesNotExist() {
    CreateHomeworkRequest request =
        CreateHomeworkRequest.builder().lessonId("1111").task("Test task").build();

    when(lessonRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> {
              homeworkService.save(request);
            });

    Assertions.assertEquals("Lesson with id: 1111 does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetAll_When_Exist() {
    Lesson lesson = Lesson.builder().id("1111").content("Test content").title("Test title").build();

    Homework firstHomework = Homework.builder().id("1111").task("Test task").lesson(lesson).build();

    Homework secondHomework =
        Homework.builder().id("2222").task("Test task").lesson(lesson).build();

    Homework thirdHomework = Homework.builder().id("3333").task("Test task").lesson(lesson).build();

    List<Homework> homewrkList = List.of(firstHomework, secondHomework, thirdHomework);
    Page<Homework> homewrokPage = new PageImpl<>(homewrkList);

    when(homeworkRepository.findAll(any(Pageable.class))).thenReturn(homewrokPage);

    Page<HomeworkDto> result = homeworkService.getAll(Pageable.unpaged());

    assertNotNull(result);
    assertEquals(result.getTotalElements(), homewrkList.size());
    verify(homeworkRepository, times(1)).findAll(any(Pageable.class));
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetLessonById_When_DoesNotExist() {
    String lessonId = "nonExistentId";

    when(lessonRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class,
            () -> ReflectionTestUtils.invokeMethod(homeworkService, "getLessonById", lessonId));

    Assertions.assertEquals("Lesson with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_ThrowEntityNotFoundExceptionOnGetById_When_DoesNotExist() {
    String homeworkId = "nonExistentId";

    when(homeworkRepository.findById(any())).thenReturn(Optional.empty());

    EntityNotFoundException thrown =
        Assertions.assertThrows(
            EntityNotFoundException.class, () -> homeworkService.getById(homeworkId));

    Assertions.assertEquals("Homework with id: nonExistentId does not exist!", thrown.getMessage());
  }

  @Test
  void Should_GetById_When_Exists() {
    String homeworkId = "1111";
    Lesson lesson = Lesson.builder().id("1111").content("Test content").title("Test title").build();
    Homework homework = Homework.builder().id("1111").task("Test task").lesson(lesson).build();

    when(homeworkRepository.findById(any())).thenReturn(Optional.of(homework));

    HomeworkDto result = homeworkService.getById(homeworkId);

    assertNotNull(result);
    assertEquals(homework.getId(), result.getId());
    verify(homeworkRepository, times(1)).findById(any());
  }
}
