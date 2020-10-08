package unit.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.*;
import com.rdlab.universityregistrar.service.dto.ScheduleEntryDTO;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.ScheduleEntryDTOEntityMapper;
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

public class ScheduleEntryDTOEntityMapperTest {
    private DTOEntityMapper<ScheduleEntry, ScheduleEntryDTO> mapper;
    private ScheduleEntry defaultScheduleEntry;
    private ScheduleEntryDTO defaultScheduleEntryDTO;
    private List<ScheduleEntry> academicRankList;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(ScheduleEntryDTOEntityMapper.class);
        defaultScheduleEntry = ScheduleEntry.builder()
                .student(Student.builder().studentId(1).build())
                .lecturer(Lecturer.builder().lecturerId(1).build())
                .course(Course.builder().courseId(1).build())
                .time(1661078705000L)
                .location("Room H3")
                .semester(Semester.builder().semesterId("FAL2020").build())
                .entryId(1)
                .build();
        defaultScheduleEntryDTO = ScheduleEntryDTO.builder()
                .studentId(1)
                .lecturerId(1)
                .courseId(1)
                .time(new Date(1661078705000L))
                .location("Room H3")
                .semesterId("FAL2020")
                .entryId(1)
                .build();
        academicRankList = Collections.singletonList(defaultScheduleEntry);
    }

    @Test
    public void testDtoToEntityMapping() {
        ScheduleEntry resultEntity = mapper.dtoToEntity(defaultScheduleEntryDTO);

        assertThat(resultEntity, samePropertyValuesAs(defaultScheduleEntry));
    }

    @Test
    public void testEntityToDtoMapping() {
        ScheduleEntryDTO resultDTO = mapper.entityToDto(defaultScheduleEntry);

        assertThat(resultDTO, samePropertyValuesAs(defaultScheduleEntryDTO));
    }

    @Test
    public void testEntityListToDtoList() {
        List<ScheduleEntryDTO> dtoList = mapper.entityListToDtoList(academicRankList);

        assertFalse(dtoList.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        mapper = null;
        defaultScheduleEntry = null;
        defaultScheduleEntryDTO = null;
        academicRankList = null;
    }
}
