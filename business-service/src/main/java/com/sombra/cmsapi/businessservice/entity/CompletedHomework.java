package com.sombra.cmsapi.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
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
@Table(name = "completed_homeworks", catalog = "cmsapi")
public class CompletedHomework {
  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String id;

  @ManyToOne
  @JoinColumn(name = "student_id", nullable = false)
  private User student;

  @ManyToOne
  @JoinColumn(name = "instructor_id")
  private User instructor;

  @ManyToOne
  @JoinColumn(name = "homework_id", nullable = false)
  @JsonBackReference
  private Homework homework;

  // todo store as blob
  @Lob
  @Column(columnDefinition = "blob")
  private byte[] homeworkFile;

  private int mark;
  private String comment;

  @CreationTimestamp private ZonedDateTime submissionDate;
  private ZonedDateTime reviewDate;
}
