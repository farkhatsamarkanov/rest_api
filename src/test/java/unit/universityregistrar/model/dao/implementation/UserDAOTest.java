package unit.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.configuration.test.HibernateDaoTestContextConfiguration;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.UserDAOImpl;
import com.rdlab.universityregistrar.model.entity.Student;
import com.rdlab.universityregistrar.model.entity.User;
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
public class UserDAOTest {
    @Autowired
    private DAO<User> dao;

    private User defaultUser;
    private User dummyUser;

    @BeforeEach
    @Transactional
    public void setupData() {
        defaultUser = User.builder()
                .login("user_1")
                .password("pa$$word")
                .isActive(false)
                .student(Student.builder().studentId(1).build())
                .build();
        dummyUser = User.builder()
                .login("user_2")
                .password("password")
                .isActive(false)
                .student(Student.builder().studentId(1).build())
                .build();
    }

    @Test
    @Transactional
    public void testGetAllUsers() {
        List<User> users = dao.getAllRecords();

        assertThat(users.isEmpty(), equalTo(false));
    }

    @Test
    @Transactional
    public void testGetUser() {
        User userToCompare = dao.getRecord(defaultUser.getLogin());

        assertAll(
                ()-> assertEquals(userToCompare.getLogin(), defaultUser.getLogin()),
                ()-> assertEquals(userToCompare.getPassword(), defaultUser.getPassword()),
                ()-> assertEquals(userToCompare.getIsActive(), defaultUser.getIsActive()),
                ()-> assertEquals(userToCompare.getStudent().getStudentId(), defaultUser.getStudent().getStudentId())
        );
    }

    @Test
    @Transactional
    public void testGetUserByStudentId() {
        User userToCompare = ((UserDAOImpl) dao).getUserByStudentId(1);

        assertAll(
                ()-> assertEquals(userToCompare.getLogin(), defaultUser.getLogin()),
                ()-> assertEquals(userToCompare.getPassword(), defaultUser.getPassword()),
                ()-> assertEquals(userToCompare.getIsActive(), defaultUser.getIsActive()),
                ()-> assertEquals(userToCompare.getStudent().getStudentId(), defaultUser.getStudent().getStudentId())
        );
    }

    @Test
    @Transactional
    public void testAddUser() {
        User userToAdd = dummyUser;
        dao.addRecord(userToAdd);
        User userToCompare = dao.getRecord(userToAdd.getLogin());

        assertAll(
                ()-> assertEquals(userToCompare.getLogin(), userToAdd.getLogin()),
                ()-> assertEquals(userToCompare.getPassword(), userToAdd.getPassword()),
                ()-> assertEquals(userToCompare.getIsActive(), userToAdd.getIsActive()),
                ()-> assertEquals(userToCompare.getStudent().getStudentId(), userToAdd.getStudent().getStudentId())
        );
    }

    @Test
    @Transactional
    public void testUpdateUser() {
        User updatedUser = dummyUser;
        updatedUser.setUserId(1);

        dao.updateRecord(updatedUser);
        User userToCompare = dao.getRecord(updatedUser.getLogin());

        assertAll(
                ()-> assertEquals(userToCompare.getLogin(), updatedUser.getLogin()),
                ()-> assertEquals(userToCompare.getPassword(), updatedUser.getPassword()),
                ()-> assertEquals(userToCompare.getIsActive(), updatedUser.getIsActive()),
                ()-> assertEquals(userToCompare.getStudent().getStudentId(), updatedUser.getStudent().getStudentId())
        );
    }

    @Test
    @Transactional
    public void testDeleteUser() {
        int numberOfUsersBeforeDeletion = dao.getAllRecords().size();
        dao.deleteRecord(defaultUser.getLogin());
        int numberOfUsersAfterDeletion = dao.getAllRecords().size();

        assertThat(numberOfUsersBeforeDeletion - numberOfUsersAfterDeletion, equalTo(1));
    }

    @Test
    @Transactional
    public void testSearchUsers() {
        List<User> foundUsers = dao.searchRecords(defaultUser.getLogin());

        assertThat(foundUsers.isEmpty(), equalTo(false));
    }

    @AfterEach
    @Transactional
    public void cleanUp() {
        defaultUser = null;
        dao = null;
    }
}
