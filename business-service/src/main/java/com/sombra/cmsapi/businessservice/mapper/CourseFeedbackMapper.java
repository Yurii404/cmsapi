package com.sombra.cmsapi.businessservice.mapper;

import com.sombra.cmsapi.businessservice.dto.courseFeedback.CourseFeedbackDto;
import com.sombra.cmsapi.businessservice.entity.CourseFeedback;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CourseFeedbackMapper {
  CourseFeedbackMapper INSTANCE = Mappers.getMapper(CourseFeedbackMapper.class);

  CourseFeedbackDto courseFeedbackToCourseFeedbackDto(CourseFeedback courseFeedback);
}
