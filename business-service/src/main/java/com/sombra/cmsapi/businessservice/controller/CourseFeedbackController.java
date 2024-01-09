package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.course.AssignInstructorRequest;
import com.sombra.cmsapi.businessservice.dto.course.CourseDto;
import com.sombra.cmsapi.businessservice.dto.course.CreateCourseRequest;
import com.sombra.cmsapi.businessservice.dto.course.WithdrawInstructorRequest;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CourseFeedbackDto;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CreateCourseFeedbackRequest;
import com.sombra.cmsapi.businessservice.service.CourseFeedbackService;
import com.sombra.cmsapi.businessservice.service.CourseService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course-feedbacks")
@AllArgsConstructor
public class CourseFeedbackController {

  private final CourseFeedbackService courseFeedbackService;

  @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR','STUDENT')")
  @PostMapping
  public ResponseEntity<CourseFeedbackDto> create(@Valid @RequestBody CreateCourseFeedbackRequest requestDto) {
    return new ResponseEntity<>(courseFeedbackService.save(requestDto), HttpStatus.OK);
  }


}
