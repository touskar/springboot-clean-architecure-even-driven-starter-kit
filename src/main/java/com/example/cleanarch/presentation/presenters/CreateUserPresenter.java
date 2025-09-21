package com.example.cleanarch.presentation.presenters;

import com.example.cleanarch.application.dto.contracts.IPresenter;
import com.example.cleanarch.application.dto.contracts.IUseCaseResponse;
import com.example.cleanarch.application.dto.contracts.ViewModel;
import com.example.cleanarch.application.dto.enums.ResponseCode;
import com.example.cleanarch.presentation.viewmodels.CreateUserViewModel;
import com.example.cleanarch.domain.entities.User;

public class CreateUserPresenter implements IPresenter<User> {
    private IUseCaseResponse<User> response;
    private CreateUserViewModel viewModel;

    @Override
    public void present(IUseCaseResponse<User> response) {
        this.response = response;

        CreateUserViewModel.UserData userData = null;
        if (response.getData() != null) {
            userData = CreateUserViewModel.UserData.builder()
                .id(response.getData().getId())
                .name(response.getData().getName())
                .email(response.getData().getEmail())
                .build();
        }

        this.viewModel = CreateUserViewModel.builder()
            .code(response.getSuccess() ? ResponseCode.CREATED : ResponseCode.BAD_REQUEST)
            .success(response.getSuccess())
            .message(response.getMessage())
            .data(userData)
            .build();
    }

    @Override
    public ViewModel getViewModel() {
        return viewModel;
    }

    public IUseCaseResponse<User> present() {
        return response;
    }
}