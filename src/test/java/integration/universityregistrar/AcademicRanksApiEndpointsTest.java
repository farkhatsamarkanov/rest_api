package integration.universityregistrar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rdlab.universityregistrar.configuration.test.IntegrationTestContextConfiguration;
import com.rdlab.universityregistrar.service.dto.AcademicRankDTO;
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
public class AcademicRanksApiEndpointsTest {
    @Autowired
    private WebApplicationContext wac;
    private MockMvc mvc;
    private AcademicRankDTO sampleRankDTO;
    private AcademicRankDTO dummyRankDTO;

    @BeforeEach
    public void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();

        sampleRankDTO = AcademicRankDTO.builder()
                .numericRank(1)
                .rankName("Professor")
                .build();
        dummyRankDTO = AcademicRankDTO.builder()
                .numericRank(2)
                .rankName("some rank name")
                .build();
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getAcademicRanksShouldReturnListOfAcademicRanks() throws Exception {
        mvc.perform(get("/api/ranks")).andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].rankName", equalTo(sampleRankDTO.getRankName())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getAcademicRankShoudReturnSingleAcademicRank() throws Exception {
        String rankId = "1";

        mvc.perform(get("/api/ranks/{id}", rankId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.numericRank", is(1)))
                .andExpect(jsonPath("$.responseBody.rankName", is(sampleRankDTO.getRankName())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void getAcademicRankShouldReturnNotFoundMessage() throws Exception {
        String rankId = "35";
        String expectedMessage = "item of requested type with such id not found";

        mvc.perform(get("/api/ranks/{id}", rankId))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addAcademicRankShouldReturnAcademicRankWithEntryId() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AcademicRankDTO rankToAdd = AcademicRankDTO.builder()
                .numericRank(dummyRankDTO.getNumericRank())
                .rankName(dummyRankDTO.getRankName())
                .build();

        String rankJson = mapper.writeValueAsString(rankToAdd);

        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.responseBody.numericRank", is(2)))
                .andExpect(jsonPath("$.responseBody.rankName", is(rankToAdd.getRankName())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addAcademicRankShouldReturnAlreadyExistsMessage() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AcademicRankDTO rankToAdd = AcademicRankDTO.builder()
                .numericRank(sampleRankDTO.getNumericRank())
                .rankName(dummyRankDTO.getRankName())
                .build();

        String expectedMessage = "Entity with such id already exists!";
        String rankJson = mapper.writeValueAsString(rankToAdd);


        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addAcademicRankShouldReturnParsingErrorMessage() throws Exception {
        String expectedMessage = "Cannot parse input data, please check input data format";
        String rankJson = "{\"numericRank\":\"f\",\"rankName\":\"name\"}";


        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody", is(expectedMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addAcademicRankWithNullNumericRankValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AcademicRankDTO rankToAdd = AcademicRankDTO.builder()
                .numericRank(null)
                .rankName(dummyRankDTO.getRankName())
                .build();

        String rankJson = mapper.writeValueAsString(rankToAdd);

        String expectedReturnMessage = "numeric rank cannot be null";

        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addAcademicRankWithEmptyRankNameValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AcademicRankDTO rankToAdd = AcademicRankDTO.builder()
                .numericRank(dummyRankDTO.getNumericRank())
                .rankName("")
                .build();

        String rankJson = mapper.writeValueAsString(rankToAdd);

        String expectedReturnMessage = "rank name cannot be empty";

        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addRankWithRankNameSizeValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        StringBuilder rankName = new StringBuilder();

        for (int i = 0; i < 46; i++) {
            rankName.append("a");
        }

        AcademicRankDTO rankToAdd = AcademicRankDTO.builder()
                .numericRank(dummyRankDTO.getNumericRank())
                .rankName(rankName.toString())
                .build();

        String rankJson = mapper.writeValueAsString(rankToAdd);

        String expectedReturnMessage = "rank name cannot be longer than 45 characters";

        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void addAcademicRankWithRankNameRegExValidationConstraint() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AcademicRankDTO rankToAdd = AcademicRankDTO.builder()
                .numericRank(dummyRankDTO.getNumericRank())
                .rankName("#$%^&*()")
                .build();

        String rankJson = mapper.writeValueAsString(rankToAdd);

        String expectedReturnMessage = "rank name must contain " +
                "only letters in range (a-z, A-Z), " +
                "whitespaces and numbers from 0 to 9";

        mvc.perform(post("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0]", equalTo(expectedReturnMessage)));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateAcademicRankShouldReturnUpdatedRank() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        AcademicRankDTO academicRankToUpdate = AcademicRankDTO.builder()
                .rankId(1)
                .numericRank(sampleRankDTO.getNumericRank())
                .rankName(dummyRankDTO.getRankName())
                .build();

        String rankJson = mapper.writeValueAsString(academicRankToUpdate);

        mvc.perform(put("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody.numericRank", is(1)))
                .andExpect(jsonPath("$.responseBody.rankName", is(academicRankToUpdate.getRankName())));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void updateAcademicRankShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        ObjectMapper mapper = new ObjectMapper();

        AcademicRankDTO academicRankToUpdate = AcademicRankDTO.builder()
                .rankId(56)
                .numericRank(dummyRankDTO.getNumericRank())
                .rankName(dummyRankDTO.getRankName())
                .build();

        String rankJson = mapper.writeValueAsString(academicRankToUpdate);

        mvc.perform(put("/api/ranks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(rankJson))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteAcademicRankShouldReturnDeleteSuccessMessage() throws Exception {
        String deleteSuccessMessage = "item deleted successfully";

        String rankId = String.valueOf(sampleRankDTO.getNumericRank());

        mvc.perform(delete("/api/ranks/{id}", rankId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message", is(deleteSuccessMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void deleteAcademicRankShouldReturnNotFoundMessage() throws Exception {
        String notFoundMessage = "item of requested type with such id not found";

        String rankId = "5";

        mvc.perform(delete("/api/ranks/{id}", rankId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message", is(notFoundMessage)))
                .andExpect(jsonPath("$.responseBody", is("")));
    }

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void searchAcademicRanksShouldReturnListOfFoundCourses() throws Exception {
        String searchCriterion = "Prof";

        mvc.perform(get("/api/ranks/search?param={searchCriterion}", searchCriterion))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseBody").isArray())
                .andExpect(jsonPath("$.responseBody", hasSize(1)))
                .andExpect(jsonPath("$.responseBody[0].rankName", equalTo(sampleRankDTO.getRankName())));
    }

    @AfterEach
    public void cleanUp() {
        sampleRankDTO = null;
        mvc = null;
    }
}
