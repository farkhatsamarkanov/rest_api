package com.rdlab.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Lecturer;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.LecturerDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
public class LecturerServiceImpl extends AbstractService<Lecturer, LecturerDTO> {

    @Autowired
    public LecturerServiceImpl(DAO<Lecturer> dao) {
        super(dao);
    }

    @Transactional
    @Override
    public ResponseEntity<Response> getAllEntities() {
        return super.getAllEntities();
    }

    @Transactional
    @Override
    public ResponseEntity<Response> getEntity(String entityId) {
        return super.getEntity(entityId);
    }

    @Transactional
    @Override
    public ResponseEntity<Response> addEntity(LecturerDTO dto, BindingResult bindingResult) {
        return super.addEntity(dto, bindingResult);
    }

    @Transactional
    @Override
    public ResponseEntity<Response> updateEntity(LecturerDTO dto, BindingResult bindingResult) {
        return super.updateEntity(dto, bindingResult);
    }

    @Transactional
    @Override
    public ResponseEntity<Response> deleteEntity(String entityId) {
        return super.deleteEntity(entityId);
    }

    @Transactional
    @Override
    public ResponseEntity<Response> searchForEntities(String searchCriterion) {
        return super.searchForEntities(searchCriterion);
    }
}
