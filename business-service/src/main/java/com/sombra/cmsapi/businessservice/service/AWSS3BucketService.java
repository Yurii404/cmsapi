package com.sombra.cmsapi.businessservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.sombra.cmsapi.businessservice.dto.BucketObjectRepresentation;
import com.sombra.cmsapi.businessservice.exception.FileUploadingExceptionException;
import java.io.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSS3BucketService {

  private final AmazonS3 amazonS3Client;
  private final Logger LOGGER = LoggerFactory.getLogger(CourseService.class);

  public String putObject(String bucketName, BucketObjectRepresentation representation) {
    String objectName = representation.getObjectName();
    File file = representation.getFile();

    try {
      var putObjectRequest = new PutObjectRequest(bucketName, objectName, file);

      amazonS3Client.putObject(putObjectRequest);

      // Generate the URL for the uploaded file
      return amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
    } catch (Exception e) {
      LOGGER.error("Failed to upload file to S3.", e);
      throw new FileUploadingExceptionException("Failed to upload file to S3");
    }
  }
}
