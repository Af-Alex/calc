package com.alexaf.salarycalc.exception;

import static java.lang.String.format;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Class<?> entityClass, Object id) {
        super(format("%s with id [%s] not found", entityClass.getSimpleName(), id));
    }

    public EntityNotFoundException(String message) {
        super(message);
    }
}
