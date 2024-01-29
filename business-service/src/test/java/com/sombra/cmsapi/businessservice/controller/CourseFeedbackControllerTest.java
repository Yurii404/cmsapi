package com.sombra.cmsapi.businessservice.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.courseFeedback.CourseFeedbackDto;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CreateCourseFeedbackRequest;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.LeaveCommentRequest;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.service.CourseFeedbackService;
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
class CourseFeedbackControllerTest {

  @Mock private CourseFeedbackService courseFeedbackService;

  @InjectMocks private CourseFeedbackController courseFeedbackController;

  @Test
  void Should_Create_When_ValidRequest() {
    // SETUP
    CreateCourseFeedbackRequest createCourseFeedbackRequest =
        CreateCourseFeedbackRequest.builder().courseId("1").studentId("1").build();

    CourseFeedbackDto expectedCourseFeedbackDto =
        CourseFeedbackDto.builder()
            .id("1")
            .course(mock(Course.class))
            .student(mock(User.class))
            .build();

    when(courseFeedbackService.save(createCourseFeedbackRequest))
        .thenReturn(expectedCourseFeedbackDto);

    // ACT
    ResponseEntity<CourseFeedbackDto> responseEntity =
        courseFeedbackController.create(createCourseFeedbackRequest);

    // VERIFY
    verify(courseFeedbackService, times(1)).save(createCourseFeedbackRequest);
    assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
    assertEquals(expectedCourseFeedbackDto, responseEntity.getBody());
  }

  @Test
  void Should_GetById_When_ValidRequest() {
    // SETUP
    CourseFeedbackDto expectedCourseFeedbackDto =
        CourseFeedbackDto.builder()
            .id("1")
            .course(mock(Course.class))
            .student(mock(User.class))
            .build();

    when(courseFeedbackService.getById("1")).thenReturn(expectedCourseFeedbackDto);

    // ACT
    ResponseEntity<CourseFeedbackDto> responseEntity = courseFeedbackController.getById("1");

    // VERIFY
    verify(courseFeedbackService, times(1)).getById("1");
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCourseFeedbackDto, responseEntity.getBody());
  }

  @Test
  void Should_GetAll_When_ValidRequest() {
    // SETUP
    CourseFeedbackDto expectedCourseFeedbackDtoFirst =
        CourseFeedbackDto.builder()
            .id("1")
            .course(mock(Course.class))
            .student(mock(User.class))
            .build();

    CourseFeedbackDto expectedCourseFeedbackDtoSecond =
        CourseFeedbackDto.builder()
            .id("2")
            .course(mock(Course.class))
            .student(mock(User.class))
            .build();

    Page<CourseFeedbackDto> pageCourseFeedback =
        new PageImpl<>(List.of(expectedCourseFeedbackDtoFirst, expectedCourseFeedbackDtoSecond));

    when(courseFeedbackService.getAll(any())).thenReturn(pageCourseFeedback);

    // ACT
    ResponseEntity<Page<CourseFeedbackDto>> responseEntity =
        courseFeedbackController.getAll(Pageable.unpaged());

    // VERIFY
    verify(courseFeedbackService, times(1)).getAll(any());
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(pageCourseFeedback, responseEntity.getBody());
  }

  @Test
  void Should_LeaveFeedback_When_ValidRequest() {
    // SETUP
    LeaveCommentRequest leaveCommentRequest =
        LeaveCommentRequest.builder().instructorId("1").comment("Nice").build();

    CourseFeedbackDto expectedCourseFeedbackDto =
        CourseFeedbackDto.builder()
            .id("1")
            .course(mock(Course.class))
            .student(mock(User.class))
            .build();

    when(courseFeedbackService.leaveFeedback("1", leaveCommentRequest))
        .thenReturn(expectedCourseFeedbackDto);

    // ACT
    ResponseEntity<CourseFeedbackDto> responseEntity =
        courseFeedbackController.leaveFeedback("1", leaveCommentRequest);

    // VERIFY
    verify(courseFeedbackService, times(1)).leaveFeedback("1", leaveCommentRequest);
    assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
    assertEquals(expectedCourseFeedbackDto, responseEntity.getBody());
  }
}
