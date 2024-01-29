package com.sombra.cmsapi.businessservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CheckHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.service.CompletedHomeworkService;
import java.time.ZonedDateTime;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class CompletedHomeworkControllerTest {

  @Mock private CompletedHomeworkService completedHomeworkService;

  @InjectMocks private CompletedHomeworkController completedHomeworkController;

  @Test
  void Should_Create_When_ValidRequest() {
    // SETUP
    CreateCompletedHomeworkRequest createCompletedHomeworkRequest =
        CreateCompletedHomeworkRequest.builder().homeworkId("1").studentId("1").build();

    MultipartFile file =
        new MockMultipartFile("testFile", "testFile.txt", "text/plain", "Hello, World!".getBytes());

    CompletedHomeworkDto expectedCompletedHomeworkDto =
        CompletedHomeworkDto.builder()
            .id("1")
            .student(mock(User.class))
            .homework(mock(Homework.class))
            .mark(100)
            .submissionDate(ZonedDateTime.now())
            .build();

    when(completedHomeworkService.save(createCompletedHomeworkRequest, file))
        .thenReturn(expectedCompletedHomeworkDto);

    // ACT
    ResponseEntity<CompletedHomeworkDto> responseEntity =
        completedHomeworkController.create(createCompletedHomeworkRequest, file);

    // VERIFY
    verify(completedHomeworkService, times(1)).save(createCompletedHomeworkRequest, file);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(expectedCompletedHomeworkDto, responseEntity.getBody());
  }

  @Test
  void Should_GetById_When_ValidRequest() {
    // SETUP
    CompletedHomeworkDto expectedCompletedHomeworkDto =
        CompletedHomeworkDto.builder()
            .id("1")
            .student(mock(User.class))
            .homework(mock(Homework.class))
            .mark(100)
            .submissionDate(ZonedDateTime.now())
            .build();

    when(completedHomeworkService.getById("1")).thenReturn(expectedCompletedHomeworkDto);

    // ACT
    ResponseEntity<CompletedHomeworkDto> responseEntity = completedHomeworkController.getById("1");

    // VERIFY
    verify(completedHomeworkService, times(1)).getById("1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCompletedHomeworkDto, responseEntity.getBody());
  }

  @Test
  void Should_GetAll_When_ValidRequest() {
    // SETUP
    CompletedHomeworkDto expectedCompletedHomeworkDtoFirst =
        CompletedHomeworkDto.builder()
            .id("1")
            .student(mock(User.class))
            .homework(mock(Homework.class))
            .mark(100)
            .submissionDate(ZonedDateTime.now())
            .build();

    CompletedHomeworkDto expectedCompletedHomeworkDtoSecond =
        CompletedHomeworkDto.builder()
            .id("1")
            .student(mock(User.class))
            .homework(mock(Homework.class))
            .mark(100)
            .submissionDate(ZonedDateTime.now())
            .build();

    Page<CompletedHomeworkDto> pageCompletedHomework =
        new PageImpl<>(
            List.of(expectedCompletedHomeworkDtoFirst, expectedCompletedHomeworkDtoSecond));

    when(completedHomeworkService.getAll(any())).thenReturn(pageCompletedHomework);

    // ACT
    ResponseEntity<Page<CompletedHomeworkDto>> responseEntity =
        completedHomeworkController.getAll(Pageable.unpaged());

    // VERIFY
    verify(completedHomeworkService, times(1)).getAll(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(pageCompletedHomework, responseEntity.getBody());
  }

  @Test
  void Should_CheckHomework_When_ValidRequest() {
    // SETUP
    CheckHomeworkRequest checkHomeworkRequest =
        CheckHomeworkRequest.builder().mark(100).instructorId("1").comment("Comment").build();

    CompletedHomeworkDto expectedCompletedHomeworkDto =
        CompletedHomeworkDto.builder()
            .id("1")
            .student(mock(User.class))
            .instructor(mock(User.class))
            .reviewDate(ZonedDateTime.now())
            .homework(mock(Homework.class))
            .mark(100)
            .submissionDate(ZonedDateTime.now())
            .build();

    when(completedHomeworkService.checkHomework("1", checkHomeworkRequest))
        .thenReturn(expectedCompletedHomeworkDto);

    // ACT
    ResponseEntity<CompletedHomeworkDto> responseEntity =
        completedHomeworkController.checkHomework("1", checkHomeworkRequest);

    // VERIFY
    verify(completedHomeworkService, times(1)).checkHomework("1", checkHomeworkRequest);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCompletedHomeworkDto, responseEntity.getBody());
  }
}
