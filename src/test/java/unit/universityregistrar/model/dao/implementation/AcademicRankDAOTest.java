package unit.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.configuration.test.HibernateDaoTestContextConfiguration;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.AcademicRank;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateDaoTestContextConfiguration.class})
public class AcademicRankDAOTest {
    @Autowired
    private DAO<AcademicRank> dao;

    private AcademicRank defaultAcademicRank;
    private AcademicRank dummyAcademicRank;

    @BeforeEach
    @Transactional
    public void setupData() {
        defaultAcademicRank = AcademicRank.builder()
                .rankId(1)
                .numericRank(1)
                .rankName("Professor")
                .build();
        dummyAcademicRank = AcademicRank.builder()
                .numericRank(2)
                .rankName("Instructor")
                .build();
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testGetAllAcademicRanks() {
        List<AcademicRank> academicRanks = dao.getAllRecords();

        assertThat(academicRanks.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testGetAcademicRank() {
        AcademicRank academicRankToCompare = dao.getRecord(String.valueOf(defaultAcademicRank.getRankId()));

        assertAll(
                ()-> assertEquals(academicRankToCompare.getRankName(), defaultAcademicRank.getRankName()),
                ()-> assertEquals(academicRankToCompare.getNumericRank(), defaultAcademicRank.getNumericRank())
        );
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testAddAcademicRank() {
        AcademicRank academicRankToAdd = AcademicRank.builder()
                .numericRank(dummyAcademicRank.getNumericRank())
                .rankName(dummyAcademicRank.getRankName())
                .build();
        Integer generatedId = dao.addRecord(academicRankToAdd);
        AcademicRank academicRankToCompare = dao.getRecord(String.valueOf(generatedId));

        assertThat(academicRankToCompare, samePropertyValuesAs(academicRankToAdd));
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testUpdateAcademicRank() {
        AcademicRank academicRankToUpdate = AcademicRank.builder()
                .rankId(defaultAcademicRank.getRankId())
                .numericRank(defaultAcademicRank.getNumericRank())
                .rankName(dummyAcademicRank.getRankName())
                .build();
        dao.updateRecord(academicRankToUpdate);
        AcademicRank academicRankToCompare = dao.getRecord(String.valueOf(academicRankToUpdate.getRankId()));

        assertAll(
                ()-> assertEquals(academicRankToCompare.getRankName(), academicRankToUpdate.getRankName()),
                ()-> assertEquals(academicRankToCompare.getNumericRank(), academicRankToUpdate.getNumericRank())
        );
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testDeleteAcademicRank() {
        int numberOfAcademicRanksBeforeDeletion = dao.getAllRecords().size();
        dao.deleteRecord(String.valueOf(defaultAcademicRank.getRankId()));
        int numberOfAcademicRanksAfterDeletion = dao.getAllRecords().size();

        assertThat(numberOfAcademicRanksBeforeDeletion - numberOfAcademicRanksAfterDeletion, equalTo(1));
    }

    @Test
    @Transactional
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public void testSearchAcademicRanks() {
        List<AcademicRank> foundAcademicRanks = dao.searchRecords(defaultAcademicRank.getRankName());

        assertThat(foundAcademicRanks.isEmpty(), equalTo(false));
    }

    @AfterEach
    @Transactional
    public void cleanUp() {
        defaultAcademicRank = null;
        dao = null;
    }
}
