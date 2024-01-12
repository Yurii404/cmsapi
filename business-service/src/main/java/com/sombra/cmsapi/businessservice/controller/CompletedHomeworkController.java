package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CheckHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.mapper.CompletedHomeworkMapper;
import com.sombra.cmsapi.businessservice.service.CompletedHomeworkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/completed-homeworks")
@AllArgsConstructor
public class CompletedHomeworkController {

  private final CompletedHomeworkService completedHomeworkService;
  private final CompletedHomeworkMapper completedHomeworkMapper = CompletedHomeworkMapper.INSTANCE;

  @PreAuthorize("hasAnyAuthority('ADMIN', 'STUDENT')")
  @PostMapping
  public ResponseEntity<CompletedHomeworkDto> create(
      @Valid @RequestPart("request") CreateCompletedHomeworkRequest requestDto,
      @NotEmpty @RequestPart("file") MultipartFile file) {
    return new ResponseEntity<>(completedHomeworkService.save(requestDto, file), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public ResponseEntity<List<CompletedHomeworkDto>> getAll() {
    return new ResponseEntity<>(
        completedHomeworkService.getAll().stream()
            .map(completedHomeworkMapper::completedHomeworkToCompletedHomeworkDto)
            .toList(),
        HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<CompletedHomeworkDto> getById(@PathVariable String id) {
    return new ResponseEntity<>(
        completedHomeworkMapper.completedHomeworkToCompletedHomeworkDto(
            completedHomeworkService.getById(id)),
        HttpStatus.OK);
  }

  @PreAuthorize("hasAnyAuthority('ADMIN', 'INSTRUCTOR')")
  @PutMapping("/{completedHomeworkId}")
  public ResponseEntity<CompletedHomeworkDto> checkHomework(
      @PathVariable String completedHomeworkId,
      @Valid @RequestBody CheckHomeworkRequest requestDto) {
    return new ResponseEntity<>(
        completedHomeworkService.checkHomework(completedHomeworkId, requestDto), HttpStatus.OK);
  }
}
