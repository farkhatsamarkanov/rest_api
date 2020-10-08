package unit.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Semester;
import com.rdlab.universityregistrar.service.dto.SemesterDTO;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.SemesterDTOEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class SemesterDTOEntityMapperTest {
    private DTOEntityMapper<Semester, SemesterDTO> mapper;
    private Semester defaultSemester;
    private SemesterDTO defaultSemesterDTO;
    private List<Semester> semesterList;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(SemesterDTOEntityMapper.class);
        defaultSemester = Semester.builder()
                .semesterId("FAL2020")
                .semesterName("Fall semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(1634841292000L)
                .semesterEndTime(1640345656000L)
                .build();
        defaultSemesterDTO = SemesterDTO.builder()
                .semesterId("FAL2020")
                .semesterName("Fall semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(new Date(1634841292000L))
                .semesterEndTime(new Date(1640345656000L))
                .build();
        semesterList = Collections.singletonList(defaultSemester);
    }

    @Test
    public void testDtoToEntityMapping() {
        Semester resultEntity = mapper.dtoToEntity(defaultSemesterDTO);

        assertThat(resultEntity, samePropertyValuesAs(defaultSemester));
    }

    @Test
    public void testEntityToDtoMapping() {
        SemesterDTO resultDTO = mapper.entityToDto(defaultSemester);

        assertThat(resultDTO, samePropertyValuesAs(defaultSemesterDTO));
    }

    @Test
    public void testEntityListToDtoList() {
        List<SemesterDTO> dtoList = mapper.entityListToDtoList(semesterList);

        assertFalse(dtoList.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        mapper = null;
        defaultSemester = null;
        defaultSemesterDTO = null;
        semesterList = null;
    }
}
