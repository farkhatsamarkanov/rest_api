package integration.universityregistrar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdlab.universityregistrar.configuration.test.IntegrationTestContextConfiguration;
import com.rdlab.universityregistrar.service.dto.LecturerDTO;
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
public class LecturersApiEndpointsTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private LecturerDTO sampleLecturerDTO;
    private LecturerDTO dummyLecturerDTO;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        sampleLecturerDTO = LecturerDTO.builder()
                .lecturerName("John Doe")
                .dateOfBirth(new Date(461885632L))
                .numericAcademicRank(1)
                .build();
        dummyLecturerDTO = LecturerDTO.builder()
                .lecturerName("some name")
                .dateOfBirth(new Date(177802432L))
                .numericAcademicRank(1)
                .build();
    }

    private String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getLecturersShouldReturnListOfLecturers() throws Exception {
        String expectedDateOfBirth = dateToString(sampleLecturerDTO.getDateOfBirth());

        mvc.perform(get("/api/lecturers")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].lecturerName", equalTo(sampleLecturerDTO.getLecturerName())))
                .andExpect(jsonPath("$.responseBody[0].numericAcademicRank", equalTo(sampleLecturerDTO.getNumericAcademicRank())))
                .andExpect(jsonPath("$.responseBody[0].dateOfBirth", equalTo(expectedDateOfBirth)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getLecturerShouldReturnSingleLecturer() throws Exception {
        String lecturerId = "1";

        String expectedDateOfBirth = dateToString(sampleLecturerDTO.getDateOfBirth());

        mvc.perform(get("/api/lecturers/{id}", lecturerId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.lecturerId", is(1)))
                .andExpect(jsonPath("$.responseBody.lecturerName", is(sampleLecturerDTO.getLecturerName())))
                .andExpect(jsonPath("$.responseBody.dateOfBirth", is(expectedDateOfBirth)))
                .andExpect(jsonPath("$.responseBody.numericAcademicRank", is(sampleLecturerDTO.getNumericAcademicRank())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getLecturerShouldReturnNotFoundMessage() throws Exception {
        String lecturerId = "35";
        String expectedMessage = "item of requested type with such id not found";

        mvc.perform(get("/api/lecturers/{id}", lecturerId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addLecturerShouldReturnLecturerWithLecturerId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        LecturerDTO lecturerToAdd = dummyLecturerDTO;

        String expectedDateOfBirth = dateToString(lecturerToAdd.getDateOfBirth());

        String lecturerJson = mapper.writeValueAsString(lecturerToAdd);

        mvc.perform(post("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody.lecturerId", is(2)))
                .andExpect(jsonPath("$.responseBody.lecturerName", is(lecturerToAdd.getLecturerName())))
                .andExpect(jsonPath("$.responseBody.dateOfBirth", is(expectedDateOfBirth)))
                .andExpect(jsonPath("$.responseBody.numericAcademicRank", is(lecturerToAdd.getNumericAcademicRank())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addLecturerShouldReturnParsingErrorMessage() throws Exception {
        String expectedMessage = "Cannot parse input data, please check input data format";
        String lecturerJson = "{\"lecturerName\":\"some name\",\"dateOfBirth\":\"19841212\",\"numericAcademicRank\":\"5\"}";


        mvc.perform(post("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addLecturerWithLecturerNameRegExValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        LecturerDTO lecturerToAdd = LecturerDTO.builder()
                .lecturerName("#$%^&*")
                .dateOfBirth(dummyLecturerDTO.getDateOfBirth())
                .numericAcademicRank(dummyLecturerDTO.getNumericAcademicRank())
                .build();

        String lecturerJson = mapper.writeValueAsString(lecturerToAdd);

        String expectedReturnMessage = "course title can " +
                "contain only letters in range (a-z, A-Z) and whitespaces";

        mvc.perform(post("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addLecturerWithEmptyLecturerNameAndRankValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        LecturerDTO lecturerToAdd = LecturerDTO.builder()
                .lecturerName("")
                .dateOfBirth(dummyLecturerDTO.getDateOfBirth())
                .numericAcademicRank(null)
                .build();

        String lecturerJson = mapper.writeValueAsString(lecturerToAdd);

        String expectedEmptyNameMessage = "lecturer name cannot be empty";
        String expectedEmptyRankMessage = "lecturer academic rank cannot be null";

        mvc.perform(post("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptyNameMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptyRankMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addLecturerWithLecturerNameSizeValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder lecturerName = new StringBuilder();

        for (int i = 0; i < 46; i++) {
            lecturerName.append("a");
        }

        LecturerDTO lecturerToAdd = LecturerDTO.builder()
                .lecturerName(lecturerName.toString())
                .dateOfBirth(dummyLecturerDTO.getDateOfBirth())
                .numericAcademicRank(dummyLecturerDTO.getNumericAcademicRank())
                .build();

        String lecturerJson = mapper.writeValueAsString(lecturerToAdd);

        String expectedReturnMessage = "lecturer name cannot be longer than 45 characters";

        mvc.perform(post("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addLecturerWithLecturerDateOfBirthInFutureValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        LecturerDTO lecturerToAdd = LecturerDTO.builder()
                .lecturerName(dummyLecturerDTO.getLecturerName())
                .dateOfBirth(new Date(1756538422000L))
                .numericAcademicRank(dummyLecturerDTO.getNumericAcademicRank())
                .build();

        String lecturerJson = mapper.writeValueAsString(lecturerToAdd);

        String expectedReturnMessage = "lecturer's date of birth cannot be in the future";

        mvc.perform(post("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateLecturerShouldReturnUpdatedLecturer() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        LecturerDTO lecturerToUpdate = LecturerDTO.builder()
                .lecturerId(1)
                .lecturerName(dummyLecturerDTO.getLecturerName())
                .dateOfBirth(dummyLecturerDTO.getDateOfBirth())
                .numericAcademicRank(dummyLecturerDTO.getNumericAcademicRank())
                .build();

        String lecturerJson = mapper.writeValueAsString(lecturerToUpdate);

        String expectedDateOfBirth = dateToString(lecturerToUpdate.getDateOfBirth());

        mvc.perform(put("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.lecturerId", is(lecturerToUpdate.getLecturerId())))
                .andExpect(jsonPath("$.responseBody.lecturerName", is(lecturerToUpdate.getLecturerName())))
                .andExpect(jsonPath("$.responseBody.dateOfBirth", is(expectedDateOfBirth)))
                .andExpect(jsonPath("$.responseBody.numericAcademicRank", is(lecturerToUpdate.getNumericAcademicRank())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateLecturerShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        ObjectMapper mapper = new ObjectMapper();

        LecturerDTO lecturerToUpdate = LecturerDTO.builder()
                .lecturerId(6)
                .lecturerName(dummyLecturerDTO.getLecturerName())
                .dateOfBirth(dummyLecturerDTO.getDateOfBirth())
                .numericAcademicRank(dummyLecturerDTO.getNumericAcademicRank())
                .build();

        String lecturerJson = mapper.writeValueAsString(lecturerToUpdate);

        mvc.perform(put("/api/lecturers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteLecturerShouldReturnDeleteSuccessMessage() throws Exception {
        String deleteSuccessMessage = "item deleted successfully";

        String lecturerId = "1";

        mvc.perform(delete("/api/lecturers/{id}", lecturerId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(deleteSuccessMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteLecturerShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        String lecturerId = "5";

        mvc.perform(delete("/api/lecturers/{id}", lecturerId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchLecturerByNameShouldReturnListOfFoundLecturers() throws Exception {
        String searchCriterion = "John";

        mvc.perform(get("/api/lecturers/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].lecturerName", equalTo(sampleLecturerDTO.getLecturerName())));
    }

    @AfterEach
    public void cleanUp() {
        sampleLecturerDTO = null;
        mvc = null;
    }
}
