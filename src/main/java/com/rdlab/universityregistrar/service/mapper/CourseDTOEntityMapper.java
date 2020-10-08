package com.rdlab.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Course;
import com.rdlab.universityregistrar.service.dto.CourseDTO;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CourseDTOEntityMapper extends DTOEntityMapper<Course, CourseDTO> {
    @Override
    default Course dtoToEntity(CourseDTO dto) {
        return Course.builder()
                .courseId(dto.getCourseId())
                .courseTitle(dto.getCourseTitle())
                .courseDescription(dto.getCourseDescription())
                .build();
    }

    @Override
    default CourseDTO entityToDto(Course entity) {
        return CourseDTO.builder()
                .courseId(entity.getCourseId())
                .courseTitle(entity.getCourseTitle())
                .courseDescription(entity.getCourseDescription())
                .build();
    }
}
