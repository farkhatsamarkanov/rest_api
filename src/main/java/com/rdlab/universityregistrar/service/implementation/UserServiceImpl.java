package com.rdlab.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.User;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;


@Service
public class UserServiceImpl extends AbstractService<User, UserDTO> {

    @Autowired
    public UserServiceImpl(DAO<User> dao) {
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
    public ResponseEntity<Response> addEntity(UserDTO dto, BindingResult bindingResult) {
        return super.addEntity(dto, bindingResult);
    }

    @Transactional
    @Override
    public ResponseEntity<Response> updateEntity(UserDTO dto, BindingResult bindingResult) {
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
