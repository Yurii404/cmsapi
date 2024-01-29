package com.sombra.cmsapi.businessservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.lesson.CreateLessonRequest;
import com.sombra.cmsapi.businessservice.dto.lesson.LessonDto;
import com.sombra.cmsapi.businessservice.dto.lesson.UpdateLessonRequest;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.service.LessonService;
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
class LessonControllerTest {

  @Mock private LessonService lessonService;

  @InjectMocks private LessonController lessonController;

  @Test
  void Should_Create_When_ValidRequest() {
    // SETUP
    CreateLessonRequest createLessonRequest =
        CreateLessonRequest.builder().title("Title").content("Content").courseId("1").build();

    LessonDto expectedLessonDto =
        LessonDto.builder()
            .id("1")
            .title("Title")
            .content("Content")
            .course(mock(Course.class))
            .build();

    when(lessonService.save(createLessonRequest)).thenReturn(expectedLessonDto);

    // ACT
    ResponseEntity<LessonDto> responseEntity = lessonController.create(createLessonRequest);

    // VERIFY
    verify(lessonService, times(1)).save(createLessonRequest);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(expectedLessonDto, responseEntity.getBody());
  }

  @Test
  void Should_Update_When_ValidRequest() {
    // SETUP
    UpdateLessonRequest updateLessonRequest =
        UpdateLessonRequest.builder()
            .id("1")
            .title("Title")
            .content("Content")
            .courseId("1")
            .build();

    LessonDto expectedLessonDto =
        LessonDto.builder()
            .id("1")
            .title("Title")
            .content("Content")
            .course(mock(Course.class))
            .build();

    when(lessonService.update(updateLessonRequest)).thenReturn(expectedLessonDto);

    // ACT
    ResponseEntity<LessonDto> responseEntity = lessonController.update(updateLessonRequest);

    // VERIFY
    verify(lessonService, times(1)).update(updateLessonRequest);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedLessonDto, responseEntity.getBody());
  }

  @Test
  void Should_GetById_When_ValidRequest() {
    // SETUP
    LessonDto expectedLessonDto =
        LessonDto.builder()
            .id("1")
            .title("Title")
            .content("Content")
            .course(mock(Course.class))
            .build();

    when(lessonService.getById("1")).thenReturn(expectedLessonDto);

    // ACT
    ResponseEntity<LessonDto> responseEntity = lessonController.getById("1");

    // VERIFY
    verify(lessonService, times(1)).getById("1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedLessonDto, responseEntity.getBody());
  }

  @Test
  void Should_GetAll_When_ValidRequest() {
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

    Page<LessonDto> pageLesson =
        new PageImpl<>(List.of(expectedLessonDtoFirst, expectedLessonDtoSecond));

    when(lessonService.getAll(any())).thenReturn(pageLesson);

    // ACT
    ResponseEntity<Page<LessonDto>> responseEntity = lessonController.getAll(Pageable.unpaged());

    // VERIFY
    verify(lessonService, times(1)).getAll(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(pageLesson, responseEntity.getBody());
  }
}
