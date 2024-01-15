package com.sombra.cmsapi.businessservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.sombra.cmsapi.businessservice.entity.BucketObjectRepresentation;
import java.io.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AWSS3BucketService {

  private final AmazonS3 amazonS3Client;

  public String putObject(String bucketName, BucketObjectRepresentation representation) {
    String objectName = representation.getObjectName();
    File file = representation.getFile();

    try {
      var putObjectRequest = new PutObjectRequest(bucketName, objectName, file);

      amazonS3Client.putObject(putObjectRequest);

      // Generate the URL for the uploaded file
      return amazonS3Client.getUrl(bucketName, objectName).toExternalForm();
    } catch (Exception e) {
      log.error("Some error has occurred.", e);
      throw new RuntimeException("Failed to upload file to S3");
    }
  }
}
