package com.github.IsaacMartins.libraryapi.controller.common;

import com.github.IsaacMartins.libraryapi.controller.dto.ErrorResponse;
import com.github.IsaacMartins.libraryapi.controller.dto.InvalidField;
import com.github.IsaacMartins.libraryapi.exceptions.DuplicatedRegisterException;
import com.github.IsaacMartins.libraryapi.exceptions.NotAllowedOperation;
import com.github.IsaacMartins.libraryapi.exceptions.RuleException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
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

    @ExceptionHandler(DuplicatedRegisterException.class)
    @ResponseStatus(HttpStatus.CONFLICT) // 409
    public ErrorResponse handleDuplicatedRegisterException(DuplicatedRegisterException e) {
        return ErrorResponse.conflict(e.getMessage());
    }

    @ExceptionHandler(NotAllowedOperation.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    public ErrorResponse handleNotAllowedOperation(NotAllowedOperation e) {
        return ErrorResponse.defaultResponse(e.getMessage());
    }

    @ExceptionHandler(RuleException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleRuleException(RuleException e) {
        return new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(), "Validation error.", List.of(new InvalidField(e.getField(), e.getMessage())));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleAccessDeniedException(AccessDeniedException e) {
        return new ErrorResponse(HttpStatus.FORBIDDEN.value(), "Access Denied.", List.of());
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleUntreatedErrors(RuntimeException e) {
        System.out.println(e.getMessage());
        return new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "An unexpected error occurred. Please contact the administration.", List.of());
    }
}
