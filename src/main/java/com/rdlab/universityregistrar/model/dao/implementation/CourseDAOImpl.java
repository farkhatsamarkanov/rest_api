package com.rdlab.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.model.dao.AbstractDAO;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Course;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

@Repository
public class CourseDAOImpl extends AbstractDAO<Course> {

    @Autowired
    private SessionFactory sessionFactory;

    public CourseDAOImpl() {
        super.setClazz(Course.class);
        super.setPrimaryKey("courseId");
    }

    @Override
    public List<Course> getAllRecords() throws RuntimeException {
        return super.getAllRecords();
    }

    @Override
    public Integer addRecord(Course record) throws RuntimeException {
        return super.addRecord(record);
    }

    @Override
    public Course getRecord(String recordId) throws RuntimeException {
        return super.getRecord(recordId);
    }

    @Override
    public int updateRecord(Course record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<Course> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Course.class);
        Root<Course> root = criteriaUpdate.from(Course.class);
        criteriaUpdate.set("courseTitle", record.getCourseTitle());
        criteriaUpdate.set("courseDescription", record.getCourseDescription());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("courseId"), record.getCourseId()));

        return session.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        return super.deleteRecord(recordId);
    }

    @Override
    public List<Course> searchRecords(String searchCriterion) throws RuntimeException {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<Course> criteriaQuery = criteriaBuilder.createQuery(Course.class);
        Root<Course> root = criteriaQuery.from(Course.class);
        Predicate predicateForCourseTitle = criteriaBuilder.like(criteriaBuilder.lower(root.get("courseTitle")), "%" + searchCriterion.toLowerCase() + "%");
        Predicate predicateForCourseDescription = criteriaBuilder.like(criteriaBuilder.lower(root.get("courseDescription")), "%" + searchCriterion.toLowerCase() + "%");
        Predicate orPredicate = criteriaBuilder.or(predicateForCourseTitle, predicateForCourseDescription);

        criteriaQuery.select(root).where(orPredicate);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("courseId")));

        Query<Course> query = currentSession.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
