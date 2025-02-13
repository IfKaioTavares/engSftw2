package com.centroSer.app.infra.api;

import com.centroSer.app.infra.exceptions.BadRequestException;
import com.centroSer.app.infra.exceptions.ConflictException;
import com.centroSer.app.infra.exceptions.ResourceNotFoundException;
import com.centroSer.app.infra.exceptions.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    @ExceptionHandler(BadRequestException.class)
    private ResponseEntity<RestErrorMessage> handleBadRequestException(BadRequestException ex) {
        return handleException(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConflictException.class)
    private ResponseEntity<RestErrorMessage> handleConflictException(ConflictException ex) {
        return handleException(ex, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    private ResponseEntity<RestErrorMessage> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return handleException(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedException.class)
    private ResponseEntity<RestErrorMessage> handleUnauthorizedException(UnauthorizedException ex) {
        return handleException(ex, HttpStatus.UNAUTHORIZED);
    }

    private ResponseEntity<RestErrorMessage> handleException(RuntimeException ex, HttpStatus status) {
        var threatResponse = new RestErrorMessage(status, ex.getMessage());
        return ResponseEntity.status(status).body(threatResponse);
    }
}
