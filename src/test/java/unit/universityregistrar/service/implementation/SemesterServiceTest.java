package unit.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.SemesterDAOImpl;
import com.rdlab.universityregistrar.model.entity.Semester;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.SemesterDTO;
import com.rdlab.universityregistrar.service.implementation.SemesterServiceImpl;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.SemesterDTOEntityMapper;
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

public class SemesterServiceTest {
    private AbstractService<Semester, SemesterDTO> service;
    private DAO<Semester> dao;
    private DTOEntityMapper<Semester, SemesterDTO> mapper;
    private Semester sampleSemester;
    private SemesterDTO sampleSemesterDTO;
    private BindingResult bindingResult;

    @BeforeEach
    public void setupData() {
        dao = Mockito.mock(SemesterDAOImpl.class);
        mapper = Mappers.getMapper(SemesterDTOEntityMapper.class);
        service = new SemesterServiceImpl(dao);
        service.setMapper(mapper);
        sampleSemester = Semester.builder()
                .semesterId("FAL2020")
                .semesterName("Fall semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(1634841292000L)
                .semesterEndTime(1640345656000L)
                .build();
        sampleSemesterDTO = mapper.entityToDto(sampleSemester);
        bindingResult = new BeanPropertyBindingResult(sampleSemesterDTO, "sampleSemesterDTO");
    }

    @Test
    public void testGetAllEntities() {
        Mockito.when(dao.getAllRecords()).thenReturn(Arrays.asList(sampleSemester));

        ResponseEntity<Response> result = service.getAllEntities();

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<SemesterDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testGetEntity() {
        Mockito.when(dao.getRecord("FAL2020")).thenReturn(sampleSemester);

        ResponseEntity<Response> result = service.getEntity("FAL2020");

        assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleSemesterDTO));
    }

    @Test
    public void testAddCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleSemesterDTO, "sampleSemesterDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleSemesterDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.addEntity(sampleSemesterDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertThat(Objects.requireNonNull(result.getBody()).getResponseBody(), samePropertyValuesAs(sampleSemesterDTO))
        );
    }

    @Test
    public void testAddCorrectEntityDBFailure() {
        bindingResult = new BeanPropertyBindingResult(sampleSemesterDTO, "sampleSemesterDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleSemesterDTO))).thenReturn(null);
        ResponseEntity<Response> result = service.addEntity(sampleSemesterDTO, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testAddEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleSemesterDTO, "sampleSemesterDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.addEntity(sampleSemesterDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((ArrayList<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testAddEntityWithStartTimeBeforeEndTime() {
        SemesterDTO anotherInvalidSemesterDTO = SemesterDTO.builder()
                .semesterId("$$$$$$$$")
                .semesterName("$%^&*(")
                .semesterYear(null)
                .semesterStartTime(new Date(1640111692000L))
                .semesterEndTime(new Date(1608575692000L))
                .build();
        ResponseEntity<Response> result = service.addEntity(anotherInvalidSemesterDTO, bindingResult);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()),
                () -> assertEquals((Objects.requireNonNull(result.getBody()).getResponseBody()), "semester end time cannot be before semester begin time")
        );
    }

    @Test
    public void testUpdateCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleSemesterDTO, "sampleSemesterDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleSemesterDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.updateEntity(sampleSemesterDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertThat(Objects.requireNonNull(result.getBody()).getResponseBody(), samePropertyValuesAs(sampleSemesterDTO))
        );
    }

    @Test
    public void testUpdateEntityShouldReturnNotFoundStatusCode() {
        bindingResult = new BeanPropertyBindingResult(sampleSemesterDTO, "sampleSemesterDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleSemesterDTO))).thenReturn(0);

        ResponseEntity<Response> result = service.updateEntity(sampleSemesterDTO, bindingResult);


        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void testUpdateInvalidEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleSemesterDTO, "sampleSemesterDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.updateEntity(sampleSemesterDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((List<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testDeleteCorrectEntity() {
        Mockito.when(dao.deleteRecord("FAL2020")).thenReturn(1);
        ResponseEntity<Response> result = service.deleteEntity("FAL2020");

        assertEquals(result.getStatusCode(), HttpStatus.OK);
    }

    @Test
    public void testDeleteEntityNotFound() {
        Mockito.when(dao.deleteRecord("FAL2020")).thenReturn(0);
        ResponseEntity<Response> result = service.deleteEntity("FAL2020");

        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);
    }

    @Test
    public void testSearchEntities() {
        Mockito.when(dao.searchRecords("FAL2020")).thenReturn(Arrays.asList(sampleSemester));

        ResponseEntity<Response> result = service.searchForEntities("FAL2020");

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<SemesterDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @AfterEach
    public void cleanUp() {
        dao = null;
        service = null;
        mapper = null;
        sampleSemester = null;
        sampleSemesterDTO = null;
        bindingResult = null;
    }
}
