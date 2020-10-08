package integration.universityregistrar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdlab.universityregistrar.configuration.test.IntegrationTestContextConfiguration;
import com.rdlab.universityregistrar.service.dto.SemesterDTO;
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
public class SemestersApiEndpointsTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private SemesterDTO sampleSemesterDTO;
    private SemesterDTO dummySemesterDTO;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        sampleSemesterDTO = SemesterDTO.builder()
                .semesterId("FAL2020")
                .semesterName("Fall semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(new Date(1599300020L))
                .semesterEndTime(new Date(1608890420L))
                .build();
        dummySemesterDTO = SemesterDTO.builder()
                .semesterId("SPR2020")
                .semesterName("Spring semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(new Date(1610668800000L))
                .semesterEndTime(new Date(1619308800000L))
                .build();
    }

    private String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.format(date);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getSemestersShouldReturnListOfSemesters() throws Exception {
        String expectedSemesterStartTime = dateToString(sampleSemesterDTO.getSemesterStartTime());
        String expectedSemesterEndTime = dateToString(sampleSemesterDTO.getSemesterEndTime());

        mvc.perform(get("/api/semesters")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath("$.responseBody[0].semesterId", equalTo(sampleSemesterDTO.getSemesterId())))
                .andExpect(jsonPath("$.responseBody[0].semesterName", equalTo(sampleSemesterDTO.getSemesterName())))
                .andExpect(jsonPath("$.responseBody[0].semesterYear", equalTo(sampleSemesterDTO.getSemesterYear())))
                .andExpect(jsonPath("$.responseBody[0].semesterStartTime", equalTo(expectedSemesterStartTime)))
                .andExpect(jsonPath("$.responseBody[0].semesterEndTime", equalTo(expectedSemesterEndTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getSemesterShouldReturnSingleSemester() throws Exception {
        String expectedSemesterStartTime = dateToString(sampleSemesterDTO.getSemesterStartTime());
        String expectedSemesterEndTime = dateToString(sampleSemesterDTO.getSemesterEndTime());

        mvc.perform(get("/api/semesters/{id}", sampleSemesterDTO.getSemesterId()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.semesterId", is(sampleSemesterDTO.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.semesterName", is(sampleSemesterDTO.getSemesterName())))
                .andExpect(jsonPath("$.responseBody.semesterYear", equalTo(sampleSemesterDTO.getSemesterYear())))
                .andExpect(jsonPath("$.responseBody.semesterStartTime", equalTo(expectedSemesterStartTime)))
                .andExpect(jsonPath("$.responseBody.semesterEndTime", equalTo(expectedSemesterEndTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getSemesterShouldReturnNotFoundMessage() throws Exception {
        String semesterId = "35";
        String expectedMessage = "item of requested type with such id not found";

        mvc.perform(get("/api/semesters/{id}", semesterId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addSemesterShouldReturnSemesterWithSemesterId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SemesterDTO semesterToAdd = dummySemesterDTO;

        String expectedSemesterStartTime = dateToString(semesterToAdd.getSemesterStartTime());
        String expectedSemesterEndTime = dateToString(semesterToAdd.getSemesterEndTime());

        String semesterJson = mapper.writeValueAsString(semesterToAdd);

        mvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody.semesterId", is(semesterToAdd.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.semesterName", is(semesterToAdd.getSemesterName())))
                .andExpect(jsonPath("$.responseBody.semesterYear", equalTo(semesterToAdd.getSemesterYear())))
                .andExpect(jsonPath("$.responseBody.semesterStartTime", equalTo(expectedSemesterStartTime)))
                .andExpect(jsonPath("$.responseBody.semesterEndTime", equalTo(expectedSemesterEndTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addSemesterShouldReturnAlreadyExistsMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SemesterDTO semesterToAdd = dummySemesterDTO;
        semesterToAdd.setSemesterId(sampleSemesterDTO.getSemesterId());

        String expectedMessage = "Entity with such id already exists!";
        String semesterJson = mapper.writeValueAsString(semesterToAdd);


        mvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addSemesterShouldReturnParsingErrorMessage() throws Exception {
        String expectedMessage = "Cannot parse input data, please check input data format";
        String semesterJson = "{\"semesterId\":\"some id\",\"semesterName\":\"some name\"," +
                "\"semesterYear\":\"2020\"," +
                "\"semesterStartTime\":\"20200101\"," +
                "\"semesterEndTime\":\"20201212\"}";

        mvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addSemesterWithSemesterNameAndSemesterIdRegExValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SemesterDTO semesterToAdd = SemesterDTO.builder()
                .semesterId("$%^&*")
                .semesterName("#$^&*(")
                .semesterYear(dummySemesterDTO.getSemesterYear())
                .semesterStartTime(dummySemesterDTO.getSemesterStartTime())
                .semesterEndTime(dummySemesterDTO.getSemesterEndTime())
                .build();

        String semesterJson = mapper.writeValueAsString(semesterToAdd);

        String expectedInvalidSemesterIdReturnMessage = "semesterId can " +
                "contain only letters in range (a-z, A-Z) and " +
                "numbers from 0 to 9";
        String expectedInvalidSemesterNameReturnMessage = "location can " +
                "contain only letters in range (a-z, A-Z), " +
                "numbers from 0 to 9, whitespaces and .-)( symbols";

        mvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidSemesterIdReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidSemesterNameReturnMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addSemesterWithEmptyValuesValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SemesterDTO semesterToAdd = SemesterDTO.builder()
                .semesterId("")
                .semesterName("")
                .semesterYear(null)
                .semesterStartTime(null)
                .semesterEndTime(null)
                .build();

        String semesterJson = mapper.writeValueAsString(semesterToAdd);

        String expectedEmptySemesterIdReturnMessage = "semesterId cannot be empty";
        String expectedEmptySemesterNameReturnMessage = "semester name cannot be empty";
        String expectedNullSemesterYearReturnMessage = "semester year cannot be null";
        String expectedNullSemesterStartTimeReturnMessage = "semester start time cannot be null";
        String expectedNullSemesterEndTimeReturnMessage = "semester end time cannot be null";

        mvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(5)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptySemesterIdReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedEmptySemesterNameReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedNullSemesterYearReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedNullSemesterStartTimeReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedNullSemesterEndTimeReturnMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addSemesterWithSemesterNameAndSemesterIdSizeValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder semesterName = new StringBuilder();
        StringBuilder semesterId = new StringBuilder();

        for (int i = 0; i < 46; i++) {
            semesterName.append("a");
        }

        for (int i = 0; i < 8; i++) {
            semesterId.append("a");
        }

        SemesterDTO semesterToAdd = SemesterDTO.builder()
                .semesterId(semesterId.toString())
                .semesterName(semesterName.toString())
                .semesterYear(dummySemesterDTO.getSemesterYear())
                .semesterStartTime(dummySemesterDTO.getSemesterStartTime())
                .semesterEndTime(dummySemesterDTO.getSemesterEndTime())
                .build();

        String semesterJson = mapper.writeValueAsString(semesterToAdd);

        String expectedInvalidSemesterIdReturnMessage = "semesterId cannot be longer than 7 characters";
        String expectedInvalidSemesterNameReturnMessage = "semesterName cannot be longer than 30 characters";

        mvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidSemesterIdReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidSemesterNameReturnMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addSemesterWithSemesterStartAndEndTimeInPastValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SemesterDTO semesterToAdd = SemesterDTO.builder()
                .semesterId(dummySemesterDTO.getSemesterId())
                .semesterName(dummySemesterDTO.getSemesterName())
                .semesterYear(dummySemesterDTO.getSemesterYear())
                .semesterStartTime(new Date(683592473000L))
                .semesterEndTime(new Date(778286873000L))
                .build();

        String semesterJson = mapper.writeValueAsString(semesterToAdd);

        String expectedInvalidStartTimeReturnMessage = "semester begin time cannot be in the past";
        String expectedInvalidEndTimeReturnMessage = "semester end time cannot be in the past";

        mvc.perform(post("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidStartTimeReturnMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedInvalidEndTimeReturnMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateSemesterShouldReturnUpdatedSemester() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        SemesterDTO semesterToUpdate = SemesterDTO.builder()
                .entryId(1)
                .semesterId(sampleSemesterDTO.getSemesterId())
                .semesterName("Another fall semester of 2020")
                .semesterYear(dummySemesterDTO.getSemesterYear())
                .semesterStartTime(dummySemesterDTO.getSemesterStartTime())
                .semesterEndTime(dummySemesterDTO.getSemesterEndTime())
                .build();

        String semesterJson = mapper.writeValueAsString(semesterToUpdate);

        String expectedStartTime = dateToString(semesterToUpdate.getSemesterStartTime());
        String expectedEndTime = dateToString(semesterToUpdate.getSemesterEndTime());

        mvc.perform(put("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.semesterId", is(semesterToUpdate.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.semesterName", is(semesterToUpdate.getSemesterName())))
                .andExpect(jsonPath("$.responseBody.semesterYear", is(semesterToUpdate.getSemesterYear())))
                .andExpect(jsonPath("$.responseBody.semesterStartTime", is(expectedStartTime)))
                .andExpect(jsonPath("$.responseBody.semesterEndTime", is(expectedEndTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateSemesterShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        ObjectMapper mapper = new ObjectMapper();

        SemesterDTO semesterToUpdate = dummySemesterDTO;
        semesterToUpdate.setEntryId(56);

        String semesterJson = mapper.writeValueAsString(semesterToUpdate);

        mvc.perform(put("/api/semesters")
                .contentType(MediaType.APPLICATION_JSON)
                .content(semesterJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteLecturerShouldReturnDeleteSuccessMessage() throws Exception {
        String deleteSuccessMessage = "item deleted successfully";

        mvc.perform(delete("/api/semesters/{id}", sampleSemesterDTO.getSemesterId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(deleteSuccessMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteLecturerShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        String semesterId = "SPR2021";

        mvc.perform(delete("/api/semesters/{id}", semesterId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchSemesterByNameShouldReturnListOfFoundSemesters() throws Exception {
        String searchCriterion = "fall";

        mvc.perform(get("/api/semesters/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].semesterName", equalTo(sampleSemesterDTO.getSemesterName())));
    }

    @AfterEach
    public void cleanUp() {
        sampleSemesterDTO = null;
        mvc = null;
    }
}
