package unit.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.CourseDAOImpl;
import com.rdlab.universityregistrar.model.entity.Course;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.CourseDTO;
import com.rdlab.universityregistrar.service.implementation.CourseServiceImpl;
import com.rdlab.universityregistrar.service.mapper.CourseDTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

public class CourseServiceTest {
    private AbstractService<Course, CourseDTO> service;
    private DAO<Course> dao;
    private CourseDTO sampleCourseDTO;
    private Course sampleCourse;
    private BindingResult bindingResult;
    private DTOEntityMapper<Course, CourseDTO> mapper;

    @BeforeEach
    public void setupData() {
        dao = Mockito.mock(CourseDAOImpl.class);
        mapper = Mappers.getMapper(CourseDTOEntityMapper.class);
        service = new CourseServiceImpl(dao);
        service.setMapper(mapper);
        sampleCourse = Course.builder()
                .courseId(1)
                .courseTitle("Something_something")
                .courseDescription("bla bLa bla 01 01")
                .build();
        sampleCourseDTO = mapper.entityToDto(sampleCourse);
        bindingResult = new BeanPropertyBindingResult(sampleCourseDTO, "sampleCourseDTO");
    }

    @Test
    public void testGetAllEntities() {
        Mockito.when(dao.getAllRecords()).thenReturn(Arrays.asList(sampleCourse));

        ResponseEntity<Response> result = service.getAllEntities();

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<CourseDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testGetEntity() {
        Mockito.when(dao.getRecord("1")).thenReturn(sampleCourse);

        ResponseEntity<Response> result = service.getEntity("1");

        assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleCourseDTO));
    }

    @Test
    public void testAddCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleCourseDTO, "sampleCourseDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleCourseDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.addEntity(sampleCourseDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleCourseDTO))
        );
    }

    @Test
    public void testAddCorrectEntityDBFailure() {
        bindingResult = new BeanPropertyBindingResult(sampleCourseDTO, "sampleCourseDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleCourseDTO))).thenReturn(null);

        ResponseEntity<Response> result = service.addEntity(sampleCourseDTO, bindingResult);

        assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testAddEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleCourseDTO, "sampleCourseDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.addEntity(sampleCourseDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((ArrayList<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testUpdateCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleCourseDTO, "sampleCourseDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleCourseDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.updateEntity(sampleCourseDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleCourseDTO))
        );
    }

    @Test
    public void testUpdateEntityShouldReturnNotFoundStatus() {
        bindingResult = new BeanPropertyBindingResult(sampleCourseDTO, "sampleCourseDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleCourseDTO))).thenReturn(0);

        ResponseEntity<Response> result = service.updateEntity(sampleCourseDTO, bindingResult);


        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void testUpdateEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleCourseDTO, "sampleCourseDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.updateEntity(sampleCourseDTO, bindingResult);

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
        Mockito.when(dao.searchRecords("Something something")).thenReturn(Arrays.asList(sampleCourse));

        ResponseEntity<Response> result = service.searchForEntities("Something something");

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<CourseDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @AfterEach
    public void cleanUp() {
        dao = null;
        service = null;
        mapper = null;
        sampleCourseDTO = null;
        sampleCourse = null;
        bindingResult = null;
    }
}
