package com.example.cleanarch.application.dto.impl;

import com.example.cleanarch.application.dto.contracts.IUseCaseRequest;

public class CreateUserRequest extends IUseCaseRequest {
    private String name;
    private String email;

    public CreateUserRequest() {}

    public CreateUserRequest(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}