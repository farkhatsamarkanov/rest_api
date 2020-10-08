package unit.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.configuration.test.HibernateDaoTestContextConfiguration;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Course;
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
public class CourseDAOTest {
    @Autowired
    private DAO<Course> dao;

    private Course defaultCourse;
    private Course dummyCourse;

    @BeforeEach
    @Transactional
    public void setupData() {
        defaultCourse = Course.builder()
                .courseId(1)
                .courseTitle("Computer Science 101")
                .courseDescription("Prerequisites: Mathematics, Calculus")
                .build();
        defaultCourse = Course.builder()
                .courseTitle("Mathematics 1061")
                .courseDescription("Prerequisites:")
                .build();
    }

    @Test
    @Transactional
    public void testGetAllCourses() {
        List<Course> courses = dao.getAllRecords();

        assertThat(courses.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    public void testGetCourse() {
        Course courseToCompare = dao.getRecord(String.valueOf(defaultCourse.getCourseId()));

        assertAll(
                ()-> assertEquals(courseToCompare.getCourseDescription(), defaultCourse.getCourseDescription()),
                ()-> assertEquals(courseToCompare.getCourseTitle(), defaultCourse.getCourseTitle())
        );
    }

    @Test
    @Transactional
    public void testAddCourse() {
        Course courseToAdd = dummyCourse;
        Integer generatedId = dao.addRecord(courseToAdd);
        Course courseToCompare = dao.getRecord(String.valueOf(generatedId));

        assertThat(courseToCompare, samePropertyValuesAs(courseToAdd));
    }

    @Test
    @Transactional
    public void testUpdateCourse() {
        Course updatedCourse = Course.builder()
                .courseId(defaultCourse.getCourseId())
                .courseTitle(dummyCourse.getCourseTitle())
                .courseDescription(dummyCourse.getCourseDescription())
                .build();
        dao.updateRecord(updatedCourse);
        Course courseToCompare = dao.getRecord(String.valueOf(updatedCourse.getCourseId()));

        assertAll(
                ()-> assertEquals(courseToCompare.getCourseDescription(), updatedCourse.getCourseDescription()),
                ()-> assertEquals(courseToCompare.getCourseTitle(), updatedCourse.getCourseTitle())
        );
    }

    @Test
    @Transactional
    public void testDeleteCourse() {
        int numberOfCoursesBeforeDeletion = dao.getAllRecords().size();
        dao.deleteRecord(String.valueOf(defaultCourse.getCourseId()));
        int numberOfCoursesAfterDeletion = dao.getAllRecords().size();

        assertThat(numberOfCoursesBeforeDeletion - numberOfCoursesAfterDeletion, equalTo(1));
    }

    @Test
    @Transactional
    public void testSearchCourses() {
        List<Course> foundCourses = dao.searchRecords(defaultCourse.getCourseTitle());

        assertThat(foundCourses.isEmpty(), equalTo(false));
    }

    @AfterEach
    @Transactional
    public void cleanUp() {
        defaultCourse = null;
        dao = null;
    }

}
