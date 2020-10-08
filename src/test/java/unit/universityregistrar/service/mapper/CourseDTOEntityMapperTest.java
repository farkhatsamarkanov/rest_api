package unit.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Course;
import com.rdlab.universityregistrar.service.dto.CourseDTO;
import com.rdlab.universityregistrar.service.mapper.CourseDTOEntityMapper;
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

public class CourseDTOEntityMapperTest {
    private DTOEntityMapper<Course, CourseDTO> mapper;
    private Course defaultCourse;
    private CourseDTO defaultCourseDTO;
    private List<Course> academicRankList;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(CourseDTOEntityMapper.class);
        defaultCourse = Course.builder()
                .courseTitle("Something_something")
                .courseDescription("bla bLa bla 01 01")
                .build();
        defaultCourseDTO = CourseDTO.builder()
                .courseTitle("Something_something")
                .courseDescription("bla bLa bla 01 01")
                .build();
        academicRankList = Collections.singletonList(defaultCourse);
    }

    @Test
    public void testDtoToEntityMapping() {
        Course resultEntity = mapper.dtoToEntity(defaultCourseDTO);

        assertThat(resultEntity, samePropertyValuesAs(defaultCourse));
    }

    @Test
    public void testEntityToDtoMapping() {
        CourseDTO resultDTO = mapper.entityToDto(defaultCourse);

        assertThat(resultDTO, samePropertyValuesAs(defaultCourseDTO));
    }

    @Test
    public void testEntityListToDtoList() {
        List<CourseDTO> dtoList = mapper.entityListToDtoList(academicRankList);

        assertFalse(dtoList.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        mapper = null;
        defaultCourse = null;
        defaultCourseDTO = null;
        academicRankList = null;
    }
}
