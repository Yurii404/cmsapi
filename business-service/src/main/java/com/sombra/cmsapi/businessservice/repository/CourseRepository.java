package com.sombra.cmsapi.businessservice.repository;

import com.sombra.cmsapi.businessservice.entity.Course;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, String> {
  //
  //  @Query("SELECT DISTINCT c FROM Course c " + "JOIN FETCH c.students s " + "WHERE s.id =
  // :userId")
  //  Optional<List<Course>> findAllCoursesByStudent(@Param("userId") String userId);

  Optional<List<Course>> findByInstructorsId(String instructorId);

  Optional<List<Course>> findByStudentsId(String studentId);
}
