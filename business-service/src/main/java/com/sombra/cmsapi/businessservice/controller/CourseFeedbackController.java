package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.courseFeedback.CourseFeedbackDto;
import com.sombra.cmsapi.businessservice.dto.courseFeedback.CreateCourseFeedbackRequest;
import com.sombra.cmsapi.businessservice.mapper.CourseFeedbackMapper;
import com.sombra.cmsapi.businessservice.service.CourseFeedbackService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/course-feedbacks")
@AllArgsConstructor
public class CourseFeedbackController {

  private final CourseFeedbackService courseFeedbackService;
  private final CourseFeedbackMapper courseFeedbackMapper = CourseFeedbackMapper.INSTANCE;

  @PreAuthorize("hasAnyAuthority('ADMIN','INSTRUCTOR','STUDENT')")
  @PostMapping
  public ResponseEntity<CourseFeedbackDto> create(
      @Valid @RequestBody CreateCourseFeedbackRequest requestDto) {
    return new ResponseEntity<>(courseFeedbackService.save(requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public ResponseEntity<List<CourseFeedbackDto>> getAll() {
    return new ResponseEntity<>(
        courseFeedbackService.getAll().stream()
            .map(courseFeedbackMapper::courseFeedbackToCourseFeedbackDto)
            .toList(),
        HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<CourseFeedbackDto> getById(@PathVariable String id) {
    return new ResponseEntity<>(
        courseFeedbackMapper.courseFeedbackToCourseFeedbackDto(courseFeedbackService.getById(id)),
        HttpStatus.OK);
  }
}
