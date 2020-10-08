package unit.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.configuration.test.HibernateDaoTestContextConfiguration;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.ScheduleEntryDAOImpl;
import com.rdlab.universityregistrar.model.entity.*;
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
public class ScheduleEntryDAOTest {
    @Autowired
    private DAO<ScheduleEntry> dao;

    private ScheduleEntry defaultScheduleEntry;
    private ScheduleEntry dummyScheduleEntry;

    @BeforeEach
    @Transactional
    public void setupData() {
        defaultScheduleEntry = ScheduleEntry.builder()
                .entryId(1)
                .student(Student.builder().studentId(1).build())
                .lecturer(Lecturer.builder().lecturerId(1).build())
                .course(Course.builder().courseId(1).build())
                .time(1609855628000L)
                .location("Room A1")
                .semester(Semester.builder().semesterId("FAL2020").build())
                .build();
       dummyScheduleEntry = ScheduleEntry.builder()
               .student(Student.builder().studentId(1).build())
               .lecturer(Lecturer.builder().lecturerId(1).build())
               .course(Course.builder().courseId(3).build())
               .time(99999L)
               .location("Room C5")
               .semester(Semester.builder().semesterId("SUM2020").build())
               .build();
    }

    @Test
    @Transactional
    public void testGetAllScheduleEntries() {
        List<ScheduleEntry> scheduleEntries = dao.getAllRecords();

        assertThat(scheduleEntries.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    public void testGetScheduleEntry() {
        ScheduleEntry scheduleEntryToCompare = dao.getRecord(String.valueOf(defaultScheduleEntry.getEntryId()));

        assertAll(
                ()-> assertEquals(scheduleEntryToCompare.getLocation(), defaultScheduleEntry.getLocation()),
                ()-> assertEquals(scheduleEntryToCompare.getTime(), defaultScheduleEntry.getTime()),
                ()-> assertEquals(scheduleEntryToCompare.getSemester().getSemesterId(), defaultScheduleEntry.getSemester().getSemesterId()),
                ()-> assertEquals(scheduleEntryToCompare.getStudent().getStudentId(), defaultScheduleEntry.getStudent().getStudentId()),
                ()-> assertEquals(scheduleEntryToCompare.getCourse().getCourseId(), defaultScheduleEntry.getCourse().getCourseId()),
                ()-> assertEquals(scheduleEntryToCompare.getLecturer().getLecturerId(), defaultScheduleEntry.getLecturer().getLecturerId())
        );
    }

    @Test
    @Transactional
    public void testAddScheduleEntry() {
        ScheduleEntry scheduleEntryToAdd = dummyScheduleEntry;
        Integer generatedId = dao.addRecord(scheduleEntryToAdd);
        ScheduleEntry scheduleEntryToCompare = dao.getRecord(String.valueOf(generatedId));

        assertAll(
                ()-> assertEquals(scheduleEntryToCompare.getLocation(), scheduleEntryToAdd.getLocation()),
                ()-> assertEquals(scheduleEntryToCompare.getTime(), scheduleEntryToAdd.getTime()),
                ()-> assertEquals(scheduleEntryToCompare.getStudent().getStudentId(), scheduleEntryToAdd.getStudent().getStudentId()),
                ()-> assertEquals(scheduleEntryToCompare.getCourse().getCourseId(), scheduleEntryToAdd.getCourse().getCourseId()),
                ()-> assertEquals(scheduleEntryToCompare.getLecturer().getLecturerId(), scheduleEntryToAdd.getLecturer().getLecturerId())
        );
    }

    @Test
    @Transactional
    public void testUpdateScheduleEntry() {
        ScheduleEntry updatedScheduleEntry = dummyScheduleEntry;
        updatedScheduleEntry.setEntryId(1);

        dao.updateRecord(updatedScheduleEntry);
        ScheduleEntry scheduleEntryToCompare = dao.getRecord(String.valueOf(updatedScheduleEntry.getEntryId()));

        assertAll(
                ()-> assertEquals(scheduleEntryToCompare.getLocation(), updatedScheduleEntry.getLocation()),
                ()-> assertEquals(scheduleEntryToCompare.getTime(), updatedScheduleEntry.getTime()),
                ()-> assertEquals(scheduleEntryToCompare.getSemester().getSemesterId(), updatedScheduleEntry.getSemester().getSemesterId()),
                ()-> assertEquals(scheduleEntryToCompare.getStudent().getStudentId(), updatedScheduleEntry.getStudent().getStudentId()),
                ()-> assertEquals(scheduleEntryToCompare.getCourse().getCourseId(), updatedScheduleEntry.getCourse().getCourseId()),
                ()-> assertEquals(scheduleEntryToCompare.getLecturer().getLecturerId(), updatedScheduleEntry.getLecturer().getLecturerId())
        );
    }

    @Test
    @Transactional
    public void testDeleteScheduleEntry() {
        int numberOfScheduleEntriesBeforeDeletion = dao.getAllRecords().size();
        dao.deleteRecord(String.valueOf(defaultScheduleEntry.getEntryId()));
        int numberOfScheduleEntriesAfterDeletion = dao.getAllRecords().size();

        assertThat(numberOfScheduleEntriesBeforeDeletion - numberOfScheduleEntriesAfterDeletion, equalTo(1));
    }

    @Test
    @Transactional
    public void testSearchScheduleEntries() {
        List<ScheduleEntry> foundScheduleEntries = dao.searchRecords(defaultScheduleEntry.getLocation());

        assertThat(foundScheduleEntries.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    public void testGetNumberOfTakenCoursesForStudent() {
        Long numberOfTakenCourses = ((ScheduleEntryDAOImpl) dao).getNumberOfTakenCoursesForStudent(defaultScheduleEntry.getStudent().getStudentId(), defaultScheduleEntry.getSemester().getSemesterId());

        assertThat(numberOfTakenCourses, equalTo(5L));
    }

    @AfterEach
    @Transactional
    public void cleanUp() {
        defaultScheduleEntry = null;
        dao = null;
    }
}
