package unit.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Student;
import com.rdlab.universityregistrar.service.dto.StudentDTO;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.StudentDTOEntityMapper;
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

public class StudentDTOEntityMapperTest {
    private DTOEntityMapper<Student, StudentDTO> mapper;
    private Student defaultStudent;
    private StudentDTO defaultStudentDTO;
    private List<Student> studentList;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(StudentDTOEntityMapper.class);
        defaultStudent = Student.builder()
                .studentId(1)
                .studentName("John Sid")
                .dateOfBirth(472502092000L)
                .build();
        defaultStudentDTO = StudentDTO.builder()
                .studentId(1)
                .studentName("John Sid")
                .dateOfBirth(new Date(472502092000L))
                .build();
        studentList = Collections.singletonList(defaultStudent);
    }

    @Test
    public void testDtoToEntityMapping() {
        Student resultEntity = mapper.dtoToEntity(defaultStudentDTO);

        assertThat(resultEntity, samePropertyValuesAs(defaultStudent));
    }

    @Test
    public void testEntityToDtoMapping() {
        StudentDTO resultDTO = mapper.entityToDto(defaultStudent);

        assertThat(resultDTO, samePropertyValuesAs(defaultStudentDTO));
    }

    @Test
    public void testEntityListToDtoList() {
        List<StudentDTO> dtoList = mapper.entityListToDtoList(studentList);

        assertFalse(dtoList.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        mapper = null;
        defaultStudent = null;
        defaultStudentDTO = null;
        studentList = null;
    }
}
