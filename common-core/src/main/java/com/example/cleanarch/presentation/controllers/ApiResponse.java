package com.example.cleanarch.common.presentation.controllers;

import com.example.cleanarch.common.application.dto.contracts.ViewModel;
import org.springframework.http.ResponseEntity;

public abstract class ApiResponse {

    protected ResponseEntity<?> send(ViewModel viewModel) {
        return ResponseEntity.status(viewModel.getCode().getHttpStatus()).body(viewModel);
    }
}