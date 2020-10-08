package com.rdlab.universityregistrar.service.implementation;

import com.rdlab.universityregistrar.controller.response.Response;
import com.rdlab.universityregistrar.model.dao.DAO;
import com.rdlab.universityregistrar.model.dao.implementation.ScheduleEntryDAOImpl;
import com.rdlab.universityregistrar.model.dao.implementation.UserDAOImpl;
import com.rdlab.universityregistrar.model.entity.ScheduleEntry;
import com.rdlab.universityregistrar.model.entity.User;
import com.rdlab.universityregistrar.service.AbstractService;
import com.rdlab.universityregistrar.service.dto.ScheduleEntryDTO;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;

import javax.persistence.NoResultException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.stream.Collectors;


@Service
@Getter
@Setter
@Slf4j
public class ScheduleEntryServiceImpl extends AbstractService<ScheduleEntry, ScheduleEntryDTO> {
    private long recommendedCourseNumberPerStudent = 5L;
    private int numberOfDaysBeforeStudentNeedsToBeActive = 5;

    @Autowired
    public ScheduleEntryServiceImpl(DAO<ScheduleEntry> dao) {
        super(dao);
    }

    @Autowired
    private DAO<User> userDAO;

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
    public ResponseEntity<Response> addEntity(ScheduleEntryDTO dto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            Integer generatedId = dao.addRecord(getMapper().dtoToEntity(dto));
            if (generatedId != null) {
                dto.setEntryId(generatedId);
                checkIfStudentOfCourseIsActive(dto);
                checkNumberOfTakenCoursesForStudent(dto);
                return new ResponseEntity<>(Response.builder()
                        .message(getAddSuccessMessage())
                        .timeStamp(System.currentTimeMillis())
                        .responseBody(dto)
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
    public ResponseEntity<Response> updateEntity(ScheduleEntryDTO dto, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            int rowsUpdated = dao.updateRecord(getMapper().dtoToEntity(dto));
            if (rowsUpdated != 0) {
                checkIfStudentOfCourseIsActive(dto);
                checkNumberOfTakenCoursesForStudent(dto);
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

    private void checkIfStudentOfCourseIsActive(ScheduleEntryDTO dtoBeingChecked) {
        Boolean isCourseStudentActive = null;
        try {
            isCourseStudentActive = ((UserDAOImpl) userDAO).getUserByStudentId(dtoBeingChecked.getStudentId()).getIsActive();
        } catch (NoResultException e) {
            log.error(e.getMessage());
            super.setAddSuccessMessage(super.getAddSuccessMessage() + " WARNING: Course begins in less than 5 days, but student is not registered as an user!");
        }
        LocalDateTime dateOfCourseBeginning = dtoBeingChecked.getTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime currentDate = LocalDateTime.now();
        if (isCourseStudentActive != null && !isCourseStudentActive) {
            if (Math.abs(ChronoUnit.DAYS.between(dateOfCourseBeginning, currentDate)) < numberOfDaysBeforeStudentNeedsToBeActive) {
                super.setAddSuccessMessage(super.getAddSuccessMessage() + " WARNING: Course begins in less than 5 days, but student is not active!");
            }
        }
    }

    private void checkNumberOfTakenCoursesForStudent(ScheduleEntryDTO dtoBeingChecked) {
        if (((ScheduleEntryDAOImpl) dao).getNumberOfTakenCoursesForStudent(dtoBeingChecked.getStudentId(), dtoBeingChecked.getSemesterId()) > recommendedCourseNumberPerStudent) {
            super.setAddSuccessMessage(super.getAddSuccessMessage() + " WARNING: Student has taken more than 5 courses in single semester!");
        }
    }

}
