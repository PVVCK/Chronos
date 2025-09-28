package com.airtribe.chronos.exception;


import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;

import javax.naming.ServiceUnavailableException;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.airtribe.chronos.response.APIResponse;

//import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.UniqueConstraint;

@RestControllerAdvice
public class ChronosExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse> handleException(Exception ex) {
        String errorMessage = ex.getMessage();
        String apiResponseMessage;
        HttpStatus httpStatus;

//        if(ex instanceof ExpiredJwtException)
//        {
//            httpStatus = HttpStatus.UNAUTHORIZED;
//            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage;
//        }

        if (ex instanceof NotFoundException || ex instanceof NoSuchElementException || ex instanceof NoHandlerFoundException)
        {
            httpStatus = HttpStatus.NOT_FOUND;
            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage;
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage);
        }
        else if (ex instanceof NullPointerException)
        {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage;
//        	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error:- ExceptionType[ "+ex.getClass()+" ] "+ errorMessage);
        }
        else if (ex instanceof IllegalArgumentException || ex instanceof MultipartException)
        {
            httpStatus = HttpStatus.BAD_REQUEST;
            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage;
//		    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage);
        }
        else if (ex instanceof ServiceUnavailableException)
        {
            httpStatus = HttpStatus.SERVICE_UNAVAILABLE;
            apiResponseMessage = "Error:- ExceptionTypce[ "+ex.getClass()+" ] " + errorMessage;
//		    return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Error:- ExceptionType[ "+ex.getClass()+" ] " +errorMessage);
        }
        else if (ex instanceof AccessDeniedException || ex instanceof SecurityException || ex instanceof DataAccessException)
        {
            httpStatus = HttpStatus.FORBIDDEN;
            apiResponseMessage = "Error:- ExceptionTypde[ "+ex.getClass()+" ] " + errorMessage;
//		    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Error:- ExceptionType[ "+ex.getClass()+" ] " +errorMessage);
        }

        else if (ex instanceof DataIntegrityViolationException || ex instanceof UniqueConstraint)
        {
            httpStatus = HttpStatus.CONFLICT;
            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage;
//			 return ResponseEntity.status(HttpStatus.CONFLICT).body("Error:- ExceptionType[" +ex.getClass()+" ] " + errorMessage);
        }
        else if (ex instanceof ResourceAccessException)
        {
            httpStatus = HttpStatus.REQUEST_TIMEOUT;
            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage;
//			 return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Error:- ExceptionType[" +ex.getClass()+" ] " + errorMessage);
        }

        else if (ex instanceof NoResourceFoundException)
        {
            httpStatus = HttpStatus.BAD_REQUEST;
            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] Wrong API schema - " + errorMessage;
//			 return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Error:- ExceptionType[" +ex.getClass()+" ] " + errorMessage);
        }

        else if (ex instanceof ExecutionFailed)
        {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

            apiResponseMessage = "Error:- " + errorMessage;

        }
        else
        {
            apiResponseMessage = "Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage;
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error:- ExceptionType[ "+ex.getClass()+" ] " + errorMessage);
        }

        APIResponse response = new APIResponse(
                false, // success
                LocalDateTime.now(),
                null, // no data
                apiResponseMessage
        );

        return ResponseEntity.status(httpStatus).body(response);
    }
}
