package com.sombra.cmsapi.businessservice.controller;

import com.sombra.cmsapi.businessservice.dto.homework.CreateHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.homework.HomeworkDto;
import com.sombra.cmsapi.businessservice.mapper.HomeworkMapper;
import com.sombra.cmsapi.businessservice.service.HomeworkService;
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
@RequestMapping("/homeworks")
@AllArgsConstructor
public class HomeworkController {

  private final HomeworkService homeworkService;
  private final HomeworkMapper homeworkMapper = HomeworkMapper.INSTANCE;

  @PreAuthorize("hasAuthority('ADMIN')")
  @PostMapping
  public ResponseEntity<HomeworkDto> create(@Valid @RequestBody CreateHomeworkRequest requestDto) {
    return new ResponseEntity<>(homeworkService.save(requestDto), HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping
  public ResponseEntity<List<HomeworkDto>> getAll() {
    return new ResponseEntity<>(
        homeworkService.getAll().stream().map(homeworkMapper::homeworkToHomeworkDto).toList(),
        HttpStatus.OK);
  }

  @PreAuthorize("hasAuthority('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<HomeworkDto> getById(@PathVariable String id) {
    return new ResponseEntity<>(
        homeworkMapper.homeworkToHomeworkDto(homeworkService.getById(id)), HttpStatus.OK);
  }
}
