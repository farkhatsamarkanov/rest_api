package unit.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.AcademicRankDAOImpl;
import com.rdlab.universityregistrar.model.entity.AcademicRank;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.AcademicRankDTO;
import com.rdlab.universityregistrar.service.implementation.AcademicRankServiceImpl;
import com.rdlab.universityregistrar.service.mapper.AcademicRankDTOEntityMapper;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

public class AcademicRankServiceTest {
    private AbstractService<AcademicRank, AcademicRankDTO> service;
    private DAO<AcademicRank> dao;
    private AcademicRankDTO sampleRankDTO;
    private AcademicRank sampleRank;
    private BindingResult bindingResult;
    private DTOEntityMapper<AcademicRank, AcademicRankDTO> mapper;

    @BeforeEach
    public void setupData() {
        dao = Mockito.mock(AcademicRankDAOImpl.class);
        mapper = Mappers.getMapper(AcademicRankDTOEntityMapper.class);
        service = new AcademicRankServiceImpl(dao);
        service.setMapper(mapper);
        sampleRank = AcademicRank.builder()
                .rankId(1)
                .numericRank(1)
                .rankName("Professor")
                .build();
        sampleRankDTO = mapper.entityToDto(sampleRank);
        bindingResult = new BeanPropertyBindingResult(sampleRankDTO, "correctRankDTO");
    }

    @Test
    public void testGetAllEntities() {
        Mockito.when(dao.getAllRecords()).thenReturn(Arrays.asList(sampleRank));

        ResponseEntity<Response> result = service.getAllEntities();

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<AcademicRankDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testGetEntity() {
        Mockito.when(dao.getRecord("1")).thenReturn(sampleRank);

        ResponseEntity<Response> result = service.getEntity("1");

        assertThat(Objects.requireNonNull(result.getBody()).getResponseBody(),
                samePropertyValuesAs(sampleRankDTO)
        );
    }

    @Test
    public void testAddCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleRankDTO, "correctRankDTO");

        Mockito.when(dao.addRecord(sampleRank)).thenReturn(1);

        ResponseEntity<Response> result = service.addEntity(sampleRankDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertEquals(sampleRankDTO.getNumericRank(), ((AcademicRankDTO) Objects.requireNonNull(result.getBody()).getResponseBody()).getNumericRank()),
                () -> assertEquals(sampleRankDTO.getRankName(), ((AcademicRankDTO) Objects.requireNonNull(result.getBody()).getResponseBody()).getRankName())
        );
    }

    @Test
    public void testAddCorrectEntityDBFailure() {
        bindingResult = new BeanPropertyBindingResult(sampleRankDTO, "correctRankDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleRankDTO))).thenReturn(null);

        ResponseEntity<Response> result = service.addEntity(sampleRankDTO, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testAddEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleRankDTO, "invalidRankDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.addEntity(sampleRankDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((List<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testUpdateCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleRankDTO, "correctRankDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleRankDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.updateEntity(sampleRankDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertEquals(sampleRankDTO.getNumericRank(), ((AcademicRankDTO) Objects.requireNonNull(result.getBody()).getResponseBody()).getNumericRank()),
                () -> assertEquals(sampleRankDTO.getRankName(), ((AcademicRankDTO) Objects.requireNonNull(result.getBody()).getResponseBody()).getRankName())
        );
    }

    @Test
    public void testUpdateInvalidEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleRankDTO, "sampleRankDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.updateEntity(sampleRankDTO, bindingResult);

        assertAll(
                () -> assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode()),
                () -> assertFalse(((List<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testUpdateEntityNotFound() {
        bindingResult = new BeanPropertyBindingResult(sampleRankDTO, "sampleRankDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleRankDTO))).thenReturn(0);

        ResponseEntity<Response> result = service.updateEntity(sampleRankDTO, bindingResult);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testDeleteCorrectEntity() {
        Mockito.when(dao.deleteRecord("3")).thenReturn(1);
        ResponseEntity<Response> result = service.deleteEntity("3");

        assertEquals(HttpStatus.OK, result.getStatusCode());
    }

    @Test
    public void testDeleteEntityNotFound() {
        Mockito.when(dao.deleteRecord("3")).thenReturn(0);
        ResponseEntity<Response> result = service.deleteEntity("3");

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
    }

    @Test
    public void testSearchEntities() {
        Mockito.when(dao.searchRecords("Professor")).thenReturn(Collections.singletonList(sampleRank));

        ResponseEntity<Response> result = service.searchForEntities("Professor");

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<AcademicRankDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @AfterEach
    public void cleanUp() {
        dao = null;
        mapper = null;
        service = null;
        sampleRank = null;
        sampleRankDTO = null;
        bindingResult = null;
    }
}
