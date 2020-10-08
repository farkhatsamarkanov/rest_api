package com.rdlab.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Student;
import com.rdlab.universityregistrar.service.dto.StudentDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface StudentDTOEntityMapper extends DTOEntityMapper<Student, StudentDTO> {
    StudentDTOEntityMapper INSTANCE = Mappers.getMapper(StudentDTOEntityMapper.class);

    @Override
    default Student dtoToEntity(StudentDTO dto) {
        return Student.builder()
                .studentId(dto.getStudentId())
                .studentName(dto.getStudentName())
                .dateOfBirth(dateToLong(dto.getDateOfBirth()))
                .build();
    }

    @Override
    default StudentDTO entityToDto(Student entity) {
        return StudentDTO.builder()
                .studentId(entity.getStudentId())
                .studentName(entity.getStudentName())
                .dateOfBirth(longToDate(entity.getDateOfBirth()))
                .build();
    }

}
