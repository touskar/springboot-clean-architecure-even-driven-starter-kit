package com.example.cleanarch.api.application.usecases.impl;

import com.example.cleanarch.common.application.usecases.contracts.IUseCase;
import com.example.cleanarch.common.application.dto.contracts.IUseCaseRequest;
import com.example.cleanarch.common.application.dto.contracts.IPresenter;
import com.example.cleanarch.api.application.dto.impl.ListUsersResponse;
import com.example.cleanarch.common.application.dto.PaginatedData;
import com.example.cleanarch.common.domain.entities.User;
import com.example.cleanarch.common.domain.repositories.IUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ListUserUseCase implements IUseCase {

    @Autowired
    private IUserRepository userRepository;

    @Override
    public void execute(IUseCaseRequest request, IPresenter presenter) {
        // ListUsersRequest listUsersRequest = (ListUsersRequest) request;
        // Get users from repository
        List<User> users = userRepository.findAllActive();

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