package com.rightdata.explorer.exception;

public class ItemAlreadyExistsException extends RuntimeException {

    public ItemAlreadyExistsException(String message) {
        super(message);
    }

    public ItemAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }
}