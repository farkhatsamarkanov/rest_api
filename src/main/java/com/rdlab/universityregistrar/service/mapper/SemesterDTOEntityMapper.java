package com.rdlab.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Semester;
import com.rdlab.universityregistrar.service.dto.SemesterDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SemesterDTOEntityMapper extends DTOEntityMapper<Semester, SemesterDTO> {
    SemesterDTOEntityMapper INSTANCE = Mappers.getMapper(SemesterDTOEntityMapper.class);

    @Override
    default Semester dtoToEntity(SemesterDTO dto) {
        return Semester.builder()
                .entryId(dto.getEntryId())
                .semesterId(dto.getSemesterId())
                .semesterName(dto.getSemesterName())
                .semesterYear(dto.getSemesterYear())
                .semesterStartTime(dateToLong(dto.getSemesterStartTime()))
                .semesterEndTime(dateToLong(dto.getSemesterEndTime()))
                .build();
    }

    @Override
    default SemesterDTO entityToDto(Semester entity) {
        return SemesterDTO.builder()
                .entryId(entity.getEntryId())
                .semesterId(entity.getSemesterId())
                .semesterName(entity.getSemesterName())
                .semesterYear(entity.getSemesterYear())
                .semesterStartTime(longToDate(entity.getSemesterStartTime()))
                .semesterEndTime(longToDate(entity.getSemesterEndTime()))
                .build();
    }


}
