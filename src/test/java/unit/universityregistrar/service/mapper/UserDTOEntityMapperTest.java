package unit.universityregistrar.service.mapper;

import com.rdlab.universityregistrar.model.entity.Student;
import com.rdlab.universityregistrar.model.entity.User;
import com.rdlab.universityregistrar.service.dto.UserDTO;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import com.rdlab.universityregistrar.service.mapper.UserDTOEntityMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class UserDTOEntityMapperTest {
    private DTOEntityMapper<User, UserDTO> mapper;
    private User defaultUser;
    private UserDTO defaultUserDTO;
    private List<User> studentList;

    @BeforeEach
    public void setUp() {
        mapper = Mappers.getMapper(UserDTOEntityMapper.class);
        defaultUser = User.builder()
                .login("sample_login")
                .password("pwd^%$")
                .isActive(true)
                .student(Student.builder().studentId(5).build())
                .build();
        defaultUserDTO = UserDTO.builder()
                .login("sample_login")
                .password("pwd^%$")
                .isActive(true)
                .studentId(5)
                .build();
        studentList = Collections.singletonList(defaultUser);
    }

    @Test
    public void testDtoToEntityMapping() {
        User resultEntity = mapper.dtoToEntity(defaultUserDTO);

        assertThat(resultEntity, samePropertyValuesAs(defaultUser));
    }

    @Test
    public void testEntityToDtoMapping() {
        UserDTO resultDTO = mapper.entityToDto(defaultUser);

        assertThat(resultDTO, samePropertyValuesAs(defaultUserDTO));
    }

    @Test
    public void testEntityListToDtoList() {
        List<UserDTO> dtoList = mapper.entityListToDtoList(studentList);

        assertFalse(dtoList.isEmpty());
    }

    @AfterEach
    public void cleanUp() {
        mapper = null;
        defaultUser = null;
        defaultUserDTO = null;
        studentList = null;
    }
}
