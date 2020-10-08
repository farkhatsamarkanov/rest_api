package unit.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.AcademicRank;
import com.rdlab.universityregistrar.model.entity.Lecturer;
import com.rdlab.universityregistrar.service.dto.LecturerDTO;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.LecturerDTOEntityMapper;
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

public class LecturerDTOEntityMapperTest {
    private DTOEntityMapper<Lecturer, LecturerDTO> mapper;
    private Lecturer defaultLecturer;
    private LecturerDTO defaultLecturerDTO;
    private List<Lecturer> lecturerList;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(LecturerDTOEntityMapper.class);
        defaultLecturer = Lecturer.builder()
                .lecturerName("John John")
                .dateOfBirth(998383301L)
                .numericAcademicRank(AcademicRank.builder().numericRank(2).build())
                .build();
        defaultLecturerDTO = LecturerDTO.builder()
                .lecturerName("John John")
                .dateOfBirth(new Date(998383301L))
                .numericAcademicRank(2)
                .build();
        lecturerList = Collections.singletonList(defaultLecturer);
    }

    @Test
    public void testDtoToEntityMapping() {
        Lecturer resultEntity = mapper.dtoToEntity(defaultLecturerDTO);

        assertThat(resultEntity, samePropertyValuesAs(defaultLecturer));
    }

    @Test
    public void testEntityToDtoMapping() {
        LecturerDTO resultDTO = mapper.entityToDto(defaultLecturer);

        assertThat(resultDTO, samePropertyValuesAs(defaultLecturerDTO));
    }

    @Test
    public void testEntityListToDtoList() {
        List<LecturerDTO> dtoList = mapper.entityListToDtoList(lecturerList);

        assertFalse(dtoList.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        mapper = null;
        defaultLecturer = null;
        defaultLecturerDTO = null;
        lecturerList = null;
    }
}
