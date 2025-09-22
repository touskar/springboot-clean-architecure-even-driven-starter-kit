package com.example.cleanarch.application.dto.contracts;

import lombok.Getter;

@Getter
public abstract class IUseCaseRequest {
    protected Integer page;
    protected String search;
    protected IFilter filter;

    protected IUseCaseRequest() {}

    protected IUseCaseRequest(Integer page, String search, IFilter filter) {
        this.page = page;
        this.search = search;
        this.filter = filter;
    }
}