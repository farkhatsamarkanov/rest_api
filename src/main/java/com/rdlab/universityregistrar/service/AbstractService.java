package com.rdlab.universityregistrar.service;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.service.mapper.DTOEntityMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Abstract service class representing common
 *
 * @Service class properties of implementing classes (DAO instance, DTO to entity mapper,
 * formalized response messages).
 * Implementation of {@link ServiceFunctionality} interface.
 */
@Getter
@Setter
@RequiredArgsConstructor
@Slf4j
@PropertySource("classpath:responseMessages.properties")
public abstract class AbstractService<S, D> implements ServiceFunctionality<D> {
    protected final DAO<S> dao;
    @Autowired
    DTOEntityMapper<S, D> mapper;
    @Value("${entity.getAllSuccessMessage}")
    private String getAllSuccessMessage;
    @Value("${entity.getEntitySuccessMessage}")
    private String getEntitySuccessMessage;
    @Value("${entity.addSuccessMessage}")
    private String addSuccessMessage;
    @Value("${entity.updateSuccessMessage}")
    private String updateSuccessMessage;
    @Value("${entity.deleteSuccessMessage}")
    private String deleteSuccessMessage;
    @Value("${entity.searchSuccessMessage}")
    private String searchSuccessMessage;
    @Value("${entity.entityNotFoundMessage}")
    private String entityNotFoundMessage;
    @Value("${entity.invalidInputMessage}")
    private String invalidInputMessage;
    @Value("${entity.internalServerErrorMessage}")
    private String internalServerErrorMessage;

    /**
     * Get all records of corresponding type from database.
     *
     * @return {@link Response} instance containing success message, timestamp and list of
     * all existing entities mapped to DTOs.
     */
    @Override
    public ResponseEntity<Response> getAllEntities() {
        return new ResponseEntity<>(Response.builder()
                .message(getAllSuccessMessage)
                .timeStamp(System.currentTimeMillis())
                .responseBody(mapper.entityListToDtoList(dao.getAllRecords()))
                .build(),
                HttpStatus.OK
        );
    }

    /**
     * Get single record of corresponding type from database
     *
     * @param entityId id of requested entity
     * @return {@link Response} instance containing success message, timestamp and
     * requested entity mapped to DTO
     */
    @Override
    public ResponseEntity<Response> getEntity(String entityId) {
        S fetchedEntity = dao.getRecord(entityId);
        if (fetchedEntity != null) {
            return new ResponseEntity<>(Response.builder()
                    .message(getEntitySuccessMessage + " " + entityId)
                    .timeStamp(System.currentTimeMillis())
                    .responseBody(mapper.entityToDto(fetchedEntity))
                    .build(),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(Response.builder()
                    .message(entityNotFoundMessage)
                    .timeStamp(System.currentTimeMillis())
                    .responseBody("")
                    .build(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Add record of corresponding type to database
     *
     * @param dto           DTO representing record that is needed to be added to database
     * @param bindingResult {@link BindingResult} instance containing all existing bean validation constraints
     * @return {@link ResponseEntity} instance with status code and {@link Response} instance
     *
     */
    @Override
    public ResponseEntity<Response> addEntity(D dto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            S entityToAdd = mapper.dtoToEntity(dto);
            Integer generatedId = dao.addRecord(entityToAdd);
            if (generatedId != null) {
                return new ResponseEntity<>(Response.builder()
                        .message(addSuccessMessage)
                        .timeStamp(System.currentTimeMillis())
                        .responseBody(mapper.entityToDto(entityToAdd))
                        .build(),
                        HttpStatus.CREATED
                );
            } else {
                return new ResponseEntity<>(Response.builder()
                        .message(internalServerErrorMessage)
                        .timeStamp(System.currentTimeMillis())
                        .responseBody("")
                        .build(),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }
        } else {
            return new ResponseEntity<>(Response.builder()
                    .message(invalidInputMessage)
                    .timeStamp(System.currentTimeMillis())
                    .responseBody(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toCollection(ArrayList::new)))
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Update record of corresponding type in database
     *
     * @param dto           DTO representing record that is needed to be updated
     * @param bindingResult {@link BindingResult} instance containing all existing bean validation constraints
     * @return {@link ResponseEntity} instance with status code and {@link Response} instance
     *
     */
    @Override
    public ResponseEntity<Response> updateEntity(D dto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            int rowsUpdated = dao.updateRecord(mapper.dtoToEntity(dto));
            if (rowsUpdated != 0) {
                return new ResponseEntity<>(Response.builder()
                        .message(updateSuccessMessage)
                        .timeStamp(System.currentTimeMillis())
                        .responseBody(dto)
                        .build(),
                        HttpStatus.OK
                );
            } else {
                return new ResponseEntity<>(Response.builder()
                        .message(entityNotFoundMessage)
                        .timeStamp(System.currentTimeMillis())
                        .responseBody("")
                        .build(),
                        HttpStatus.NOT_FOUND
                );
            }
        } else {
            return new ResponseEntity<>(Response.builder()
                    .message(invalidInputMessage)
                    .timeStamp(System.currentTimeMillis())
                    .responseBody(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toCollection(ArrayList::new)))
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    /**
     * Delete record from database with provided id
     *
     * @param entityId database record id
     * @return {@link ResponseEntity} instance with status code and {@link Response} instance
     *
     */
    @Override
    public ResponseEntity<Response> deleteEntity(String entityId) {
        if (dao.deleteRecord(entityId) != 0) {
            return new ResponseEntity<>(Response.builder()
                    .message(deleteSuccessMessage)
                    .timeStamp(System.currentTimeMillis())
                    .responseBody("")
                    .build(),
                    HttpStatus.OK
            );
        } else {
            return new ResponseEntity<>(Response.builder()
                    .message(entityNotFoundMessage)
                    .timeStamp(System.currentTimeMillis())
                    .responseBody("")
                    .build(),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    /**
     * Search for records meeting provided search criterion
     *
     * @param searchCriterion search criterion
     * @return {@link Response} instance containing search success message, timestamp and list of found DTOs representing
     * records that meet search criterion
     */
    @Override
    public ResponseEntity<Response> searchForEntities(String searchCriterion) {
        return new ResponseEntity<>(Response.builder()
                .message(searchSuccessMessage)
                .timeStamp(System.currentTimeMillis())
                .responseBody(mapper.entityListToDtoList(dao.searchRecords(searchCriterion)))
                .build(),
                HttpStatus.OK);
    }
}
