package com.example.cleanarch.application.usecases.impl;

import com.example.cleanarch.application.usecases.contracts.IUseCase;
import com.example.cleanarch.application.dto.contracts.IUseCaseRequest;
import com.example.cleanarch.application.dto.contracts.IPresenter;
import com.example.cleanarch.application.dto.impl.ListUsersResponse;
import com.example.cleanarch.application.dto.PaginatedData;
import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.domain.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListUserUseCase implements IUseCase {

    @Autowired
    private UserService userService;

    @Override
    public void execute(IUseCaseRequest request, IPresenter presenter) {
        // ListUsersRequest listUsersRequest = (ListUsersRequest) request;
        // Get users from domain service
        List<User> users = userService.getAllUsers();

        // Create mock paginated data
        PaginatedData<User> paginatedData = PaginatedData.<User>builder()
            .data(users)
            .total(users.size())
            .currentPage(1)
            .hasNext(false)
            .build();

        ListUsersResponse response = new ListUsersResponse(true, "Users retrieved successfully", null, paginatedData);

        presenter.present(response);
    }
}