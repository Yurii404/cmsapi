package com.sombra.cmsapi.businessservice.dto.completedHomework;

import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

@Data
@AllArgsConstructor
public class CreateCompletedHomeworkRequest {

  @NotEmpty(message = "StudentId cannot be null or empty")
  private String studentId;

  @NotEmpty(message = "HomeworkId cannot be null or empty")
  private String homeworkId;
}