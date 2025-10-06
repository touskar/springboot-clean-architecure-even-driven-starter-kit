package com.example.cleanarch.api.presentation.viewmodels;

import com.example.cleanarch.common.application.dto.contracts.ViewModel;
import com.example.cleanarch.common.application.dto.enums.ResponseCode;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateUserViewModel implements ViewModel {
    private ResponseCode code;
    private boolean success;
    private String message;
    private UserData data;

    @Override
    public ResponseCode getCode() {
        return code;
    }

    @Getter
    @Builder
    public static class UserData {
        private String id;
        private String name;
        private String email;
    }
}