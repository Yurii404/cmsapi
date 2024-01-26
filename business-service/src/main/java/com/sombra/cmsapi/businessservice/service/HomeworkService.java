package com.sombra.cmsapi.businessservice.service;

import com.sombra.cmsapi.businessservice.dto.homework.CreateHomeworkRequest;
import com.sombra.cmsapi.businessservice.dto.homework.HomeworkDto;
import com.sombra.cmsapi.businessservice.entity.Homework;
import com.sombra.cmsapi.businessservice.entity.Lesson;
import com.sombra.cmsapi.businessservice.exception.EntityNotFoundException;
import com.sombra.cmsapi.businessservice.mapper.HomeworkMapper;
import com.sombra.cmsapi.businessservice.repository.HomeworkRepository;
import com.sombra.cmsapi.businessservice.repository.LessonRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
@Slf4j
public class HomeworkService {

  private final LessonRepository lessonRepository;
  private final HomeworkRepository homeworkRepository;
  private final HomeworkMapper homeworkMapper = HomeworkMapper.INSTANCE;

  public HomeworkDto save(CreateHomeworkRequest createHomeworkRequest) {
    Lesson lesson = getLessonById(createHomeworkRequest.getLessonId());

    Homework homeworkToSave =
        homeworkMapper.createHomeworkRequestToHomework(createHomeworkRequest, lesson);

    return homeworkMapper.homeworkToHomeworkDto(homeworkRepository.save(homeworkToSave));
  }

  public Page<HomeworkDto> getAll(Pageable pageable) {
    return homeworkRepository.findAll(pageable).map(homeworkMapper::homeworkToHomeworkDto);
  }

  public HomeworkDto getById(String userId) {
    return homeworkRepository
        .findById(userId)
        .map(homeworkMapper::homeworkToHomeworkDto)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Homework with id: %s does not exist!", userId)));
  }

  private Lesson getLessonById(String lessonId) {
    return lessonRepository
        .findById(lessonId)
        .orElseThrow(
            () ->
                new EntityNotFoundException(
                    String.format("Lesson with id: %s does not exist!", lessonId)));
  }
}
