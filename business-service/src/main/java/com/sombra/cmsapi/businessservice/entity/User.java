package com.sombra.cmsapi.businessservice.entity;

import com.sombra.cmsapi.businessservice.enumerated.UserRole;
import jakarta.persistence.*;
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
  private List<CourseFeedback> feedbacks;

  @Enumerated(EnumType.STRING)
  private UserRole role;
}
