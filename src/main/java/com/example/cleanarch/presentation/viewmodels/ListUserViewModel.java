package com.example.cleanarch.presentation.viewmodels;

import com.example.cleanarch.application.dto.contracts.ViewModel;
import com.example.cleanarch.application.dto.enums.ResponseCode;
import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
public class ListUserViewModel implements ViewModel {
    private ResponseCode code;
    private boolean success;
    private String message;
    private List<UserData> users;
    private int total;
    private int currentPage;
    private boolean hasNext;

    @Builder
    public ListUserViewModel(ResponseCode code, boolean success, String message, List<UserData> users, int total, int currentPage, boolean hasNext) {
        this.code = code;
        this.success = success;
        this.message = message;
        this.users = users;
        this.total = total;
        this.currentPage = currentPage;
        this.hasNext = hasNext;
    }

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