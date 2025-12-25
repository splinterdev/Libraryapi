package com.github.IsaacMartins.libraryapi.controller.dto;

import org.springframework.http.HttpStatus;

import java.util.List;

public record ErrorResponse(int status, String message, List<InvalidField> errors) {

    public static ErrorResponse defaultResponse(String message) {
        return new ErrorResponse(HttpStatus.BAD_REQUEST.value(), message, List.of());
    }

    public static ErrorResponse conflict(String message) {
        return new ErrorResponse(HttpStatus.CONFLICT.value(), message, List.of());
    }
}
