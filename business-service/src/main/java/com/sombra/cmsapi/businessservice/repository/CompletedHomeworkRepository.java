package com.sombra.cmsapi.businessservice.repository;

import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompletedHomeworkRepository extends JpaRepository<CompletedHomework, String> {
  Optional<CompletedHomework> findFirsByStudentAndHomeworkOrderBySubmissionDateDesc(
      User student, Homework homework);
}
