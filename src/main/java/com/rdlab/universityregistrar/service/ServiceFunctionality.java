package com.rdlab.universityregistrar.service;

import com.rdlab.universityregistrar.controller.response.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;

/**
 * Interface representing main functionality for
 *
 * @Service classes to perform operations
 * with corresponding DAOs.
 */
public interface ServiceFunctionality<T> {

    ResponseEntity<Response> getAllEntities();

    ResponseEntity<Response> getEntity(String entityId);

    ResponseEntity<Response> addEntity(T dto, BindingResult bindingResult);

    ResponseEntity<Response> updateEntity(T dto, BindingResult bindingResult);

    ResponseEntity<Response> deleteEntity(String entityId);

    ResponseEntity<Response> searchForEntities(String searchCriterion);
}
