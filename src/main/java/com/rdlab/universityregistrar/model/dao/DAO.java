package com.rdlab.universityregistrar.model.dao;

import java.util.List;

/**
 * DAO interface for performing operations with database
 *
 * @param <T> entity type of DAO
 */
public interface DAO<T> {
    List<T> getAllRecords() throws RuntimeException;

    Integer addRecord(T record) throws RuntimeException;

    T getRecord(String recordId) throws RuntimeException;

    int updateRecord(T record) throws RuntimeException;

    int deleteRecord(String recordId) throws RuntimeException;

    List<T> searchRecords(String searchCriterion) throws RuntimeException;
}
