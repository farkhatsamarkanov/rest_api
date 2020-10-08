package unit.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.AcademicRank;
import com.rdlab.universityregistrar.service.dto.AcademicRankDTO;
import com.rdlab.universityregistrar.service.mapper.AcademicRankDTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class AcademicRankDTOEntityMapperTest {
    private DTOEntityMapper<AcademicRank, AcademicRankDTO> mapper;
    private AcademicRank defaultAcademicRank;
    private AcademicRankDTO defaultAcademicRankDTO;
    private List<AcademicRank> academicRankList;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(AcademicRankDTOEntityMapper.class);
        defaultAcademicRank = AcademicRank.builder()
                .numericRank(1).rankName("Professor")
                .build();
        defaultAcademicRankDTO = AcademicRankDTO.builder()
                .numericRank(1).rankName("Professor")
                .build();
        academicRankList = Collections.singletonList(defaultAcademicRank);
    }

    @Test
    public void testDtoToEntityMapping() {
        AcademicRank resultEntity = mapper.dtoToEntity(defaultAcademicRankDTO);

        assertThat(resultEntity, samePropertyValuesAs(defaultAcademicRank));
    }

    @Test
    public void testEntityToDtoMapping() {
        AcademicRankDTO resultDTO = mapper.entityToDto(defaultAcademicRank);

        assertThat(resultDTO, samePropertyValuesAs(defaultAcademicRankDTO));
    }

    @Test
    public void testEntityListToDtoList() {
        List<AcademicRankDTO> dtoList = mapper.entityListToDtoList(academicRankList);

        assertFalse(dtoList.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        mapper = null;
        defaultAcademicRank = null;
        defaultAcademicRankDTO = null;
        academicRankList = null;
    }
}
