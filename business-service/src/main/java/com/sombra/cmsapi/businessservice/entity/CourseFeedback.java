package com.sombra.cmsapi.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.sombra.cmsapi.businessservice.enumerated.CourseStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "course_feedbacks", catalog = "cmsapi")
public class CourseFeedback {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String id;

  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  @JsonBackReference
  private User student;

  @ManyToOne
  @JoinColumn(name = "instructor_id")
  private User instructor;

  @ManyToOne
  @JoinColumn(name = "course_id", nullable = false)
  @JsonBackReference
  private Course course;

  @Enumerated(EnumType.STRING)
  private CourseStatus status;

  private Integer finalMark;
  private String comment;

  @CreationTimestamp private ZonedDateTime created;
}
