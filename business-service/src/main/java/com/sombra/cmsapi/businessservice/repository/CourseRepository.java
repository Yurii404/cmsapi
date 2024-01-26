package com.sombra.cmsapi.businessservice.repository;

import com.sombra.cmsapi.businessservice.entity.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {

  Page<Course> findByInstructorsId(String instructorId, Pageable pageable);

  Page<Course> findByStudentsId(String studentId, Pageable pageable);

  Optional<List<Course>> findByStudentsId(String studentId);
}
