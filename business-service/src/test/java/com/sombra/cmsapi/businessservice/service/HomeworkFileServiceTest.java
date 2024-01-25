package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.sombra.cmsapi.businessservice.dto.BucketObjectRepresentation;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

@ExtendWith(MockitoExtension.class)
class HomeworkFileServiceTest {

  @Mock private AWSS3BucketService awss3BucketService;

  @InjectMocks private HomeworkFileService homeworkFileService;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(homeworkFileService, "BUCKET_NAME", "testBucket");
    ReflectionTestUtils.setField(homeworkFileService, "BUCKET_FOLDER_NAME", "testFolder");
  }

  @Test
  void Should_GenerateFileName_When_ValidData() {
    String expectedDateTime = "2024-03-27-10-15-30-000";
    String expectedFileName =
        String.format("%s/%s_%s_%s", "testFolder", "1111", "1111", expectedDateTime);
    ZonedDateTime zonedDateTime =
        ZonedDateTime.of(2024, 3, 27, 10, 15, 30, 0, ZoneId.systemDefault());

    try (MockedStatic<ZonedDateTime> mockedLocalDateTime =
        Mockito.mockStatic(ZonedDateTime.class)) {

      mockedLocalDateTime.when(ZonedDateTime::now).thenReturn(zonedDateTime);

      String result =
          ReflectionTestUtils.invokeMethod(homeworkFileService, "generateFileName", "1111", "1111");

      assertEquals(expectedFileName, result);
    }
  }

  @Test
  void Should_ReturnUrl_When_SuccessfulSave() throws IOException {
    User student =
        User.builder()
            .id("1111")
            .email("test@mail")
            .password("password")
            .role(UserRole.STUDENT)
            .firstName("John")
            .lastName("Doe")
            .build();

    Homework homework = Homework.builder().id("1111").task("task").build();

    MultipartFile file =
        new MockMultipartFile("testFile", "testFile.txt", "text/plain", "Hello, World!".getBytes());

    when(awss3BucketService.putObject(anyString(), any(BucketObjectRepresentation.class)))
        .thenReturn("yourS3ObjectUrl");

    String result = homeworkFileService.saveHomeworkFileToS3(student, homework, file);

    assertEquals("yourS3ObjectUrl", result);
    verify(awss3BucketService, times(1)).putObject(any(), any());
  }
}
