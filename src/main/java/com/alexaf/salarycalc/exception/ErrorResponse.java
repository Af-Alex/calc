package com.alexaf.salarycalc.exception;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@EqualsAndHashCode
public class ErrorResponse {

    private final UUID code = UUID.randomUUID();
    private String message = "An uncaught exception was thrown, ask admin what happened";

    public ErrorResponse(String message) {
        this.message = message;
    }
}
