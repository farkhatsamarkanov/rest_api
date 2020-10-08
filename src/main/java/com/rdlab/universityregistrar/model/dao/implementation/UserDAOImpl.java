package com.rdlab.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.model.dao.AbstractDAO;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class UserDAOImpl extends AbstractDAO<User> {

    @Autowired
    private SessionFactory sessionFactory;

    public UserDAOImpl() {
        super.setClazz(User.class);
        super.setPrimaryKey("login");
    }

    @Override
    public List<User> getAllRecords() throws RuntimeException {
        return super.getAllRecords();
    }

    @Override
    public Integer addRecord(User record) throws RuntimeException {
        return super.addRecord(record);
    }

    @Override
    public User getRecord(String recordId) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("login"), recordId));

        Query<User> query = session.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    public User getUserByStudentId(Integer studentId) {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("student").get("studentId"), String.valueOf(studentId)));

        Query<User> query = session.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    @Override
    public int updateRecord(User record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<User> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(User.class);
        Root<User> root = criteriaUpdate.from(User.class);
        criteriaUpdate.set("login", record.getLogin());
        criteriaUpdate.set("password", record.getPassword());
        criteriaUpdate.set("isActive", record.getIsActive());
        criteriaUpdate.set("student", record.getStudent());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("userId"), record.getUserId()));

        return session.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        return super.deleteRecord(recordId);
    }

    @Override
    public List<User> searchRecords(String searchCriterion) throws RuntimeException {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
        Root<User> root = criteriaQuery.from(User.class);
        Predicate predicateForLogin = criteriaBuilder.like(criteriaBuilder.lower(root.get("login")), "%" + searchCriterion.toLowerCase() + "%");


        criteriaQuery.select(root).where(predicateForLogin);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("userId")));

        Query<User> query = currentSession.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
