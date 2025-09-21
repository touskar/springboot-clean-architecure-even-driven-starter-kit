package com.example.cleanarch.application.dto.contracts;

public abstract class IUseCaseRequest {
    protected Integer page;
    protected String search;
    protected IFilter filter;
}