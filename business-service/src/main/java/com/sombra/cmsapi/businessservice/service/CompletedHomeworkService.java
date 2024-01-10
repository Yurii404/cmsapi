package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.BrokenFileException;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.CompletedHomeworkMapper;
import com.sombra.cmsapi.businessservice.repository.CompletedHomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.HomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
@Slf4j
public class CompletedHomeworkService {

  private final CompletedHomeworkRepository completedHomeworkRepository;
  private final UserRepository userRepository;
  private final HomeworkRepository homeworkRepository;

  private final Logger LOGGER = LoggerFactory.getLogger(CompletedHomeworkService.class);

  private final CompletedHomeworkMapper completedHomeworkMapper = CompletedHomeworkMapper.INSTANCE;

  public CompletedHomeworkDto save(CreateCompletedHomeworkRequest requestDto, MultipartFile file) {
    User student = getStudentById(requestDto.getStudentId());
    Homework homework = getHomeworkById(requestDto.getHomeworkId());

    try {
      CompletedHomework completedHomework =
          CompletedHomework.builder()
              .homeworkFile(file.getBytes())
              .student(student)
              .homework(homework)
              .build();

      CompletedHomework savedCompletedHomework =
          completedHomeworkRepository.save(completedHomework);

      return completedHomeworkMapper.completedHomeworkToCompletedHomeworkDto(
          savedCompletedHomework);
    } catch (IOException e) {
      LOGGER.error("Something went wrong when reading file.");
      throw new BrokenFileException("Something went wrong when reading file.");
    }
  }

  public List<CompletedHomework> getAll() {
    return completedHomeworkRepository.findAll();
  }

  public CompletedHomework getById(String id) {
    return completedHomeworkRepository
        .findById(id)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Completed homework with id: %s does not exist!", id)));
  }

  private User getStudentById(String userId) {
    return userRepository
        .findByIdAndRole(userId, UserRole.STUDENT)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Student with id: %s does not exist!", userId)));
  }

  private Homework getHomeworkById(String homeworkId) {
    return homeworkRepository
        .findById(homeworkId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Homework with id: %s does not exist!", homeworkId)));
  }
}
