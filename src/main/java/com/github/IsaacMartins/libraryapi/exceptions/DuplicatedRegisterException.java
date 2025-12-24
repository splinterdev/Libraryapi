package com.github.IsaacMartins.libraryapi.exceptions;

public class DuplicatedRegisterException extends RuntimeException {
    public DuplicatedRegisterException(String message) {
        super(message);
    }
}
