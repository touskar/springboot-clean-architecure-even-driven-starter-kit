package com.example.cleanarch.application.dto.contracts;

import java.util.Date;
import java.util.List;

public abstract class IUseCaseResponse<T> {
    protected boolean success;
    protected String message;
    protected List<String> errors;
    protected T data;
    protected Date timestamp;

    protected IUseCaseResponse() {
        this.timestamp = new Date();
    }

    public boolean getSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public T getData() {
        return data;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}