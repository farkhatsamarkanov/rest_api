package integration.universityregistrar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdlab.universityregistrar.configuration.test.IntegrationTestContextConfiguration;
import com.rdlab.universityregistrar.service.dto.ScheduleEntryDTO;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.TimeZone;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {IntegrationTestContextConfiguration.class})
@WebAppConfiguration
public class SchedulesApiEndpointsTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private ScheduleEntryDTO sampleScheduleEntryDTO;
    private ScheduleEntryDTO dummyScheduleEntryDTO;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        sampleScheduleEntryDTO = ScheduleEntryDTO.builder()
                .studentId(1)
                .lecturerId(1)
                .courseId(1)
                .time(new Date(1609855628000L))
                .location("Room A1")
                .semesterId("FAL2020")
                .entryId(1)
                .build();
        dummyScheduleEntryDTO = ScheduleEntryDTO.builder()
                .studentId(1)
                .lecturerId(1)
                .courseId(3)
                .time(new Date(1620223628000L))
                .location("Room B1")
                .semesterId("FAL2020")
                .build();
    }

    private String dateToString(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Almaty"));
        return dateFormat.format(date);
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getScheduleEntriesShouldReturnListOfScheduleEntries() throws Exception {
        mvc.perform(get("/api/schedules")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody").isNotEmpty())
                .andExpect(jsonPath("$.responseBody", hasSize(5)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getScheduleEntryShouldReturnSingleScheduleEntry() throws Exception {
        String scheduleEntryId = String.valueOf(sampleScheduleEntryDTO.getEntryId());

        String expectedTime = dateToString(sampleScheduleEntryDTO.getTime());

        mvc.perform(get("/api/schedules/{id}", scheduleEntryId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.entryId", is(sampleScheduleEntryDTO.getEntryId())))
                .andExpect(jsonPath("$.responseBody.studentId", is(sampleScheduleEntryDTO.getStudentId())))
                .andExpect(jsonPath("$.responseBody.lecturerId", is(sampleScheduleEntryDTO.getLecturerId())))
                .andExpect(jsonPath("$.responseBody.courseId", is(sampleScheduleEntryDTO.getCourseId())))
                .andExpect(jsonPath("$.responseBody.location", equalTo(sampleScheduleEntryDTO.getLocation())))
                .andExpect(jsonPath("$.responseBody.semesterId", equalTo(sampleScheduleEntryDTO.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.time", equalTo(expectedTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getScheduleEntryShouldReturnNotFoundMessage() throws Exception {
        String scheduleEntryId = "35";
        String expectedMessage = "item of requested type with such id not found";

        mvc.perform(get("/api/schedules/{id}", scheduleEntryId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryShouldReturnScheduleEntryWithEntryId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ScheduleEntryDTO scheduleEntryToAdd = dummyScheduleEntryDTO;

        String expectedTime = dateToString(scheduleEntryToAdd.getTime());

        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToAdd);

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody.entryId", is(6)))
                .andExpect(jsonPath("$.responseBody.studentId", is(scheduleEntryToAdd.getStudentId())))
                .andExpect(jsonPath("$.responseBody.lecturerId", is(scheduleEntryToAdd.getLecturerId())))
                .andExpect(jsonPath("$.responseBody.courseId", is(scheduleEntryToAdd.getCourseId())))
                .andExpect(jsonPath("$.responseBody.location", equalTo(scheduleEntryToAdd.getLocation())))
                .andExpect(jsonPath("$.responseBody.semesterId", equalTo(scheduleEntryToAdd.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.time", equalTo(expectedTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryShouldReturnAlreadyExistsMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ScheduleEntryDTO scheduleEntryToAdd = sampleScheduleEntryDTO;
        scheduleEntryToAdd.setEntryId(null);

        String expectedMessage = "Entity with such id already exists!";
        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToAdd);

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryShouldReturnParsingErrorMessage() throws Exception {
        String expectedMessage = "Cannot parse input data, please check input data format";
        String scheduleEntryJson = "{\"studentId\":\"f\"," +
                "\"lecturerId\":\"f\"," +
                "\"courseId\":\"f\"," +
                "\"time\":\"20201212\"," +
                "\"location\":\"some location\"," +
                "\"semesterId\":\"some semester id\"}";

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryShouldReturnCourseBeginningWarningMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ScheduleEntryDTO scheduleEntryToAdd = dummyScheduleEntryDTO;
        scheduleEntryToAdd.setTime(new Date(LocalDateTime.now().plusDays(3).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()));

        String expectedTime = dateToString(scheduleEntryToAdd.getTime());

        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToAdd);

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("WARNING: Course begins in less than 5 days, but student is not active")))
                .andExpect(jsonPath("$.responseBody.entryId", is((6))))
                .andExpect(jsonPath("$.responseBody.studentId", is(scheduleEntryToAdd.getStudentId())))
                .andExpect(jsonPath("$.responseBody.lecturerId", is(scheduleEntryToAdd.getLecturerId())))
                .andExpect(jsonPath("$.responseBody.courseId", is(scheduleEntryToAdd.getCourseId())))
                .andExpect(jsonPath("$.responseBody.location", equalTo(scheduleEntryToAdd.getLocation())))
                .andExpect(jsonPath("$.responseBody.semesterId", equalTo(scheduleEntryToAdd.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.time", equalTo(expectedTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryShouldReturnCourseQuantityWarningMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ScheduleEntryDTO scheduleEntryToAdd = dummyScheduleEntryDTO;
        scheduleEntryToAdd.setCourseId(6);

        String expectedTime = dateToString(scheduleEntryToAdd.getTime());

        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToAdd);

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message", containsString("WARNING: Student has taken more than 5 courses in single semester!")))
                .andExpect(jsonPath("$.responseBody.entryId", is(6)))
                .andExpect(jsonPath("$.responseBody.studentId", is(scheduleEntryToAdd.getStudentId())))
                .andExpect(jsonPath("$.responseBody.lecturerId", is(scheduleEntryToAdd.getLecturerId())))
                .andExpect(jsonPath("$.responseBody.courseId", is(scheduleEntryToAdd.getCourseId())))
                .andExpect(jsonPath("$.responseBody.location", equalTo(scheduleEntryToAdd.getLocation())))
                .andExpect(jsonPath("$.responseBody.semesterId", equalTo(scheduleEntryToAdd.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.time", equalTo(expectedTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryWithEmptyValuesShouldReturnSixValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ScheduleEntryDTO scheduleEntryToAdd = ScheduleEntryDTO.builder()
                .studentId(null)
                .lecturerId(null)
                .courseId(null)
                .time(null)
                .location("")
                .semesterId("")
                .build();

        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToAdd);

        String expectedReturnMessageEmptyStudentId = "studentId must not be empty";
        String expectedReturnMessageEmptyLecturerId = "lecturerId must not be empty";
        String expectedReturnMessageEmptyCourseId = "courseId must not be empty";
        String expectedReturnMessageEmptyTime = "time must not be empty";
        String expectedReturnMessageEmptyLocation = "location cannot be empty";
        String expectedReturnMessageEmptySemesterId = "semesterId cannot be empty";

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(6)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedReturnMessageEmptyStudentId)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedReturnMessageEmptyLecturerId)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedReturnMessageEmptyCourseId)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedReturnMessageEmptyTime)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedReturnMessageEmptyLocation)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedReturnMessageEmptySemesterId)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryWithTimeInPastValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        ScheduleEntryDTO scheduleEntryToAdd = dummyScheduleEntryDTO;
        scheduleEntryToAdd.setTime(new Date(1440943628000L));

        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToAdd);

        String expectedReturnMessage = "course begin time cannot be in the past";

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryWithLocationAndSemesterIdSizeValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder location = new StringBuilder();
        StringBuilder semesterId = new StringBuilder();

        for (int i = 0; i < 46; i++) {
            location.append("a");
        }

        for (int i = 0; i < 8; i++) {
            semesterId.append("a");
        }

        ScheduleEntryDTO scheduleEntryToAdd = dummyScheduleEntryDTO;
        scheduleEntryToAdd.setLocation(location.toString());
        scheduleEntryToAdd.setSemesterId(semesterId.toString());

        String lecturerJson = mapper.writeValueAsString(scheduleEntryToAdd);

        String expectedLocationSizeValidationConstraintMessage = "location cannot be longer than 45 characters";
        String expectedStudentIdSizeValidationConstraintMessage = "semesterId cannot be longer than 7 characters";

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedLocationSizeValidationConstraintMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedStudentIdSizeValidationConstraintMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addScheduleEntryWithLocationAndSemesterIdRegExValidationConstraints() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        String wrongLocationAndSemesterId = "#$%^&*(";

        ScheduleEntryDTO scheduleEntryToAdd = dummyScheduleEntryDTO;
        scheduleEntryToAdd.setLocation(wrongLocationAndSemesterId);
        scheduleEntryToAdd.setSemesterId(wrongLocationAndSemesterId);

        String lecturerJson = mapper.writeValueAsString(scheduleEntryToAdd);

        String expectedLocationValidationConstraintMessage = "location can " +
                "contain only letters in range (a-z, A-Z), " +
                "numbers from 0 to 9, whitespaces and .- symbols";
        String expectedStudentIdValidationConstraintMessage = "semesterId can " +
                "contain only letters in range (a-z, A-Z) and " +
                "numbers from 0 to 9";

        mvc.perform(post("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(lecturerJson))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)))
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedLocationValidationConstraintMessage)).exists())
                .andExpect(jsonPath(String.format("$.responseBody[?(@=='%s')]", expectedStudentIdValidationConstraintMessage)).exists());
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateScheduleEntryShouldReturnUpdatedScheduleEntry() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        Integer newCourseId = 6;
        Integer entryId = 1;

        ScheduleEntryDTO scheduleEntryToUpdate = dummyScheduleEntryDTO;
        scheduleEntryToUpdate.setEntryId(entryId);
        scheduleEntryToUpdate.setCourseId(newCourseId);

        String expectedTime = dateToString(scheduleEntryToUpdate.getTime());

        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToUpdate);

        mvc.perform(put("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.entryId", is(scheduleEntryToUpdate.getEntryId())))
                .andExpect(jsonPath("$.responseBody.studentId", is(scheduleEntryToUpdate.getStudentId())))
                .andExpect(jsonPath("$.responseBody.lecturerId", is(scheduleEntryToUpdate.getLecturerId())))
                .andExpect(jsonPath("$.responseBody.courseId", is(scheduleEntryToUpdate.getCourseId())))
                .andExpect(jsonPath("$.responseBody.location", equalTo(scheduleEntryToUpdate.getLocation())))
                .andExpect(jsonPath("$.responseBody.semesterId", equalTo(scheduleEntryToUpdate.getSemesterId())))
                .andExpect(jsonPath("$.responseBody.time", equalTo(expectedTime)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateScheduleEntryShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";
        Integer notExistingEntryId = 78;

        ObjectMapper mapper = new ObjectMapper();

        ScheduleEntryDTO scheduleEntryToUpdate = dummyScheduleEntryDTO;
        scheduleEntryToUpdate.setEntryId(notExistingEntryId);

        String scheduleEntryJson = mapper.writeValueAsString(scheduleEntryToUpdate);

        mvc.perform(put("/api/schedules")
                .contentType(MediaType.APPLICATION_JSON)
                .content(scheduleEntryJson))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteScheduleEntryShouldReturnDeleteSuccessMessage() throws Exception {
        String deleteSuccessMessage = "item deleted successfully";

        String entryId = String.valueOf(sampleScheduleEntryDTO.getEntryId());

        mvc.perform(delete("/api/schedules/{id}", entryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(deleteSuccessMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteScheduleEntryShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        String notExistingEntryId = "25";

        mvc.perform(delete("/api/schedules/{id}", notExistingEntryId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchScheduleEntryByCourseIdShouldReturnListOfFoundScheduleEntries() throws Exception {
        String searchCriterion = "5";

        mvc.perform(get("/api/schedules/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].courseId", equalTo(Integer.parseInt(searchCriterion))));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchScheduleEntryByLocationShouldReturnListOfFoundScheduleEntries() throws Exception {
        String searchCriterion = "Room A1";

        mvc.perform(get("/api/schedules/search?param={searchCriterion}", searchCriterion))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(5)))
                .andExpect(jsonPath("$.responseBody[0].location", equalTo(sampleScheduleEntryDTO.getLocation())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchScheduleEntryBySemesterIdShouldReturnListOfFoundScheduleEntries() throws Exception {
        String searchCriterion = "FAL2020";

        mvc.perform(get("/api/schedules/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(5)))
                .andExpect(jsonPath("$.responseBody[0].semesterId", equalTo(sampleScheduleEntryDTO.getSemesterId())));
    }

    @AfterEach
    public void cleanUp() {
        sampleScheduleEntryDTO = null;
        mvc = null;
    }
}
