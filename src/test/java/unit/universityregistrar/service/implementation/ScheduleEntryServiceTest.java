package unit.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.ScheduleEntryDAOImpl;
import com.rdlab.universityregistrar.model.dao.implementation.UserDAOImpl;
import com.rdlab.universityregistrar.model.entity.*;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.ScheduleEntryDTO;
import com.rdlab.universityregistrar.service.implementation.ScheduleEntryServiceImpl;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.ScheduleEntryDTOEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;

public class ScheduleEntryServiceTest {
    private AbstractService<ScheduleEntry, ScheduleEntryDTO> service;
    private DAO<ScheduleEntry> dao;
    private DAO<User> userDAO;
    private DTOEntityMapper<ScheduleEntry, ScheduleEntryDTO> mapper;
    private ScheduleEntry sampleScheduleEntry;
    private ScheduleEntryDTO sampleScheduleEntryDTO;
    private BindingResult bindingResult;

    @BeforeEach
    public void setupData() {
        dao = Mockito.mock(ScheduleEntryDAOImpl.class);
        userDAO = Mockito.mock(UserDAOImpl.class);
        mapper = Mappers.getMapper(ScheduleEntryDTOEntityMapper.class);
        service = new ScheduleEntryServiceImpl(dao);
        service.setMapper(mapper);
        ((ScheduleEntryServiceImpl) service).setUserDAO(userDAO);
        sampleScheduleEntry = ScheduleEntry.builder()
                .student(Student.builder().studentId(1).build())
                .lecturer(Lecturer.builder().lecturerId(1).build())
                .course(Course.builder().courseId(1).build())
                .time(1661078705000L)
                .location("Room H3")
                .semester(Semester.builder().semesterId("FAL2020").build())
                .entryId(1)
                .build();
        sampleScheduleEntryDTO = mapper.entityToDto(sampleScheduleEntry);
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntry, "sampleScheduleEntryDTO");
    }

    @Test
    public void testGetAllEntities() {
        Mockito.when(dao.getAllRecords()).thenReturn(Arrays.asList(sampleScheduleEntry));

        ResponseEntity<Response> result = service.getAllEntities();

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<ScheduleEntryDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testGetEntity() {
        Mockito.when(dao.getRecord("1")).thenReturn(sampleScheduleEntry);

        ResponseEntity<Response> result = service.getEntity("1");

        assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleScheduleEntryDTO));
    }

    @Test
    public void testAddCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleScheduleEntryDTO))).thenReturn(1);
        Mockito.when(((UserDAOImpl) userDAO).getUserByStudentId(sampleScheduleEntryDTO.getStudentId())).thenReturn(User.builder().isActive(true).build());

        ResponseEntity<Response> result = service.addEntity(sampleScheduleEntryDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleScheduleEntryDTO))
        );
    }

    @Test
    public void testAddCorrectEntityDBFailure() {
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleScheduleEntryDTO))).thenReturn(null);

        ResponseEntity<Response> result = service.addEntity(sampleScheduleEntryDTO, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testAddEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.addEntity(sampleScheduleEntryDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((ArrayList<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testAddEntityWithCourseBeginTimeInLessThanFiveDays() {
        sampleScheduleEntry.setTime(LocalDateTime.now().plusDays(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        sampleScheduleEntryDTO = mapper.entityToDto(sampleScheduleEntry);
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleScheduleEntryDTO))).thenReturn(1);
        Mockito.when(((UserDAOImpl) userDAO).getUserByStudentId(sampleScheduleEntryDTO.getStudentId())).thenReturn(User.builder().isActive(false).build());

        ResponseEntity<Response> result = service.addEntity(sampleScheduleEntryDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertTrue(Objects.requireNonNull(result.getBody()).getMessage().contains("WARNING: Course begins in less than 5 days, but student is not active"))
        );
    }

    @Test
    public void testAddStudentWithSixCoursesInSingleSemester() {
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleScheduleEntryDTO))).thenReturn(1);
        Mockito.when(((UserDAOImpl) userDAO).getUserByStudentId(sampleScheduleEntryDTO.getStudentId())).thenReturn(User.builder().isActive(false).build());
        Mockito.when(((ScheduleEntryDAOImpl) dao).getNumberOfTakenCoursesForStudent(sampleScheduleEntryDTO.getStudentId(), sampleScheduleEntryDTO.getSemesterId())).thenReturn(6L);

        ResponseEntity<Response> result = service.addEntity(sampleScheduleEntryDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertTrue(Objects.requireNonNull(result.getBody()).getMessage().contains("WARNING: Student has taken more than 5 courses in single semester!"))
        );
    }

    @Test
    public void testUpdateCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleScheduleEntryDTO))).thenReturn(1);
        Mockito.when(((UserDAOImpl) userDAO).getUserByStudentId(sampleScheduleEntryDTO.getStudentId())).thenReturn(User.builder().isActive(true).build());

        ResponseEntity<Response> result = service.updateEntity(sampleScheduleEntryDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleScheduleEntryDTO))
        );
    }

    @Test
    public void testUpdateEntityShouldReturnNotFoundStatusCode() {
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleScheduleEntryDTO))).thenReturn(0);
        Mockito.when(((UserDAOImpl) userDAO).getUserByStudentId(sampleScheduleEntryDTO.getStudentId())).thenReturn(User.builder().isActive(true).build());

        ResponseEntity<Response> result = service.updateEntity(sampleScheduleEntryDTO, bindingResult);


        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void testUpdateEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleScheduleEntryDTO, "sampleScheduleEntryDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.updateEntity(sampleScheduleEntryDTO, bindingResult);

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
        Mockito.when(dao.searchRecords("FAL2020")).thenReturn(Arrays.asList(sampleScheduleEntry));

        ResponseEntity<Response> result = service.searchForEntities("FAL2020");

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<ScheduleEntryDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @AfterEach
    public void cleanUp() {
        dao = null;
        service = null;
        mapper = null;
        sampleScheduleEntry = null;
        sampleScheduleEntryDTO = null;
        bindingResult = null;
    }
}
