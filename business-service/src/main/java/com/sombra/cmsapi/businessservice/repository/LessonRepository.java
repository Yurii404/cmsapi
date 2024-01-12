package com.sombra.cmsapi.businessservice.repository;

import com.sombra.cmsapi.businessservice.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LessonRepository extends JpaRepository<Lesson, String> {}
