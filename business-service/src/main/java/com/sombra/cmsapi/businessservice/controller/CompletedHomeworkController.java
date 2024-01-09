package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.service.CompletedHomeworkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/completed-homeworks")
@AllArgsConstructor
public class CompletedHomeworkController {

  private final CompletedHomeworkService completedHomeworkService;

  @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
  @PostMapping
  public ResponseEntity<CompletedHomeworkDto> create(
      @Valid @RequestPart("request") CreateCompletedHomeworkRequest requestDto,@NotEmpty @RequestPart("file")
  MultipartFile file) {
    return new ResponseEntity<>(completedHomeworkService.save(requestDto, file), HttpStatus.OK);
  }
}
