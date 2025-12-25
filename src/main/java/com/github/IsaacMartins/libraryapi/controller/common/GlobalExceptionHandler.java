package com.github.IsaacMartins.libraryapi.controller.common;


import com.github.IsaacMartins.libraryapi.controller.dto.ErrorResponse;
import com.github.IsaacMartins.libraryapi.controller.dto.InvalidField;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // sempre retorna esse status (422)
    public ErrorResponse handleMethodArgsNotValidException(MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getFieldErrors();
        List<InvalidField> errors = fieldErrors.stream().map(fe -> new InvalidField(fe.getField(), fe.getDefaultMessage())).toList();

        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error.", errors);
    }
}
