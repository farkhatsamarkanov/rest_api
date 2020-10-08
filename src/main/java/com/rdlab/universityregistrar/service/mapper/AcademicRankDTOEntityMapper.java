package com.rdlab.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.AcademicRank;
import com.rdlab.universityregistrar.service.dto.AcademicRankDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AcademicRankDTOEntityMapper extends DTOEntityMapper<AcademicRank, AcademicRankDTO> {
    @Override
    default AcademicRank dtoToEntity(AcademicRankDTO dto) {
        return AcademicRank.builder()
                .rankId(dto.getRankId())
                .rankName(dto.getRankName())
                .numericRank(dto.getNumericRank())
                .build();
    }

    @Override
    default AcademicRankDTO entityToDto(AcademicRank entity) {
        return AcademicRankDTO.builder()
                .rankId(entity.getRankId())
                .rankName(entity.getRankName())
                .numericRank(entity.getNumericRank())
                .build();
    }
}
