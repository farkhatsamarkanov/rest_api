package com.rdlab.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.model.dao.AbstractDAO;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Semester;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class SemesterDAOImpl extends AbstractDAO<Semester> {

    @Autowired
    private SessionFactory sessionFactory;

    public SemesterDAOImpl() {
        super.setClazz(Semester.class);
        super.setPrimaryKey("semesterId");
    }

    @Override
    public List<Semester> getAllRecords() throws RuntimeException {
        return super.getAllRecords();
    }

    @Override
    public Integer addRecord(Semester record) throws RuntimeException {
        return super.addRecord(record);
    }

    @Override
    public Semester getRecord(String recordId) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Semester> criteriaQuery = criteriaBuilder.createQuery(Semester.class);
        Root<Semester> root = criteriaQuery.from(Semester.class);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("semesterId"), recordId));

        Query<Semester> query = session.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    @Override
    public int updateRecord(Semester record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<Semester> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Semester.class);
        Root<Semester> root = criteriaUpdate.from(Semester.class);
        criteriaUpdate.set("semesterId", record.getSemesterId());
        criteriaUpdate.set("semesterName", record.getSemesterName());
        criteriaUpdate.set("semesterYear", record.getSemesterYear());
        criteriaUpdate.set("semesterStartTime", record.getSemesterStartTime());
        criteriaUpdate.set("semesterEndTime", record.getSemesterEndTime());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("entryId"), record.getEntryId()));

        return session.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        return super.deleteRecord(recordId);
    }

    @Override
    public List<Semester> searchRecords(String searchCriterion) throws RuntimeException {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Semester> criteriaQuery = criteriaBuilder.createQuery(Semester.class);
        Root<Semester> root = criteriaQuery.from(Semester.class);
        Predicate predicateForSemesterName = criteriaBuilder.like(criteriaBuilder.lower(root.get("semesterName")), "%" + searchCriterion.toLowerCase() + "%");


        criteriaQuery.select(root).where(predicateForSemesterName);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("entryId")));

        Query<Semester> query = currentSession.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
