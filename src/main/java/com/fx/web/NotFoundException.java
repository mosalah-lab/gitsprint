package com.fx.web;

/**
 * Thrown when a requested resource (e.g. an unknown currency pair) doesn't exist.
 * Handled by {@link ApiExceptionHandler} -> a clean 404 JSON {error}, never a stack trace.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}
