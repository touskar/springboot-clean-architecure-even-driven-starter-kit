package com.example.cleanarch.api.application.dto.impl;

import com.example.cleanarch.common.application.dto.contracts.IUseCaseRequest;

public class ListUsersRequest extends IUseCaseRequest {

    public ListUsersRequest(Integer page, String search) {
        super(page, search, null);
    }
}