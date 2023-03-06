package com.validator;

public class ValidatorException extends Exception {
    private static final long serialVersionUID = -1356439454367756521L;

    public ValidatorException() {
    }

    public ValidatorException(String message) {
        super(message);
    }

    public ValidatorException(Throwable cause) {
        super(cause);
    }

    public ValidatorException(String message, Throwable cause) {
        super(message, cause);
    }
}
