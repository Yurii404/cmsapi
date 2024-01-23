package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.BucketObjectRepresentation;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class HomeworkFileService {

  @Value("${aws.bucketName}")
  private String BUCKET_NAME;

  @Value("${aws.bucketFolderName}")
  private String BUCKET_FOLDER_NAME;

  private final AWSS3BucketService awss3BucketService;

  public String saveHomeworkFileToS3(User student, Homework homework, MultipartFile file)
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

  private String generateFileName(String studentId, String homeworkId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
    String dateTimeString = ZonedDateTime.now().format(formatter);
    return String.format("%s/%s_%s_%s", BUCKET_FOLDER_NAME, studentId, homeworkId, dateTimeString);
  }
}
