package unit.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.UserDAOImpl;
import com.rdlab.universityregistrar.model.entity.Student;
import com.rdlab.universityregistrar.model.entity.User;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.UserDTO;
import com.rdlab.universityregistrar.service.implementation.UserServiceImpl;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.UserDTOEntityMapper;
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

public class UserServiceTest {
    private AbstractService<User, UserDTO> service;
    private DAO<User> dao;
    private DTOEntityMapper<User, UserDTO> mapper;
    private User sampleUser;
    private UserDTO sampleUserDTO;
    private BindingResult bindingResult;

    @BeforeEach
    public void setupData() {
        dao = Mockito.mock(UserDAOImpl.class);
        mapper = Mappers.getMapper(UserDTOEntityMapper.class);
        service = new UserServiceImpl(dao);
        service.setMapper(mapper);
        sampleUser = User.builder()
                .login("sample_login")
                .password("pwd^%$")
                .isActive(true)
                .student(Student.builder().studentId(5).build())
                .build();
        sampleUserDTO = mapper.entityToDto(sampleUser);
        bindingResult = new BeanPropertyBindingResult(sampleUserDTO, "sampleUserDTO");
    }


    @Test
    public void testGetAllEntities() {
        Mockito.when(dao.getAllRecords()).thenReturn(Arrays.asList(sampleUser));
        ResponseEntity<Response> result = service.getAllEntities();

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<UserDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testGetEntity() {
        Mockito.when(dao.getRecord("sample_login")).thenReturn(sampleUser);
        ResponseEntity<Response> result = service.getEntity("sample_login");

        assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleUserDTO));
    }

    @Test
    public void testAddCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleUserDTO, "sampleUserDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleUserDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.addEntity(sampleUserDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.CREATED),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleUserDTO))
        );
    }

    @Test
    public void testAddCorrectEntityDBFailure() {
        bindingResult = new BeanPropertyBindingResult(sampleUserDTO, "sampleUserDTO");

        Mockito.when(dao.addRecord(mapper.dtoToEntity(sampleUserDTO))).thenReturn(null);
        ResponseEntity<Response> result = service.addEntity(sampleUserDTO, bindingResult);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
    }

    @Test
    public void testAddEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleUserDTO, "sampleUserDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.addEntity(sampleUserDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.BAD_REQUEST),
                () -> assertFalse(((ArrayList<String>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @Test
    public void testUpdateCorrectEntity() {
        bindingResult = new BeanPropertyBindingResult(sampleUserDTO, "sampleUserDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleUserDTO))).thenReturn(1);

        ResponseEntity<Response> result = service.updateEntity(sampleUserDTO, bindingResult);

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertThat((Objects.requireNonNull(result.getBody()).getResponseBody()), samePropertyValuesAs(sampleUserDTO))
        );
    }

    @Test
    public void testUpdateEntityShouldReturnNotFoundStatusCode() {
        bindingResult = new BeanPropertyBindingResult(sampleUserDTO, "sampleUserDTO");

        Mockito.when(dao.updateRecord(mapper.dtoToEntity(sampleUserDTO))).thenReturn(0);

        ResponseEntity<Response> result = service.updateEntity(sampleUserDTO, bindingResult);


        assertEquals(result.getStatusCode(), HttpStatus.NOT_FOUND);

    }

    @Test
    public void testUpdateEntityWithValidationConstraints() {
        bindingResult = new BeanPropertyBindingResult(sampleUserDTO, "sampleUserDTO");
        bindingResult.reject("error.code", "default message");

        ResponseEntity<Response> result = service.updateEntity(sampleUserDTO, bindingResult);

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
        Mockito.when(dao.searchRecords("FAL2020")).thenReturn(Arrays.asList(sampleUser));

        ResponseEntity<Response> result = service.searchForEntities("FAL2020");

        assertAll(
                () -> assertEquals(result.getStatusCode(), HttpStatus.OK),
                () -> assertFalse(((List<UserDTO>) Objects.requireNonNull(result.getBody()).getResponseBody()).isEmpty())
        );
    }

    @AfterEach
    public void cleanUp() {
        dao = null;
        service = null;
        mapper = null;
        sampleUser = null;
        sampleUserDTO = null;
        bindingResult = null;
    }
}
