package com.example.cleanarch.presentation.controllers;

import com.example.cleanarch.application.usecases.impl.CreateUserUseCase;
import com.example.cleanarch.application.usecases.impl.ListUserUseCase;
import com.example.cleanarch.application.dto.impl.CreateUserRequest;
import com.example.cleanarch.application.dto.impl.ListUsersRequest;
import com.example.cleanarch.presentation.presenters.CreateUserPresenter;
import com.example.cleanarch.presentation.presenters.ListUserPresenter;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.List;

@RestController
public class UserController extends ApiResponse {

    private final CreateUserUseCase createUserUseCase;
    private final ListUserUseCase listUserUseCase;
    private final CreateUserPresenter createUserPresenter;
    private final ListUserPresenter listUserPresenter;

    public UserController(CreateUserUseCase createUserUseCase,
                         ListUserUseCase listUserUseCase,
                         CreateUserPresenter createUserPresenter,
                         ListUserPresenter listUserPresenter) {
        this.createUserUseCase = createUserUseCase;
        this.listUserUseCase = listUserUseCase;
        this.createUserPresenter = createUserPresenter;
        this.listUserPresenter = listUserPresenter;
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<?> register(@RequestBody CreateUserRequest request) {
        createUserUseCase.execute(request, createUserPresenter);
        return send(createUserPresenter.getViewModel());
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<?> listUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String search) {

        ListUsersRequest request = new ListUsersRequest(page, search);
        listUserUseCase.execute(request, listUserPresenter);
        return send(listUserPresenter.getViewModel());
    }
}