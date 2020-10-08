package com.rdlab.universityregistrar.model.dao.implementation;

import com.rdlab.universityregistrar.model.dao.AbstractDAO;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.AcademicRank;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.criteria.*;
import java.util.List;

/**
 * {@link DAO} implementation for {@link AcademicRank} entity type
 */

@Repository
public class AcademicRankDAOImpl extends AbstractDAO<AcademicRank> {

    @Autowired
    private SessionFactory sessionFactory;

    public AcademicRankDAOImpl() {
        super.setClazz(AcademicRank.class);
        super.setPrimaryKey("numericRank");
    }

    @Override
    public List<AcademicRank> getAllRecords() throws RuntimeException {
        return super.getAllRecords();
    }

    @Override
    public Integer addRecord(AcademicRank record) throws RuntimeException {
        return super.addRecord(record);
    }

    @Override
    public AcademicRank getRecord(String recordId) throws RuntimeException {
        return super.getRecord(recordId);
    }

    @Override
    public int updateRecord(AcademicRank record) throws RuntimeException {
        Session session = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<AcademicRank> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(AcademicRank.class);
        Root<AcademicRank> root = criteriaUpdate.from(AcademicRank.class);
        criteriaUpdate.set("rankName", record.getRankName());
        criteriaUpdate.set("numericRank", record.getNumericRank());
        criteriaUpdate.where(criteriaBuilder.equal(root.get("rankId"), record.getRankId()));

        return session.createQuery(criteriaUpdate).executeUpdate();
    }

    @Override
    public int deleteRecord(String recordId) throws RuntimeException {
        return super.deleteRecord(recordId);
    }

    @Override
    public List<AcademicRank> searchRecords(String searchCriterion) throws RuntimeException {
        Session currentSession = sessionFactory.getCurrentSession();

        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<AcademicRank> criteriaQuery = criteriaBuilder.createQuery(AcademicRank.class);
        Root<AcademicRank> root = criteriaQuery.from(AcademicRank.class);
        Predicate predicateForRankName = criteriaBuilder.like(criteriaBuilder.lower(root.get("rankName")), "%" + searchCriterion.toLowerCase() + "%");

        criteriaQuery.select(root).where(predicateForRankName);
        criteriaQuery.orderBy(criteriaBuilder.asc(root.get("numericRank")));

        Query<AcademicRank> query = currentSession.createQuery(criteriaQuery);
        return query.getResultList();
    }
}
