package integration.universityregistrar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdlab.universityregistrar.configuration.test.IntegrationTestContextConfiguration;
import com.rdlab.universityregistrar.service.dto.CourseDTO;
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
public class CoursesApiEndpointsTest {

    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private CourseDTO sampleCourseDTO;
    private CourseDTO dummyCourseDTO;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        sampleCourseDTO = CourseDTO.builder()
                .courseTitle("Computer Science 101")
                .courseDescription("Prerequisites: Mathematics, Calculus")
                .build();
        dummyCourseDTO = CourseDTO.builder()
                .courseTitle("some title")
                .courseDescription("some description")
                .build();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getCoursesShouldReturnListOfCourses() throws Exception {
        mvc.perform(get("/api/courses")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(6)))
                .andExpect(jsonPath("$.responseBody[0].courseTitle", equalTo(sampleCourseDTO.getCourseTitle())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getCourseShouldReturnSingleCourse() throws Exception {
        String courseId = "1";

        mvc.perform(get("/api/courses/{id}", courseId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.courseId", is(1)))
                .andExpect(jsonPath("$.responseBody.courseTitle", is(sampleCourseDTO.getCourseTitle())))
                .andExpect(jsonPath("$.responseBody.courseDescription", is(sampleCourseDTO.getCourseDescription())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getCourseShouldReturnNotFoundMessage() throws Exception {
        String courseId = "35";
        String expectedMessage = "item of requested type with such id not found";

        mvc.perform(get("/api/courses/{id}", courseId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }


    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addCourseShouldReturnCourseWithCourseId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CourseDTO courseToAdd = dummyCourseDTO;

        String courseJson = mapper.writeValueAsString(courseToAdd);

        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody.courseId", is(7)))
                .andExpect(jsonPath("$.responseBody.courseTitle", is(courseToAdd.getCourseTitle())))
                .andExpect(jsonPath("$.responseBody.courseDescription", is(courseToAdd.getCourseDescription())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addCourseShouldReturnAlreadyExistsMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CourseDTO courseToAdd = CourseDTO.builder()
                .courseTitle(sampleCourseDTO.getCourseTitle())
                .courseDescription(dummyCourseDTO.getCourseDescription())
                .build();

        String expectedMessage = "Entity with such id already exists!";
        String courseJson = mapper.writeValueAsString(courseToAdd);


        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addCourseWithCourseTitleRegExValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CourseDTO courseToAdd = CourseDTO.builder()
                .courseTitle("#$%^&*(")
                .courseDescription(dummyCourseDTO.getCourseDescription())
                .build();

        String courseJson = mapper.writeValueAsString(courseToAdd);

        String expectedReturnMessage = "course title can " +
                "contain only letters in range (a-z, A-Z), " +
                "numbers from 0 to 9 and whitespaces";

        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addCourseWithEmptyCourseTitleValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CourseDTO courseToAdd = CourseDTO.builder()
                .courseTitle("")
                .courseDescription(dummyCourseDTO.getCourseDescription())
                .build();

        String courseJson = mapper.writeValueAsString(courseToAdd);

        String expectedReturnMessage = "course title cannot be empty";

        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addCourseWithCourseTitleSizeValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder courseTitle = new StringBuilder();
        for (int i = 0; i < 46; i++) {
            courseTitle.append("a");
        }

        CourseDTO courseToAdd = CourseDTO.builder()
                .courseTitle(courseTitle.toString())
                .courseDescription(dummyCourseDTO.getCourseDescription())
                .build();

        String courseJson = mapper.writeValueAsString(courseToAdd);

        String expectedReturnMessage = "course title cannot be longer than 45 characters";

        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addCourseWithCourseDescriptionSizeValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder courseDescription = new StringBuilder();

        for (int i = 0; i < 46; i++) {
            courseDescription.append("a");
        }

        CourseDTO courseToAdd = CourseDTO.builder()
                .courseTitle(dummyCourseDTO.getCourseTitle())
                .courseDescription(courseDescription.toString())
                .build();

        String courseJson = mapper.writeValueAsString(courseToAdd);

        String expectedReturnMessage = "course description cannot be longer than 45 characters";

        mvc.perform(post("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateCourseShouldReturnUpdatedCourse() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        CourseDTO courseToUpdate = CourseDTO.builder()
                .courseId(1)
                .courseTitle(dummyCourseDTO.getCourseTitle())
                .courseDescription(dummyCourseDTO.getCourseDescription())
                .build();

        String courseJson = mapper.writeValueAsString(courseToUpdate);

        mvc.perform(put("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.courseId", is(1)))
                .andExpect(jsonPath("$.responseBody.courseTitle", is(courseToUpdate.getCourseTitle())))
                .andExpect(jsonPath("$.responseBody.courseDescription", is(courseToUpdate.getCourseDescription())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateCourseShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        ObjectMapper mapper = new ObjectMapper();

        CourseDTO courseToUpdate = CourseDTO.builder()
                .courseId(38)
                .courseTitle(dummyCourseDTO.getCourseTitle())
                .courseDescription(dummyCourseDTO.getCourseDescription())
                .build();

        String courseJson = mapper.writeValueAsString(courseToUpdate);

        mvc.perform(put("/api/courses")
                .contentType(MediaType.APPLICATION_JSON)
                .content(courseJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteCourseShouldReturnDeleteSuccessMessage() throws Exception {
        String deleteSuccessMessage = "item deleted successfully";

        String courseId = "1";

        mvc.perform(delete("/api/courses/{id}", courseId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(deleteSuccessMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteCourseShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        String courseId = "38";

        mvc.perform(delete("/api/courses/{id}", courseId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchCourseByTitleShouldReturnListOfFoundCourses() throws Exception {
        String searchCriterion = "101";

        mvc.perform(get("/api/courses/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchCourseByDescriptionShouldReturnListOfFoundCourses() throws Exception {
        String searchCriterion = "Mathematics";

        mvc.perform(get("/api/courses/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(2)));
    }

    @AfterEach
    public void cleanUp() {
        sampleCourseDTO = null;
        mvc = null;
    }
}
