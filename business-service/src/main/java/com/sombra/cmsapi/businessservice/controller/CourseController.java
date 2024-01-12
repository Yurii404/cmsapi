package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.course.CourseDto;
import com.sombra.cmsapi.businessservice.dto.course.CreateCourseRequest;
import com.sombra.cmsapi.businessservice.mapper.CourseMapper;
import com.sombra.cmsapi.businessservice.service.CourseService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
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
@RequestMapping("/courses")
@AllArgsConstructor
public class CourseController {

  private final CourseService courseService;
  private final CourseMapper courseMapper = CourseMapper.INSTANCE;

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public ResponseEntity<CourseDto> create(@Valid @RequestBody CreateCourseRequest requestDto) {
    return new ResponseEntity<>(courseService.save(requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public ResponseEntity<List<CourseDto>> getAll() {
    return new ResponseEntity<>(
        courseService.getAll().stream().map(courseMapper::courseToCourseDto).toList(),
        HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<CourseDto> getById(@PathVariable String id) {
    return new ResponseEntity<>(
        courseMapper.courseToCourseDto(courseService.getById(id)), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{courseId}/instructors/assign/{instructorId}")
  public ResponseEntity<CourseDto> assignInstructor(
      @PathVariable String courseId, @PathVariable String instructorId) {
    return new ResponseEntity<>(
        courseService.assignInstructor(courseId, instructorId), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @PutMapping("/{courseId}/instructors/withdraw/{instructorId}")
  public ResponseEntity<CourseDto> withdrawInstructor(
      @PathVariable String courseId, @PathVariable String instructorId) {
    return new ResponseEntity<>(
        courseService.withdrawInstructor(courseId, instructorId), HttpStatus.OK);
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'INSTRUCTOR')")
  @PutMapping("/{courseId}/students/register/{studentId}")
  public ResponseEntity<CourseDto> registerStudent(
      @PathVariable String courseId, @PathVariable String studentId) {
    return new ResponseEntity<>(courseService.registerStudent(courseId, studentId), HttpStatus.OK);
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT', 'INSTRUCTOR')")
  @PutMapping("/{courseId}/students/remove/{studentId}")
  public ResponseEntity<CourseDto> removeStudent(
      @PathVariable String courseId, @PathVariable String studentId) {
    return new ResponseEntity<>(courseService.removeStudent(courseId, studentId), HttpStatus.OK);
  }
}
