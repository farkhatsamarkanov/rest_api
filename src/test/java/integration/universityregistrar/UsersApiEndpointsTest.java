package integration.universityregistrar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdlab.universityregistrar.configuration.test.IntegrationTestContextConfiguration;
import com.rdlab.universityregistrar.service.dto.UserDTO;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IntegrationTestContextConfiguration.class})
@WebAppConfiguration
public class UsersApiEndpointsTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private UserDTO sampleUserDTO;
    private UserDTO dummyUserDTO;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        sampleUserDTO = UserDTO.builder()
                .login("user_1")
                .password("pa$$word")
                .isActive(false)
                .studentId(1)
                .build();
        dummyUserDTO = UserDTO.builder()
                .login("user_2")
                .password("password")
                .isActive(false)
                .studentId(1)
                .build();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getUsersShouldReturnListOfUsers() throws Exception {
        mvc.perform(get("/api/users")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].login", equalTo(sampleUserDTO.getLogin())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getUserShouldReturnSingleUser() throws Exception {
        mvc.perform(get("/api/users/{id}", sampleUserDTO.getLogin()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.login", is(sampleUserDTO.getLogin())))
                .andExpect(jsonPath("$.responseBody.password", is(sampleUserDTO.getPassword())))
                .andExpect(jsonPath("$.responseBody.isActive", is(sampleUserDTO.getIsActive())))
                .andExpect(jsonPath("$.responseBody.studentId", is(sampleUserDTO.getStudentId())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getUserShouldReturnNotFoundMessage() throws Exception {
        String userId = "user35";
        String expectedMessage = "item of requested type with such id not found";

        mvc.perform(get("/api/users/{id}", userId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addUserShouldReturnUserWithEntryId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDTO userToAdd = dummyUserDTO;

        String userJson = mapper.writeValueAsString(userToAdd);

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody.login", is(userToAdd.getLogin())))
                .andExpect(jsonPath("$.responseBody.password", is(userToAdd.getPassword())))
                .andExpect(jsonPath("$.responseBody.isActive", is(userToAdd.getIsActive())))
                .andExpect(jsonPath("$.responseBody.studentId", is(userToAdd.getStudentId())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addUserShouldReturnAlreadyExistsMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDTO userToAdd = sampleUserDTO;

        String expectedMessage = "Entity with such id already exists!";
        String userJson = mapper.writeValueAsString(userToAdd);


        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addUserShouldReturnParsingErrorMessage() throws Exception {
        String expectedMessage = "Cannot parse input data, please check input data format";
        String userJson = "{\"login\":\"f\",\"password\":\"password\"" +
                "\"isActive\":\"true\"," +
                "\"studentId\":\"f\"}";

        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addUserWithUserLoginAndPasswordRegExValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDTO userToAdd = UserDTO.builder()
                .login("#$%^&")
                .password(")))))")
                .isActive(dummyUserDTO.getIsActive())
                .studentId(dummyUserDTO.getStudentId())
                .build();

        String userJson = mapper.writeValueAsString(userToAdd);

        String expectedInvalidLoginReturnMessage = "login can " +
                "contain only letters in range (a-z, A-Z), whitespaces" +
                "numbers from 0 to 9 and _ symbols";
        String expectedInvalidPasswordReturnMessage = "password can " +
                "contain only letters in range (a-z, A-Z), whitespaces" +
                "numbers from 0 to 9 and .,_,@,#,$,*,^ symbols";

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidLoginReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidPasswordReturnMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addUserWithEmptyValuesValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        UserDTO userToAdd = UserDTO.builder()
                .login("")
                .password("")
                .isActive(null)
                .studentId(null)
                .build();

        String userJson = mapper.writeValueAsString(userToAdd);

        String expectedEmptyLoginReturnMessage = "login cannot be empty";
        String expectedEmptyPasswordReturnMessage = "password cannot be empty";
        String expectedEmptyIsActiveReturnMessage = "isActive flag cannot be empty";
        String expectedEmptyStudentIdReturnMessage = "student id cannot be empty";

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(4)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptyLoginReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptyPasswordReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptyIsActiveReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptyStudentIdReturnMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addUserWithLoginAndPasswordSizeValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder userLogin = new StringBuilder();
        for (int i = 0; i < 46; i++) {
            userLogin.append("a");
        }
        StringBuilder userPassword = new StringBuilder();
        for (int i = 0; i < 46; i++) {
            userPassword.append("a");
        }

        UserDTO userToAdd = UserDTO.builder()
                .login(userLogin.toString())
                .password(userPassword.toString())
                .isActive(dummyUserDTO.getIsActive())
                .studentId(dummyUserDTO.getStudentId())
                .build();

        String userJson = mapper.writeValueAsString(userToAdd);

        String expectedInvalidLoginSizeReturnMessage = "login cannot be longer than 45 characters";
        String expectedInvalidPasswordSizeReturnMessage = "password cannot be longer than 45 characters";

        mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidLoginSizeReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidPasswordSizeReturnMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateUserShouldReturnUpdatedCourse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Integer existingUserId = 1;

        UserDTO userToUpdate = dummyUserDTO;
        userToUpdate.setUserId(existingUserId);

        String userJson = mapper.writeValueAsString(userToUpdate);

        mvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.login", is(userToUpdate.getLogin())))
                .andExpect(jsonPath("$.responseBody.password", is(userToUpdate.getPassword())))
                .andExpect(jsonPath("$.responseBody.isActive", is(userToUpdate.getIsActive())))
                .andExpect(jsonPath("$.responseBody.studentId", is(userToUpdate.getStudentId())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateUserShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";
        Integer notExistingUserId = 35;

        ObjectMapper mapper = new ObjectMapper();

        UserDTO userToUpdate = dummyUserDTO;
        userToUpdate.setUserId(notExistingUserId);

        String userJson = mapper.writeValueAsString(userToUpdate);

        mvc.perform(put("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteUserShouldReturnDeleteSuccessMessage() throws Exception {
        String deleteSuccessMessage = "item deleted successfully";

        mvc.perform(delete("/api/users/{id}", sampleUserDTO.getLogin()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(deleteSuccessMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteUserShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        String userLogin = "5";

        mvc.perform(delete("/api/users/{id}", userLogin))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchUserByLoginShouldReturnListOfFoundUsers() throws Exception {
        String searchCriterion = "user";

        mvc.perform(get("/api/users/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].login", equalTo(sampleUserDTO.getLogin())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchUserByStudentIdShouldReturnListOfFoundUsers() throws Exception {
        String searchCriterion = "1";

        mvc.perform(get("/api/users/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].studentId", equalTo(sampleUserDTO.getStudentId())));
    }

    @AfterEach
    public void cleanUp() {
        sampleUserDTO = null;
        mvc = null;
    }
}
