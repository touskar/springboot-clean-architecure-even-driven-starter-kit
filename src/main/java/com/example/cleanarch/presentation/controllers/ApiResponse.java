package com.example.cleanarch.presentation.controllers;

import com.example.cleanarch.application.dto.contracts.ViewModel;
import org.springframework.http.ResponseEntity;

public abstract class ApiResponse {

    protected ResponseEntity<?> send(ViewModel viewModel) {
        return ResponseEntity.status(viewModel.getCode().getHttpStatus()).body(viewModel);
    }
}