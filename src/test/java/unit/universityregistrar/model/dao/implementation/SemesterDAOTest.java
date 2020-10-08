package unit.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.configuration.test.HibernateDaoTestContextConfiguration;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Semester;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {HibernateDaoTestContextConfiguration.class})
public class SemesterDAOTest {
    @Autowired
    private DAO<Semester> dao;

    private Semester defaultSemester;
    private Semester secondSemester;
    private Semester dummySemester;

    @BeforeEach
    @Transactional
    public void setupData() {
        defaultSemester = Semester.builder()
                .entryId(1)
                .semesterId("FAL2020")
                .semesterName("Fall semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(1599300020L)
                .semesterEndTime(1608890420L)
                .build();
        secondSemester = Semester.builder()
                .entryId(2)
                .semesterId("SUM2020")
                .semesterName("Summer semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(456132789L)
                .semesterEndTime(123456789L)
                .build();
        dummySemester = Semester.builder()
                .semesterId("SPR2020")
                .semesterName("Spring semester of 2020")
                .semesterYear(2020)
                .semesterStartTime(1579082420L)
                .semesterEndTime(1587808820L)
                .build();
    }

    @Test
    @Transactional
    public void testGetAllSemesters() {
        List<Semester> semesters = dao.getAllRecords();

        assertThat(semesters.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    public void testGetSemester() {
        Semester semesterToCompare = dao.getRecord(defaultSemester.getSemesterId());

        assertAll(
                ()-> assertEquals(semesterToCompare.getSemesterName(), defaultSemester.getSemesterName()),
                ()-> assertEquals(semesterToCompare.getSemesterYear(), defaultSemester.getSemesterYear()),
                ()-> assertEquals(semesterToCompare.getSemesterStartTime(), defaultSemester.getSemesterStartTime()),
                ()-> assertEquals(semesterToCompare.getSemesterEndTime(), defaultSemester.getSemesterEndTime())
        );
    }

    @Test
    @Transactional
    public void testAddSemester() {
        Semester semesterToAdd = dummySemester;
        dao.addRecord(semesterToAdd);
        Semester semesterToCompare = dao.getRecord(semesterToAdd.getSemesterId());

        assertThat(semesterToCompare, samePropertyValuesAs(semesterToAdd));
    }

    @Test
    @Transactional
    public void testUpdateSemester() {
        Semester updatedSemester = dummySemester;
        updatedSemester.setEntryId(secondSemester.getEntryId());

        dao.updateRecord(updatedSemester);
        Semester semesterToCompare = dao.getRecord(updatedSemester.getSemesterId());

        assertAll(
                ()-> assertEquals(semesterToCompare.getSemesterName(), updatedSemester.getSemesterName()),
                ()-> assertEquals(semesterToCompare.getSemesterYear(), updatedSemester.getSemesterYear()),
                ()-> assertEquals(semesterToCompare.getSemesterStartTime(), updatedSemester.getSemesterStartTime()),
                ()-> assertEquals(semesterToCompare.getSemesterEndTime(), updatedSemester.getSemesterEndTime())
        );
    }

    @Test
    @Transactional
    public void testDeleteSemester() {
        int numberOfSemestersBeforeDeletion = dao.getAllRecords().size();
        dao.deleteRecord(defaultSemester.getSemesterId());
        int numberOfSemestersAfterDeletion = dao.getAllRecords().size();

        assertThat(numberOfSemestersBeforeDeletion - numberOfSemestersAfterDeletion, equalTo(1));
    }

    @Test
    @Transactional
    public void testSearchSemesters() {
        List<Semester> foundSemesters = dao.searchRecords(defaultSemester.getSemesterName());

        assertThat(foundSemesters.isEmpty(), equalTo(false));
    }

    @AfterEach
    @Transactional
    public void cleanUp() {
        defaultSemester = null;
        dao = null;
    }
}
