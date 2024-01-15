package com.sombra.cmsapi.businessservice.entity;

import java.io.File;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BucketObjectRepresentation {
  private String objectName;
  private File file;
}
