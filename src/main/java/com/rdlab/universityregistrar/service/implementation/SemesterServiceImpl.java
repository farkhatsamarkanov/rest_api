package com.rdlab.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.entity.Semester;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.SemesterDTO;
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
public class SemesterServiceImpl extends AbstractService<Semester, SemesterDTO> {

    @Autowired
    public SemesterServiceImpl(DAO<Semester> dao) {
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
    public ResponseEntity<Response> addEntity(SemesterDTO dto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (isSemesterStartsBeforeEndTime(dto)) {
                Semester entityToAdd = getMapper().dtoToEntity(dto);
                Integer generatedId = dao.addRecord(entityToAdd);
                if (generatedId != null) {
                    return new ResponseEntity<>(Response.builder()
                            .message(getAddSuccessMessage())
                            .timeStamp(System.currentTimeMillis())
                            .responseBody(getMapper().entityToDto(entityToAdd))
                            .build(),
                            HttpStatus.CREATED
                    );
                } else {
                    return new ResponseEntity<>(Response.builder()
                            .message(getInternalServerErrorMessage())
                            .timeStamp(System.currentTimeMillis())
                            .responseBody("")
                            .build(),
                            HttpStatus.INTERNAL_SERVER_ERROR
                    );
                }
            } else {
                return new ResponseEntity<>(Response.builder()
                        .message(super.getInvalidInputMessage())
                        .timeStamp(System.currentTimeMillis())
                        .responseBody("semester end time cannot be before semester begin time")
                        .build(),
                        HttpStatus.BAD_REQUEST
                );
            }
        } else {
            return new ResponseEntity<>(Response.builder()
                    .message(getInvalidInputMessage())
                    .timeStamp(System.currentTimeMillis())
                    .responseBody(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toCollection(ArrayList::new)))
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    @Transactional
    @Override
    public ResponseEntity<Response> updateEntity(SemesterDTO dto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            if (isSemesterStartsBeforeEndTime(dto)) {
                int rowsUpdated = dao.updateRecord(getMapper().dtoToEntity(dto));
                if (rowsUpdated != 0) {
                    return new ResponseEntity<>(Response.builder()
                            .message(getUpdateSuccessMessage())
                            .timeStamp(System.currentTimeMillis())
                            .responseBody(dto)
                            .build(),
                            HttpStatus.OK
                    );
                } else {
                    return new ResponseEntity<>(Response.builder()
                            .message(getEntityNotFoundMessage())
                            .timeStamp(System.currentTimeMillis())
                            .responseBody("")
                            .build(),
                            HttpStatus.NOT_FOUND
                    );
                }
            } else {
                return new ResponseEntity<>(Response.builder()
                        .message(super.getInvalidInputMessage())
                        .timeStamp(System.currentTimeMillis())
                        .responseBody("semester end time cannot be before semester begin time")
                        .build(),
                        HttpStatus.BAD_REQUEST
                );
            }
        } else {
            return new ResponseEntity<>(Response.builder()
                    .message(getInvalidInputMessage())
                    .timeStamp(System.currentTimeMillis())
                    .responseBody(bindingResult.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toCollection(ArrayList::new)))
                    .build(),
                    HttpStatus.BAD_REQUEST
            );
        }
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

    /**
     * Check if semester start time is before semester end time.
     * If not, return invalid input message to client.
     */

    private boolean isSemesterStartsBeforeEndTime(SemesterDTO dto) {
        return dto.getSemesterStartTime().before(dto.getSemesterEndTime());
    }
}
