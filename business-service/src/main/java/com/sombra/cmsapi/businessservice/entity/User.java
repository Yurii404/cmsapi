package com.sombra.cmsapi.businessservice.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "users", catalog = "cmsapi")
public class User {

  @Id
  @GeneratedValue(generator = "uuid2")
  @GenericGenerator(name = "uuid2", strategy = "uuid2")
  private String id;

  private String firstName;
  private String lastName;
  private String email;
  private String password;

  @OneToMany(mappedBy = "student")
  @JsonManagedReference
  private List<CourseFeedback> feedbacks;

  @Enumerated(EnumType.STRING)
  private UserRole role;
}
