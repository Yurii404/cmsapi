package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CheckHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.dto.completedHomework.CreateCompletedHomeworkRequest;
import com.sombra.cmsapi.businessservice.entity.BucketObjectRepresentation;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
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

  public static final String BUCKET_NAME = "cmsapi";
  public static final String BUKET_FOLDER_NAME = "homeworks";
  private final CompletedHomeworkRepository completedHomeworkRepository;
  private final UserRepository userRepository;
  private final HomeworkRepository homeworkRepository;
  private final AWSS3BucketService awss3BucketService;

  private final Logger LOGGER = LoggerFactory.getLogger(CompletedHomeworkService.class);

  private final CompletedHomeworkMapper completedHomeworkMapper = CompletedHomeworkMapper.INSTANCE;

  public CompletedHomeworkDto save(CreateCompletedHomeworkRequest requestDto, MultipartFile file) {
    User student = getStudentById(requestDto.getStudentId());
    Homework homework = getHomeworkById(requestDto.getHomeworkId());

    try {

      String fileUrl = saveFileToS3(student, homework, file);

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

  private String saveFileToS3(User student, Homework homework, MultipartFile file)
      throws IOException {
    BucketObjectRepresentation bucketObjectRepresentation =
        BucketObjectRepresentation.builder()
            .objectName(generateFileName(student.getId(), homework.getId()))
            .file(convertMultipartFileToFile(file))
            .build();

    return awss3BucketService.putObject(BUCKET_NAME, bucketObjectRepresentation);
  }

  private File convertMultipartFileToFile(MultipartFile multipartFile) throws IOException {
    File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
    try (FileOutputStream fos = new FileOutputStream(file)) {
      fos.write(multipartFile.getBytes());
    }
    return file;
  }

  public static String generateFileName(String studentId, String homeworkId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
    String dateTimeString = ZonedDateTime.now().format(formatter);
    return String.format("%s/%s_%s_%s", BUKET_FOLDER_NAME, studentId, homeworkId, dateTimeString);
  }

  public CompletedHomeworkDto checkHomework(
      String completedHomeworkId, CheckHomeworkRequest requestDto) {
    CompletedHomework completedHomework = getById(completedHomeworkId);
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
