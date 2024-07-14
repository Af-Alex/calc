package com.alexaf.salarycalc.exception;

public class MethodNotImplementedException extends RuntimeException {
    public MethodNotImplementedException(String message) {
        super(message + " не реализовано");
    }
}
