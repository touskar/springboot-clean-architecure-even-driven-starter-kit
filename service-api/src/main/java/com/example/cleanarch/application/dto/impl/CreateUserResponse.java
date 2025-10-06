package com.example.cleanarch.api.application.dto.impl;

import com.example.cleanarch.common.domain.entities.User;
import com.example.cleanarch.common.application.dto.contracts.IUseCaseResponse;
import java.util.List;

public class CreateUserResponse extends IUseCaseResponse<User> {

    public CreateUserResponse() {
        super();
    }

    public CreateUserResponse(boolean success, String message, List<String> errors, User data) {
        super();
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.data = data;
    }
}