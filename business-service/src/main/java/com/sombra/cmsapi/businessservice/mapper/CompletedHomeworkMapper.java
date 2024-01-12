package com.sombra.cmsapi.businessservice.mapper;

import com.sombra.cmsapi.businessservice.dto.completedHomework.CompletedHomeworkDto;
import com.sombra.cmsapi.businessservice.entity.CompletedHomework;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CompletedHomeworkMapper {
  CompletedHomeworkMapper INSTANCE = Mappers.getMapper(CompletedHomeworkMapper.class);

  CompletedHomeworkDto completedHomeworkToCompletedHomeworkDto(CompletedHomework completedHomework);
}
