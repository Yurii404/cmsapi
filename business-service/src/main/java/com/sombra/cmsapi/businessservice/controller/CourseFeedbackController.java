package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.courseFeedback.CourseFeedbackDto;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CreateCourseFeedbackRequest;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.LeaveCommentRequest;
import com.sombra.cmsapi.businessservice.service.CourseFeedbackService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
  public ResponseEntity<CourseFeedbackDto> create(
      @Valid @RequestBody CreateCourseFeedbackRequest requestDto) {
    return new ResponseEntity<>(courseFeedbackService.save(requestDto), HttpStatus.CREATED);
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
  @PutMapping("/{courseFeedbackId}")
  public ResponseEntity<CourseFeedbackDto> leaveFeedback(
      @PathVariable String courseFeedbackId, @Valid @RequestBody LeaveCommentRequest requestDto) {
    return new ResponseEntity<>(
        courseFeedbackService.leaveFeedback(courseFeedbackId, requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public ResponseEntity<Page<CourseFeedbackDto>> getAll(Pageable pageable) {
    return new ResponseEntity<>(courseFeedbackService.getAll(pageable), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<CourseFeedbackDto> getById(@PathVariable String id) {
    return new ResponseEntity<>(courseFeedbackService.getById(id), HttpStatus.OK);
  }
}
