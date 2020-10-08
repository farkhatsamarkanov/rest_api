package com.rdlab.universityregistrar.controller.exception.handler;

import com.rdlab.universityregistrar.controller.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.TransientObjectException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.orm.hibernate5.HibernateOptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.persistence.NoResultException;
import javax.persistence.OptimisticLockException;
import javax.persistence.PersistenceException;

/**
 * {@link ControllerAdvice} implementation. Class for intercepting and
 * processing exceptions thrown by application. Wraps up exception messages
 * to readable ResponseEntity format.
 */
@ControllerAdvice("com.rdlab.universityregistrar.controller")
@EnableWebMvc
@PropertySource("classpath:responseMessages.properties")
@Slf4j
public class GlobalControllerExceptionHandler {
    @Value("${entity.internalServerErrorMessage}")
    private String internalServerErrorMessage;
    @Value("${entity.invalidInputMessage}")
    private String invalidInputMessage;
    @Value("${entity.duplicateEntryMessage}")
    private String duplicateEntryMessage;
    @Value("${entity.entityNotFoundMessage}")
    private String entityNotFoundMessage;

    @ExceptionHandler
    public ResponseEntity<Response> handleGenericException(Exception e) {
        e.printStackTrace();
        log.error(e.getMessage());
        return new ResponseEntity<>(Response.builder()
                .message(internalServerErrorMessage)
                .timeStamp(System.currentTimeMillis())
                .build(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler
    public ResponseEntity<Response> handleNotReadableException(HttpMessageNotReadableException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Response.builder()
                .message(invalidInputMessage)
                .timeStamp(System.currentTimeMillis())
                .responseBody("Cannot parse input data, please check input data format")
                .build(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<Response> handleDuplicateException(PersistenceException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Response.builder()
                .message(duplicateEntryMessage)
                .timeStamp(System.currentTimeMillis())
                .responseBody("")
                .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    public ResponseEntity<Response> handleNoResultException(NoResultException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Response.builder()
                .message(entityNotFoundMessage)
                .timeStamp(System.currentTimeMillis())
                .responseBody("")
                .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler
    public ResponseEntity<Response> handleTransientObjectException(TransientObjectException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(Response.builder()
                .message(invalidInputMessage)
                .timeStamp(System.currentTimeMillis())
                .responseBody("id is empty!")
                .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
