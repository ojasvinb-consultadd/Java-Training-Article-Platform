package com.ojasvinC.article_platform.exception;

import com.ojasvinC.article_platform.dto.ErrorResponse;
import org.springframework.boot.webmvc.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private final DefaultErrorAttributes errorAttributes;

    public GlobalExceptionHandler(DefaultErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

    //when given resource doesnt exist
    @ExceptionHandler(NotFoundException.class)
    public ErrorResponse handleNotFound(NotFoundException ex){
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    //when resouce already exists
    @ExceptionHandler(ConflictException.class)
    public ErrorResponse handleConflict(ConflictException ex){
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    //user is not authorised to access resource
    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponse handleForbidden(ForbiddenException ex){
        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                ex.getMessage(),
                null
        );
    }

    //when correct parameters are not provided to api
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleArgsNotValid(MethodArgumentNotValidException ex){

        Map<String,String> errors =
                ex.getBindingResult()
                .getFieldErrors()
                .stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),
                                error -> error.getDefaultMessage()
                        ));

        return new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                HttpStatus.FORBIDDEN.getReasonPhrase(),
                "validation failure",
                errors
        );
    }
}