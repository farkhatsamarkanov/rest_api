package unit.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.LecturerDAOImpl;
import com.rdlab.universityregistrar.model.entity.AcademicRank;
import com.rdlab.universityregistrar.model.entity.Lecturer;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.LecturerDTO;
import com.rdlab.universityregistrar.service.implementation.LecturerServiceImpl;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.LecturerDTOEntityMapper;
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

public class LecturerServiceTest {
    private AbstractService<Lecturer, LecturerDTO> service;
    private DAO<Lecturer> dao;
    private DTOEntityMapper<Lecturer, LecturerDTO> mapper;
    private Lecturer sampleLecturer;
    private LecturerDTO sampleLecturerDTO;
    private BindingResult bindingResult;

    @BeforeEach
    public void setupData() {
        dao = Mockito.mock(LecturerDAOImpl.class);
        mapper = Mappers.getMapper(LecturerDTOEntityMapper.class);
        service = new LecturerServiceImpl(dao);
        service.setMapper(mapper);
        sampleLecturer = Lecturer.builder()
                .lecturerId(1)
                .lecturerName("John John")
                .dateOfBirth(998383301L)
                .numericAcademicRank(AcademicRank.builder().numericRank(2).build())
                .build();
        sampleLecturerDTO = mapper.entityToDto(sampleLecturer);
        bindingResult = new BeanPropertyBindingResult(sampleLecturer, "sampleLecturerDTO");
    }

    @Test
    public void testGetAllEntities() {
        Mockito.when(dao.getAllRecords()).thenReturn(Arrays.asList(sampleLecturer));

        ResponseEntity<Response> result = service.getAllEntities();

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<LecturerDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testGetEntity() {
        Mockito.when(dao.getRecord("1")).thenReturn(sampleLecturer);

        ResponseEntity<Response> result = service.getEntity("1");

        assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleLecturerDTO));
    }

    @Test
    public void testAddCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleLecturerDTO, "sampleLecturerDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleLecturerDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.addEntity(sampleLecturerDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleLecturerDTO))
        );
    }

    @Test
    public void testAddCorrectEntityDBFailure() {
        bindingResult = new BeanPropertyBindingResult(sampleLecturerDTO, "sampleLecturerDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleLecturerDTO))).thenReturn(null);

        ResponseEntity<Response> result = service.addEntity(sampleLecturerDTO, bindingResult);

        assertEquals(result.getStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    public void testAddEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleLecturerDTO, "sampleLecturerDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.addEntity(sampleLecturerDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((ArrayList<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testUpdateCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleLecturerDTO, "sampleLecturerDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleLecturerDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.updateEntity(sampleLecturerDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleLecturerDTO))
        );
    }

    @Test
    public void testUpdateEntityShouldReturnNotFoundStatusCode() {
        bindingResult = new BeanPropertyBindingResult(sampleLecturerDTO, "sampleLecturerDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleLecturerDTO))).thenReturn(0);

        ResponseEntity<Response> result = service.updateEntity(sampleLecturerDTO, bindingResult);

        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void testUpdateEntityWithValidationConstraints() {
        sampleLecturerDTO.setDateOfBirth(new Date(998383301L));
        bindingResult = new BeanPropertyBindingResult(sampleLecturerDTO, "sampleLecturerDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.updateEntity(sampleLecturerDTO, bindingResult);

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
        Mockito.when(dao.searchRecords("John")).thenReturn(Arrays.asList(sampleLecturer));

        ResponseEntity<Response> result = service.searchForEntities("John");

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<LecturerDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @AfterEach
    public void cleanUp() {
        dao = null;
        service = null;
        mapper = null;
        sampleLecturerDTO = null;
        sampleLecturer = null;
        bindingResult = null;
    }
}
