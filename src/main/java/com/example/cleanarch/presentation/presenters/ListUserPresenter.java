package com.example.cleanarch.presentation.presenters;

import com.example.cleanarch.application.dto.contracts.IPresenter;
import com.example.cleanarch.application.dto.contracts.IUseCaseResponse;
import com.example.cleanarch.application.dto.contracts.ViewModel;
import com.example.cleanarch.application.dto.PaginatedData;
import com.example.cleanarch.application.dto.enums.ResponseCode;
import com.example.cleanarch.presentation.viewmodels.ListUserViewModel;
import com.example.cleanarch.domain.entities.User;
import java.util.List;

public class ListUserPresenter implements IPresenter<PaginatedData<User>> {
    private IUseCaseResponse<PaginatedData<User>> response;
    private ListUserViewModel viewModel;

    @Override
    public void present(IUseCaseResponse<PaginatedData<User>> response) {
        this.response = response;

        List<ListUserViewModel.UserData> users = response.getData() != null && response.getData().getData() != null
            ? response.getData().getData().stream()
                .map(user -> ListUserViewModel.UserData.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .build())
                .toList()
            : List.of();

        this.viewModel = new ListUserViewModel(
            response.getSuccess() ? ResponseCode.OK : ResponseCode.BAD_REQUEST,
            response.getSuccess(),
            response.getMessage(),
            users,
            response.getData() != null ? response.getData().getTotal() : 0,
            response.getData() != null ? response.getData().getCurrentPage() : 0,
            response.getData() != null ? response.getData().isHasNext() : false
        );
    }

    @Override
    public ViewModel getViewModel() {
        return viewModel;
    }
}