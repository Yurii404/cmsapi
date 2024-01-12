package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.lesson.CreateLessonRequest;
import com.sombra.cmsapi.businessservice.dto.lesson.LessonDto;
import com.sombra.cmsapi.businessservice.dto.lesson.UpdateLessonRequest;
import com.sombra.cmsapi.businessservice.entity.Course;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.LessonMapper;
import com.sombra.cmsapi.businessservice.repository.CourseRepository;
import com.sombra.cmsapi.businessservice.repository.LessonRepository;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class LessonService {

  private final LessonRepository lessonRepository;
  private final CourseRepository courseRepository;
  private final Logger LOGGER = LoggerFactory.getLogger(LessonService.class);
  private final LessonMapper lessonMapper = LessonMapper.INSTANCE;

  public LessonDto save(CreateLessonRequest createLessonRequest) {
    Course course;
    Lesson lessonToSave;

    if (createLessonRequest.getCourseId() != null) {
      course =
          courseRepository
              .findById(createLessonRequest.getCourseId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          String.format(
                              "Course with id: %s does not exist!",
                              createLessonRequest.getCourseId())));

      lessonToSave = lessonMapper.createLessonRequestToLesson(createLessonRequest, course);
    } else {
      lessonToSave = lessonMapper.createLessonRequestToLesson(createLessonRequest);
    }

    return lessonMapper.lessonToLessonDto(lessonRepository.save(lessonToSave));
  }

  public LessonDto update(UpdateLessonRequest updateLessonRequest) {
    Lesson lessonFromRep =
        lessonRepository
            .findById(updateLessonRequest.getId())
            .orElseThrow(
                () ->
                    new EntityNotFoundException(
                        String.format(
                            "Lesson with id: %s does not exist!", updateLessonRequest.getId())));

    lessonFromRep.setTitle(updateLessonRequest.getTitle());
    lessonFromRep.setContent(updateLessonRequest.getContent());

    if (updateLessonRequest.getCourseId() != null) {
      Course course =
          courseRepository
              .findById(updateLessonRequest.getCourseId())
              .orElseThrow(
                  () ->
                      new EntityNotFoundException(
                          String.format(
                              "Course with id: %s does not exist!",
                              updateLessonRequest.getCourseId())));

      lessonFromRep.setCourse(course);
    }

    return lessonMapper.lessonToLessonDto(lessonRepository.save(lessonFromRep));
  }

  public List<Lesson> getAll() {
    return lessonRepository.findAll();
  }

  public Lesson getById(String userId) {
    return lessonRepository
        .findById(userId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Lesson with id: %s does not exist!", userId)));
  }
}
