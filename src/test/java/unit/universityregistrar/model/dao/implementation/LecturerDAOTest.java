package unit.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.configuration.test.HibernateDaoTestContextConfiguration;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.AcademicRank;
import com.rdlab.universityregistrar.model.entity.Lecturer;
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
public class LecturerDAOTest {
    @Autowired
    private DAO<Lecturer> dao;

    private Lecturer defaultLecturer;
    private Lecturer dummyLecturer;

    @BeforeEach
    @Transactional
    public void setupData() {
        defaultLecturer = Lecturer.builder()
                .lecturerId(1)
                .lecturerName("John Doe")
                .dateOfBirth(461885632L)
                .numericAcademicRank(AcademicRank.builder().numericRank(1).build())
                .build();
        dummyLecturer = Lecturer.builder()
                .lecturerName("Lecturer #2")
                .dateOfBirth(177802432L)
                .numericAcademicRank(AcademicRank.builder().numericRank(1).build())
                .build();
    }

    @Test
    @Transactional
    public void testGetAllLecturers() {
        List<Lecturer> lecturers = dao.getAllRecords();

        assertThat(lecturers.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    public void testGetLecturer() {
        Lecturer lecturerToCompare = dao.getRecord(String.valueOf(defaultLecturer.getLecturerId()));

        assertAll(
                ()-> assertEquals(lecturerToCompare.getLecturerName(), defaultLecturer.getLecturerName()),
                ()-> assertEquals(lecturerToCompare.getDateOfBirth(), defaultLecturer.getDateOfBirth()),
                ()-> assertEquals(lecturerToCompare.getNumericAcademicRank().getNumericRank(), defaultLecturer.getNumericAcademicRank().getNumericRank())
        );
    }

    @Test
    @Transactional
    public void testAddLecturer() {
        Lecturer lecturerToAdd = dummyLecturer;
        Integer generatedId = dao.addRecord(lecturerToAdd);
        Lecturer lecturerToCompare = dao.getRecord(String.valueOf(generatedId));

        assertThat(lecturerToCompare, samePropertyValuesAs(lecturerToAdd));
    }

    @Test
    @Transactional
    public void testUpdateLecturer() {
        Lecturer updatedLecturer = Lecturer.builder()
                .lecturerId(defaultLecturer.getLecturerId())
                .lecturerName(dummyLecturer.getLecturerName())
                .dateOfBirth(dummyLecturer.getDateOfBirth())
                .numericAcademicRank(AcademicRank.builder().numericRank(1).build())
                .build();
        dao.updateRecord(updatedLecturer);
        Lecturer lecturerToCompare = dao.getRecord(String.valueOf(updatedLecturer.getLecturerId()));

        assertAll(
                ()-> assertEquals(lecturerToCompare.getLecturerName(), updatedLecturer.getLecturerName()),
                ()-> assertEquals(lecturerToCompare.getDateOfBirth(), updatedLecturer.getDateOfBirth()),
                ()-> assertEquals(lecturerToCompare.getNumericAcademicRank().getNumericRank(), updatedLecturer.getNumericAcademicRank().getNumericRank())
        );
    }

    @Test
    @Transactional
    public void testDeleteLecturer() {
        int numberOfLecturersBeforeDeletion = dao.getAllRecords().size();
        dao.deleteRecord(String.valueOf(defaultLecturer.getLecturerId()));
        int numberOfLecturersAfterDeletion = dao.getAllRecords().size();

        assertThat(numberOfLecturersBeforeDeletion - numberOfLecturersAfterDeletion, equalTo(1));
    }

    @Test
    @Transactional
    public void testSearchLecturers() {
        List<Lecturer> foundLecturers = dao.searchRecords(defaultLecturer.getLecturerName());

        assertThat(foundLecturers.isEmpty(), equalTo(false));
    }

    @AfterEach
    @Transactional
    public void cleanUp() {
        defaultLecturer = null;
        dao = null;
    }
}
