package com.sombra.cmsapi.businessservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.homework.CreateHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.homework.HomeworkDto;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.service.HomeworkService;
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
class HomeworkControllerTest {

  @Mock private HomeworkService homeworkService;

  @InjectMocks private HomeworkController homeworkController;

  @Test
  void Should_Create_When_ValidRequest() {
    // SETUP
    CreateHomeworkRequest createHomeworkRequest =
        CreateHomeworkRequest.builder().lessonId("1").task("Task").build();

    HomeworkDto expectedHomeworkDto =
        HomeworkDto.builder().id("1").lesson(mock(Lesson.class)).task("Task").build();

    when(homeworkService.save(createHomeworkRequest)).thenReturn(expectedHomeworkDto);

    // ACT
    ResponseEntity<HomeworkDto> responseEntity = homeworkController.create(createHomeworkRequest);

    // VERIFY
    verify(homeworkService, times(1)).save(createHomeworkRequest);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(expectedHomeworkDto, responseEntity.getBody());
  }

  @Test
  void Should_GetById_When_ValidRequest() {
    // SETUP
    HomeworkDto expectedHomeworkDto =
        HomeworkDto.builder().id("1").lesson(mock(Lesson.class)).task("Task").build();

    when(homeworkService.getById("1")).thenReturn(expectedHomeworkDto);

    // ACT
    ResponseEntity<HomeworkDto> responseEntity = homeworkController.getById("1");

    // VERIFY
    verify(homeworkService, times(1)).getById("1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedHomeworkDto, responseEntity.getBody());
  }

  @Test
  void Should_GetAll_When_ValidRequest() {
    // SETUP
    HomeworkDto expectedHomeworkDtoFirst =
        HomeworkDto.builder().id("1").lesson(mock(Lesson.class)).task("Task").build();

    HomeworkDto expectedHomeworkDtoSecond =
        HomeworkDto.builder().id("2").lesson(mock(Lesson.class)).task("Task").build();

    Page<HomeworkDto> pageHomework =
        new PageImpl<>(List.of(expectedHomeworkDtoFirst, expectedHomeworkDtoSecond));

    when(homeworkService.getAll(any())).thenReturn(pageHomework);

    // ACT
    ResponseEntity<Page<HomeworkDto>> responseEntity =
        homeworkController.getAll(Pageable.unpaged());

    // VERIFY
    verify(homeworkService, times(1)).getAll(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(pageHomework, responseEntity.getBody());
  }
}
