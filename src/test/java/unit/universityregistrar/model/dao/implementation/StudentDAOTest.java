package unit.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.configuration.test.HibernateDaoTestContextConfiguration;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Student;
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
public class StudentDAOTest {
    @Autowired
    private DAO<Student> dao;

    private Student defaultStudent;
    private Student dummyStudent;

    @BeforeEach
    @Transactional
    public void setupData() {
        defaultStudent = Student.builder()
                .studentId(1)
                .studentName("Student one")
                .dateOfBirth(463226420L)
                .build();
        dummyStudent = Student.builder()
                .studentName("Student #2")
                .dateOfBirth(19840502L)
                .build();
    }

    @Test
    @Transactional
    public void testGetAllStudents() {
        List<Student> students = dao.getAllRecords();

        assertThat(students.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    public void testGetStudent() {
        Student studentToCompare = dao.getRecord(String.valueOf(defaultStudent.getStudentId()));

        assertAll(
                ()-> assertEquals(studentToCompare.getDateOfBirth(), defaultStudent.getDateOfBirth()),
                ()-> assertEquals(studentToCompare.getStudentName(), defaultStudent.getStudentName())
        );
    }

    @Test
    @Transactional
    public void testAddStudent() {
        Student studentToAdd = dummyStudent;
        Integer generatedId = dao.addRecord(studentToAdd);
        Student studentToCompare = dao.getRecord(String.valueOf(String.valueOf(generatedId)));

        assertThat(studentToCompare, samePropertyValuesAs(studentToAdd));
    }

    @Test
    @Transactional
    public void testUpdateStudent() {
        Student updatedStudent = dummyStudent;
        updatedStudent.setStudentId(defaultStudent.getStudentId());

        dao.updateRecord(updatedStudent);
        Student studentToCompare = dao.getRecord(String.valueOf(updatedStudent.getStudentId()));

        assertAll(
                ()-> assertEquals(studentToCompare.getDateOfBirth(), updatedStudent.getDateOfBirth()),
                ()-> assertEquals(studentToCompare.getStudentName(), updatedStudent.getStudentName())
        );
    }

    @Test
    @Transactional
    public void testDeleteStudent() {
        int numberOfStudentsBeforeDeletion = dao.getAllRecords().size();
        dao.deleteRecord(String.valueOf(defaultStudent.getStudentId()));
        int numberOfStudentsAfterDeletion = dao.getAllRecords().size();

        assertThat(numberOfStudentsBeforeDeletion - numberOfStudentsAfterDeletion, equalTo(1));
    }

    @Test
    @Transactional
    public void testSearchStudents() {
        List<Student> foundStudents = dao.searchRecords(defaultStudent.getStudentName());

        assertThat(foundStudents.isEmpty(), equalTo(false));
    }

    @AfterEach
    @Transactional
    public void cleanUp() {
        defaultStudent = null;
        dao = null;
    }
}
