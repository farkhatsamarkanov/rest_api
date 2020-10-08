package com.rdlab.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.*;
import com.rdlab.universityregistrar.service.dto.ScheduleEntryDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ScheduleEntryDTOEntityMapper extends DTOEntityMapper<ScheduleEntry, ScheduleEntryDTO> {
    ScheduleEntryDTOEntityMapper INSTANCE = Mappers.getMapper(ScheduleEntryDTOEntityMapper.class);

    @Override
    default ScheduleEntry dtoToEntity(ScheduleEntryDTO dto) {
        return ScheduleEntry.builder()
                .entryId(dto.getEntryId())
                .location(dto.getLocation())
                .time(dateToLong(dto.getTime()))
                .course(Course.builder().courseId(dto.getCourseId()).build())
                .lecturer(Lecturer.builder().lecturerId(dto.getLecturerId()).build())
                .student(Student.builder().studentId(dto.getStudentId()).build())
                .semester(Semester.builder().semesterId(dto.getSemesterId()).build())
                .build();
    }

    @Override
    default ScheduleEntryDTO entityToDto(ScheduleEntry entity) {
        return ScheduleEntryDTO.builder()
                .entryId(entity.getEntryId())
                .location(entity.getLocation())
                .time(longToDate(entity.getTime()))
                .courseId(entity.getCourse().getCourseId())
                .lecturerId(entity.getLecturer().getLecturerId())
                .semesterId(entity.getSemester().getSemesterId())
                .studentId(entity.getStudent().getStudentId())
                .build();
    }
}
