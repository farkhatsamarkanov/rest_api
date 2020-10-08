package com.rdlab.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.model.dao.AbstractDAO;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Lecturer;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class LecturerDAOImpl extends AbstractDAO<Lecturer> {

    @Autowired
    private SessionFactory sessionFactory;

    public LecturerDAOImpl() {
        super.setClazz(Lecturer.class);
        super.setPrimaryKey("lecturerId");
    }

    @Override
    public List<Lecturer> getAllRecords() throws RuntimeException {
        return super.getAllRecords();
    }

    @Override
    public Integer addRecord(Lecturer record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();
        return (Integer) session.save(record);
    }

    @Override
    public Lecturer getRecord(String recordId) throws RuntimeException {
        return super.getRecord(recordId);
    }

    @Override
    public int updateRecord(Lecturer record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<Lecturer> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Lecturer.class);
        Root<Lecturer> root = criteriaUpdate.from(Lecturer.class);
        criteriaUpdate.set("lecturerName", record.getLecturerName());
        criteriaUpdate.set("dateOfBirth", record.getDateOfBirth());
        criteriaUpdate.set(root.get("numericAcademicRank"), record.getNumericAcademicRank());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("lecturerId"), record.getLecturerId()));

        return session.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        return super.deleteRecord(recordId);
    }

    @Override
    public List<Lecturer> searchRecords(String searchCriterion) throws RuntimeException {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Lecturer> criteriaQuery = criteriaBuilder.createQuery(Lecturer.class);
        Root<Lecturer> root = criteriaQuery.from(Lecturer.class);
        Predicate predicateForLecturerName = criteriaBuilder.like(criteriaBuilder.lower(root.get("lecturerName")), "%" + searchCriterion.toLowerCase() + "%");

        criteriaQuery.select(root).where(predicateForLecturerName);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("lecturerId")));

        Query<Lecturer> query = currentSession.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
