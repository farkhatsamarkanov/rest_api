package com.rdlab.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.AcademicRank;
import com.rdlab.universityregistrar.model.entity.Lecturer;
import com.rdlab.universityregistrar.service.dto.LecturerDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface LecturerDTOEntityMapper extends DTOEntityMapper<Lecturer, LecturerDTO> {
    LecturerDTOEntityMapper INSTANCE = Mappers.getMapper(LecturerDTOEntityMapper.class);

    @Override
    default Lecturer dtoToEntity(LecturerDTO dto) {
        return Lecturer.builder()
                .lecturerId(dto.getLecturerId())
                .lecturerName(dto.getLecturerName())
                .dateOfBirth(dateToLong(dto.getDateOfBirth()))
                .numericAcademicRank(AcademicRank.builder().numericRank(dto.getNumericAcademicRank()).build())
                .build();
    }

    @Override
    default LecturerDTO entityToDto(Lecturer entity) {
        return LecturerDTO.builder()
                .lecturerId(entity.getLecturerId())
                .lecturerName(entity.getLecturerName())
                .dateOfBirth(longToDate(entity.getDateOfBirth()))
                .numericAcademicRank(entity.getNumericAcademicRank().getNumericRank())
                .build();
    }


}
