package com.rdlab.universityregistrar.model.dao;

import lombok.Setter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.*;
import java.util.List;

@Setter
public abstract class AbstractDAO<T> implements DAO<T> {
    @Autowired
    private SessionFactory sessionFactory;

    private Class<T> clazz;
    private String primaryKey;

    @Override
    public List<T> getAllRecords() throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get(primaryKey)));

        Query<T> query = session.createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public Integer addRecord(T record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();
        Integer generatedId = (Integer) session.save(record);
        session.refresh(record);
        return generatedId;
    }

    @Override
    public T getRecord(String recordId) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
        Root<T> root = criteriaQuery.from(clazz);
        criteriaQuery.select(root).where(criteriaBuilder.equal(root.get(primaryKey), Integer.parseInt(recordId)));

        Query<T> query = session.createQuery(criteriaQuery);
        return query.getSingleResult();
    }

    @Override
    public int updateRecord(T record) throws RuntimeException {
        return 0;
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaDelete<T> criteriaDelete = criteriaBuilder.createCriteriaDelete(clazz);
        Root<T> root = criteriaDelete.from(clazz);
        criteriaDelete.where(criteriaBuilder.equal(root.get(primaryKey), recordId));

        return session.createQuery(criteriaDelete).executeUpdate();
    }

    @Override
    public List<T> searchRecords(String searchCriterion) throws RuntimeException {
        return null;
    }
}
