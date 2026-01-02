package com.github.IsaacMartins.libraryapi.exceptions;

import lombok.Getter;

public class RuleException extends RuntimeException {

    @Getter
    private String field;

    public RuleException(String field, String message) {
        super(message);
        this.field = field;
    }
}
