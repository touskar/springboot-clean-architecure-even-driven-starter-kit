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

    public UserController(CreateUserUseCase createUserUseCase, ListUserUseCase listUserUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.listUserUseCase = listUserUseCase;
    }

    @PostMapping("/api/v1/register")
    public ResponseEntity<?> register(@RequestBody CreateUserRequest request) {
        CreateUserPresenter presenter = new CreateUserPresenter();

        createUserUseCase.execute(request, presenter);
        return send(presenter.getViewModel());
    }

    @GetMapping("/api/v1/users")
    public ResponseEntity<?> listUsers(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) String search) {

        ListUsersRequest request = new ListUsersRequest();
        ListUserPresenter presenter = new ListUserPresenter();

        listUserUseCase.execute(request, presenter);
        return send(presenter.getViewModel());
    }
}