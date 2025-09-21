package com.example.cleanarch.application.dto.impl;

import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.application.dto.contracts.IUseCaseResponse;
import com.example.cleanarch.application.dto.PaginatedData;
import java.util.List;

public class ListUsersResponse extends IUseCaseResponse<PaginatedData<User>> {

    public ListUsersResponse() {
        super();
    }

    public ListUsersResponse(boolean success, String message, List<String> errors, PaginatedData<User> data) {
        super();
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.data = data;
    }
}