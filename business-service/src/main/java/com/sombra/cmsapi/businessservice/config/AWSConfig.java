package com.sombra.cmsapi.businessservice.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {

  @Value("${security.aws.accessKey}")
  private String AWS_ACCESS_KEY;

  @Value("${security.aws.secretKey}")
  private String AWS_SECRET_KEY;

  public AWSCredentials credentials() {
    return new BasicAWSCredentials(AWS_ACCESS_KEY, AWS_SECRET_KEY);
  }

  @Bean
  public AmazonS3 amazonS3() {
    return AmazonS3ClientBuilder.standard()
        .withCredentials(new AWSStaticCredentialsProvider(credentials()))
        .withRegion(Regions.EU_NORTH_1)
        .build();
  }
}
