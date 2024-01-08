package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.lesson.CreateLessonRequest;
import com.sombra.cmsapi.businessservice.dto.lesson.LessonDto;
import com.sombra.cmsapi.businessservice.dto.lesson.UpdateLessonRequest;
import com.sombra.cmsapi.businessservice.service.LessonService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/lessons")
@AllArgsConstructor
public class LessonController {

  private final LessonService lessonService;

  @PostMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<LessonDto> create(@Valid @RequestBody CreateLessonRequest requestDto) {
    return new ResponseEntity<>(lessonService.save(requestDto), HttpStatus.OK);
  }

  @PutMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<LessonDto> update(@Valid @RequestBody UpdateLessonRequest requestDto) {
    return new ResponseEntity<>(lessonService.update(requestDto), HttpStatus.OK);
  }

  @GetMapping
  @PreAuthorize("hasAuthority('ADMIN')")
  public ResponseEntity<List<LessonDto>> getAll() {
    return new ResponseEntity<>(lessonService.getAll(), HttpStatus.OK);
  }
}
