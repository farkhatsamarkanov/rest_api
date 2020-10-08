package com.rdlab.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.model.dao.AbstractDAO;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Student;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class StudentDAOImpl extends AbstractDAO<Student> {

    @Autowired
    private SessionFactory sessionFactory;

    public StudentDAOImpl() {
        super.setClazz(Student.class);
        super.setPrimaryKey("studentId");
    }

    @Override
    public List<Student> getAllRecords() throws RuntimeException {
        return super.getAllRecords();
    }

    @Override
    public Integer addRecord(Student record) throws RuntimeException {
        return super.addRecord(record);
    }

    @Override
    public Student getRecord(String recordId) throws RuntimeException {
        return super.getRecord(recordId);
    }

    @Override
    public int updateRecord(Student record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<Student> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Student.class);
        Root<Student> root = criteriaUpdate.from(Student.class);
        criteriaUpdate.set("studentName", record.getStudentName());
        criteriaUpdate.set("dateOfBirth", record.getDateOfBirth());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("studentId"), record.getStudentId()));

        return session.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        return super.deleteRecord(recordId);
    }

    @Override
    public List<Student> searchRecords(String searchCriterion) throws RuntimeException {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        Root<Student> root = criteriaQuery.from(Student.class);
        Predicate predicateForStudentName = criteriaBuilder.like(criteriaBuilder.lower(root.get("studentName")), "%" + searchCriterion.toLowerCase() + "%");


        criteriaQuery.select(root).where(predicateForStudentName);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("studentId")));

        Query<Student> query = currentSession.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
