package integration.universityregistrar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdlab.universityregistrar.configuration.test.IntegrationTestContextConfiguration;
import com.rdlab.universityregistrar.service.dto.StudentDTO;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IntegrationTestContextConfiguration.class})
@WebAppConfiguration
public class StudentsApiEndpointsTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private StudentDTO sampleStudentDTO;
    private StudentDTO dummyStudentDTO;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        sampleStudentDTO = StudentDTO.builder()
                .studentId(1)
                .studentName("Student one")
                .dateOfBirth(new Date(463226420L))
                .build();
         dummyStudentDTO = StudentDTO.builder()
                .studentName("Student two")
                .dateOfBirth(new Date(19840502L))
                .build();
    }

    private String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getStudentsShouldReturnListOfStudents() throws Exception {
        String expectedDateOfBirth = dateToString(sampleStudentDTO.getDateOfBirth());

        mvc.perform(get("/api/students")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].studentId", equalTo(sampleStudentDTO.getStudentId())))
                .andExpect(jsonPath("$.responseBody[0].studentName", equalTo(sampleStudentDTO.getStudentName())))
                .andExpect(jsonPath("$.responseBody[0].dateOfBirth", equalTo(expectedDateOfBirth)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getStudentShouldReturnSingleStudent() throws Exception {
        String studentId = String.valueOf(sampleStudentDTO.getStudentId());

        String expectedDateOfBirth = dateToString(sampleStudentDTO.getDateOfBirth());

        mvc.perform(get("/api/students/{id}", studentId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.studentId", is(sampleStudentDTO.getStudentId())))
                .andExpect(jsonPath("$.responseBody.studentName", is(sampleStudentDTO.getStudentName())))
                .andExpect(jsonPath("$.responseBody.dateOfBirth", is(expectedDateOfBirth)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getStudentShouldReturnNotFoundMessage() throws Exception {
        String studentId = "35";
        String expectedMessage = "item of requested type with such id not found";

        mvc.perform(get("/api/students/{id}", studentId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addStudentShouldReturnStudentWithStudentId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StudentDTO studentToAdd = dummyStudentDTO;

        String expectedDateOfBirth = dateToString(studentToAdd.getDateOfBirth());

        String studentJson = mapper.writeValueAsString(studentToAdd);

        mvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody.studentId", is(2)))
                .andExpect(jsonPath("$.responseBody.studentName", is(studentToAdd.getStudentName())))
                .andExpect(jsonPath("$.responseBody.dateOfBirth", is(expectedDateOfBirth)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addStudentShouldReturnParsingErrorMessage() throws Exception {
        String expectedMessage = "Cannot parse input data, please check input data format";
        String studentJson = "{\"studentName\":\"some name\",\"dateOfBirth\":\"date\"}";

        mvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addStudentWithStudentNameRegExValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StudentDTO studentToAdd = StudentDTO.builder()
                .studentName("#$%^&*")
                .dateOfBirth(dummyStudentDTO.getDateOfBirth())
                .build();

        String studentJson = mapper.writeValueAsString(studentToAdd);

        String expectedReturnMessage = "course title can " +
                "contain only letters in range (a-z, A-Z) and whitespaces";

        mvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addStudentWithEmptyStudentNameValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StudentDTO studentToAdd = StudentDTO.builder()
                .studentName("")
                .dateOfBirth(dummyStudentDTO.getDateOfBirth())
                .build();

        String studentJson = mapper.writeValueAsString(studentToAdd);

        String expectedReturnMessage = "student name name cannot be empty";

        mvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addStudentWithStudentNameSizeValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder studentName = new StringBuilder();

        for (int i = 0; i < 46; i++) {
            studentName.append("a");
        }

        StudentDTO studentToAdd = StudentDTO.builder()
                .studentName(studentName.toString())
                .dateOfBirth(dummyStudentDTO.getDateOfBirth())
                .build();

        String studentJson = mapper.writeValueAsString(studentToAdd);

        String expectedReturnMessage = "student name name cannot be longer than 45 characters";

        mvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addStudentWithStudentDateOfBirthInFutureValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StudentDTO studentToAdd = StudentDTO.builder()
                .studentName(dummyStudentDTO.getStudentName())
                .dateOfBirth(new Date(1745539200000L))
                .build();

        String studentJson = mapper.writeValueAsString(studentToAdd);

        String expectedReturnMessage = "student date of birth cannot be in the future";

        mvc.perform(post("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(studentJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateStudentShouldReturnUpdatedStudent() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StudentDTO studentToUpdate = dummyStudentDTO;
        studentToUpdate.setStudentId(1);

        String lecturerJson = mapper.writeValueAsString(studentToUpdate);

        String expectedDateOfBirth = dateToString(studentToUpdate.getDateOfBirth());

        mvc.perform(put("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.studentId", is(studentToUpdate.getStudentId())))
                .andExpect(jsonPath("$.responseBody.studentName", is(studentToUpdate.getStudentName())))
                .andExpect(jsonPath("$.responseBody.dateOfBirth", is(expectedDateOfBirth)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateStudentShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        ObjectMapper mapper = new ObjectMapper();

        StudentDTO studentToUpdate = dummyStudentDTO;
        studentToUpdate.setStudentId(42);

        String lecturerJson = mapper.writeValueAsString(studentToUpdate);

        mvc.perform(put("/api/students")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteStudentShouldReturnDeleteSuccessMessage() throws Exception {
        String deleteSuccessMessage = "item deleted successfully";

        mvc.perform(delete("/api/students/{id}", String.valueOf(sampleStudentDTO.getStudentId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(deleteSuccessMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteStudentShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        String studentId = "5";

        mvc.perform(delete("/api/students/{id}", studentId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchStudentByNameShouldReturnListOfFoundStudents() throws Exception {
        String searchCriterion = "Student";

        mvc.perform(get("/api/students/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].studentName", equalTo(sampleStudentDTO.getStudentName())));
    }

    @AfterEach
    public void cleanUp() {
        sampleStudentDTO = null;
        mvc = null;
    }
}
