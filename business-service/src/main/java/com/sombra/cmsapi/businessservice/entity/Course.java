package com.sombra.cmsapi.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "courses", catalog = "cmsapi")
public class Course {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String id;

  private String name;
  private String description;

  @ManyToMany(cascade = {CascadeType.ALL})
  @JoinTable(
      name = "student_course",
      joinColumns = {@JoinColumn(name = "course_id")},
      inverseJoinColumns = {@JoinColumn(name = "student_id")})
  private List<User> students;

  @ManyToMany(cascade = {CascadeType.ALL})
  @JoinTable(
      name = "instructor_course",
      joinColumns = {@JoinColumn(name = "course_id")},
      inverseJoinColumns = {@JoinColumn(name = "instructor_id")})
  private List<User> instructors;

  @OneToMany(mappedBy = "course")
  @JsonManagedReference
  private List<Lesson> lessons;

  @OneToMany(mappedBy = "course")
  @JsonManagedReference
  private List<CourseFeedback> feedbacks;
}
