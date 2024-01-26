package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CheckHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import com.sombra.cmsapi.businessservice.exception.BrokenFileException;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.exception.NotAllowedOperationException;
import com.sombra.cmsapi.businessservice.mapper.CompletedHomeworkMapper;
import com.sombra.cmsapi.businessservice.repository.CompletedHomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.HomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.UserRepository;
import java.io.IOException;
import java.time.ZonedDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@Service
@Slf4j
public class CompletedHomeworkService {

  private final CompletedHomeworkRepository completedHomeworkRepository;
  private final UserRepository userRepository;
  private final HomeworkRepository homeworkRepository;
  private final HomeworkFileService homeworkFileService;
  private final Logger LOGGER = LoggerFactory.getLogger(CompletedHomeworkService.class);

  private final CompletedHomeworkMapper completedHomeworkMapper = CompletedHomeworkMapper.INSTANCE;

  public CompletedHomeworkDto save(CreateCompletedHomeworkRequest requestDto, MultipartFile file) {
    User student = getStudentById(requestDto.getStudentId());
    Homework homework = getHomeworkById(requestDto.getHomeworkId());

    try {

      String fileUrl = homeworkFileService.saveHomeworkFileToS3(student, homework, file);

      CompletedHomework completedHomework =
          CompletedHomework.builder()
              .homeworkFileUrl(fileUrl)
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

  public CompletedHomeworkDto checkHomework(
      String completedHomeworkId, CheckHomeworkRequest requestDto) {
    CompletedHomework completedHomework = getCompletedHomeworkById(completedHomeworkId);
    User instructor = getInstructorById(requestDto.getInstructorId());

    validateInstructorPermissions(instructor, completedHomework);

    completedHomework.setMark(requestDto.getMark());
    completedHomework.setReviewDate(ZonedDateTime.now());
    completedHomework.setInstructor(instructor);
    if (requestDto.getComment() != null) {
      completedHomework.setComment(requestDto.getComment());
    }

    CompletedHomework savedCompletedHomework = completedHomeworkRepository.save(completedHomework);
    return completedHomeworkMapper.completedHomeworkToCompletedHomeworkDto(savedCompletedHomework);
  }

  private void validateInstructorPermissions(User instructor, CompletedHomework completedHomework) {
    if (!completedHomework
        .getHomework()
        .getLesson()
        .getCourse()
        .getInstructors()
        .contains(instructor)) {
      throw new NotAllowedOperationException(
          String.format(
              "Instructor with id %s does not teaches the course this homework belongs.",
              instructor.getId()));
    }
  }

  public Page<CompletedHomeworkDto> getAll(Pageable pageable) {
    return completedHomeworkRepository
        .findAll(pageable)
        .map(completedHomeworkMapper::completedHomeworkToCompletedHomeworkDto);
  }

  public CompletedHomeworkDto getById(String id) {
    return completedHomeworkMapper.completedHomeworkToCompletedHomeworkDto(
        getCompletedHomeworkById(id));
  }

  private CompletedHomework getCompletedHomeworkById(String id) {
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

  private User getInstructorById(String userId) {
    return userRepository
        .findByIdAndRole(userId, UserRole.INSTRUCTOR)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Instructor with id: %s does not exist!", userId)));
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
