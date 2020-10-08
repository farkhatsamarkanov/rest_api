package unit.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.StudentDAOImpl;
import com.rdlab.universityregistrar.model.entity.Student;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.StudentDTO;
import com.rdlab.universityregistrar.service.implementation.StudentServiceImpl;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.StudentDTOEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

public class StudentServiceTest {
    private AbstractService<Student, StudentDTO> service;
    private DAO<Student> dao;
    private DTOEntityMapper<Student, StudentDTO> mapper;
    private Student sampleStudent;
    private StudentDTO sampleStudentDTO;
    private BindingResult bindingResult;

    @BeforeEach
    public void setupData() {
        dao = Mockito.mock(StudentDAOImpl.class);
        mapper = Mappers.getMapper(StudentDTOEntityMapper.class);
        service = new StudentServiceImpl(dao);
        service.setMapper(mapper);
        sampleStudent = Student.builder()
                .studentId(1)
                .studentName("John Sid")
                .dateOfBirth(472502092000L)
                .build();
        sampleStudentDTO = mapper.entityToDto(sampleStudent);
        bindingResult = new BeanPropertyBindingResult(sampleStudentDTO, "sampleStudentDTO");
    }

    @Test
    public void testGetAllEntities() {
        Mockito.when(dao.getAllRecords()).thenReturn(Arrays.asList(sampleStudent));

        ResponseEntity<Response> result = service.getAllEntities();

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<StudentDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testGetEntity() {
        Mockito.when(dao.getRecord("3")).thenReturn(sampleStudent);

        ResponseEntity<Response> result = service.getEntity("3");

        assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleStudentDTO));
    }

    @Test
    public void testAddCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleStudentDTO, "sampleStudentDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleStudentDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.addEntity(sampleStudentDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleStudentDTO))
        );
    }

    @Test
    public void testAddCorrectEntityDBFailure() {
        bindingResult = new BeanPropertyBindingResult(sampleStudentDTO, "sampleStudentDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleStudentDTO))).thenReturn(null);
        ResponseEntity<Response> result = service.addEntity(sampleStudentDTO, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testAddEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleStudentDTO, "sampleStudentDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.addEntity(sampleStudentDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((ArrayList<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testUpdateCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleStudentDTO, "correctStudentDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleStudentDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.updateEntity(sampleStudentDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleStudentDTO))
        );
    }

    @Test
    public void testUpdateEntityShouldReturnNotFoundStatusCode() {
        bindingResult = new BeanPropertyBindingResult(sampleStudentDTO, "correctStudentDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleStudentDTO))).thenReturn(0);

        ResponseEntity<Response> result = service.updateEntity(sampleStudentDTO, bindingResult);


        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void testUpdateInvalidEntity() {
        StudentDTO anotherInvalidStudentDTO = StudentDTO.builder()
                .studentId(1)
                .studentName("$%^&*")
                .dateOfBirth(new Date(472502092000L))
                .build();

        bindingResult = new BeanPropertyBindingResult(anotherInvalidStudentDTO, "anotherInvalidStudentDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.updateEntity(anotherInvalidStudentDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((List<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testDeleteCorrectEntity() {
        Mockito.when(dao.deleteRecord("3")).thenReturn(1);
        ResponseEntity<Response> result = service.deleteEntity("3");

        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testDeleteEntityNotFound() {
        Mockito.when(dao.deleteRecord("3")).thenReturn(0);
        ResponseEntity<Response> result = service.deleteEntity("3");

        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testSearchEntities() {
        Mockito.when(dao.searchRecords("John")).thenReturn(Arrays.asList(sampleStudent));

        ResponseEntity<Response> result = service.searchForEntities("John");

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<StudentDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @AfterEach
    public void cleanUp() {
        dao = null;
        service = null;
        mapper = null;
        sampleStudent = null;
        sampleStudentDTO = null;
        bindingResult = null;
    }
}
