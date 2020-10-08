package com.rdlab.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.model.dao.AbstractDAO;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.ScheduleEntry;
import org.apache.commons.lang3.math.NumberUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class ScheduleEntryDAOImpl extends AbstractDAO<ScheduleEntry> {
    @Autowired
    private SessionFactory sessionFactory;

    public ScheduleEntryDAOImpl() {
        super.setClazz(ScheduleEntry.class);
        super.setPrimaryKey("entryId");
    }

    @Override
    public List<ScheduleEntry> getAllRecords() throws RuntimeException {
        return super.getAllRecords();
    }

    @Override
    public Integer addRecord(ScheduleEntry record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();
        return (Integer) session.save(record);
    }

    @Override
    public ScheduleEntry getRecord(String recordId) throws RuntimeException {
        return super.getRecord(recordId);
    }

    @Override
    public int updateRecord(ScheduleEntry record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<ScheduleEntry> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(ScheduleEntry.class);
        Root<ScheduleEntry> root = criteriaUpdate.from(ScheduleEntry.class);
        criteriaUpdate.set("course", record.getCourse());
        criteriaUpdate.set("lecturer", record.getLecturer());
        criteriaUpdate.set("semester", record.getSemester());
        criteriaUpdate.set("student", record.getStudent());
        criteriaUpdate.set("location", record.getLocation());
        criteriaUpdate.set("time", record.getTime());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("entryId"), record.getEntryId()));

        return session.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        return super.deleteRecord(recordId);
    }

    @Override
    public List<ScheduleEntry> searchRecords(String searchCriterion) throws RuntimeException {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<ScheduleEntry> criteriaQuery = criteriaBuilder.createQuery(ScheduleEntry.class);
        Root<ScheduleEntry> root = criteriaQuery.from(ScheduleEntry.class);

        Predicate finalPredicate;

        if (NumberUtils.isParsable(searchCriterion)) {
            finalPredicate = criteriaBuilder.equal(root.get("course").get("courseId"), Integer.parseInt(searchCriterion));
        } else {
            Predicate predicateForSemesterId = criteriaBuilder.like(criteriaBuilder.lower(root.get("semester").get("semesterId")), "%" + searchCriterion.toLowerCase() + "%");
            Predicate predicateForLocation = criteriaBuilder.like(criteriaBuilder.lower(root.get("location")), "%" + searchCriterion.toLowerCase() + "%");
            finalPredicate = criteriaBuilder.or(predicateForLocation, predicateForSemesterId);
        }

        criteriaQuery.select(root).where(finalPredicate);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("entryId")));

        Query<ScheduleEntry> query = currentSession.createQuery(criteriaQuery);
        return query.getResultList();
    }

    public Long getNumberOfTakenCoursesForStudent(Integer studentId, String semesterId) {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<ScheduleEntry> mainRoot = criteriaQuery.from(ScheduleEntry.class);

        Subquery<Long> sub = criteriaQuery.subquery(Long.class);
        Root<ScheduleEntry> root = sub.from(ScheduleEntry.class);

        sub.select(criteriaBuilder.countDistinct(root.get("course").get("courseId")));

        Predicate predicateStudentId = criteriaBuilder.equal(root.get("student").get("studentId"), studentId);
        Predicate predicateSemesterId = criteriaBuilder.equal(root.get("semester").get("semesterId"), semesterId);
        sub.where(criteriaBuilder.and(predicateStudentId, predicateSemesterId));

        sub.groupBy(root.get("semester").get("semesterId"));

        CriteriaBuilder.Coalesce<Long> coalesce = criteriaBuilder.coalesce();
        coalesce.value(sub);
        coalesce.value((long) 0);

        criteriaQuery.select(coalesce);

        Query<Long> query = currentSession.createQuery(criteriaQuery);
        return query.getSingleResult();
    }
}
