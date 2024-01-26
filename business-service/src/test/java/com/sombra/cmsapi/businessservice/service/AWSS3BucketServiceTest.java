package com.sombra.cmsapi.businessservice.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.sombra.cmsapi.businessservice.dto.BucketObjectRepresentation;
import com.sombra.cmsapi.businessservice.exception.FileUploadingExceptionException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AWSS3BucketServiceTest {

  @Mock private AmazonS3 amazonS3;

  @InjectMocks private AWSS3BucketService awss3BucketService;

  @Test
  void Should_PutObject_When_ValidData() throws IOException {
    String bucketName = "testBucket";
    String objectName = "testObject";
    String expectedUrl = "https://s3-yourfile.com";
    BucketObjectRepresentation bucketObjectRepresentation =
        BucketObjectRepresentation.builder()
            .objectName(objectName)
            .file(File.createTempFile("file", "test"))
            .build();

    when(amazonS3.putObject(any())).thenReturn(new PutObjectResult());
    when(amazonS3.getUrl(bucketName, objectName)).thenReturn(new URL(expectedUrl));

    String result = awss3BucketService.putObject(bucketName, bucketObjectRepresentation);

    assertNotNull(result);
    assertEquals(expectedUrl, result);
  }

  @Test
  void Should_FileUploadingExceptionException_When_UploadFailed() throws IOException {
    String bucketName = "testBucket";
    String objectName = "testObject";
    BucketObjectRepresentation bucketObjectRepresentation =
        BucketObjectRepresentation.builder()
            .objectName(objectName)
            .file(File.createTempFile("file", "test"))
            .build();

    FileUploadingExceptionException thrown =
        Assertions.assertThrows(
            FileUploadingExceptionException.class,
            () -> {
              awss3BucketService.putObject(bucketName, bucketObjectRepresentation);
            });

    Assertions.assertEquals("Failed to upload file to S3", thrown.getMessage());
  }
}
