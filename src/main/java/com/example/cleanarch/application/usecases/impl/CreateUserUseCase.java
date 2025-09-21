package com.example.cleanarch.application.usecases.impl;

import com.example.cleanarch.application.usecases.contracts.IUseCase;
import com.example.cleanarch.application.dto.contracts.IUseCaseRequest;
import com.example.cleanarch.application.dto.contracts.IPresenter;
import com.example.cleanarch.application.dto.contracts.IUseCaseResponse;
import com.example.cleanarch.application.dto.impl.CreateUserRequest;
import com.example.cleanarch.application.dto.impl.CreateUserResponse;
import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.domain.events.UserCreatedEvent;
import com.example.cleanarch.domain.services.UserService;
import com.example.cleanarch.domain.contracts.IDomainEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase implements IUseCase {

    @Autowired
    private UserService userService;

    @Autowired
    private IDomainEventPublisher eventPublisher;

    @Override
    public void execute(IUseCaseRequest request, IPresenter presenter) {
        CreateUserRequest createUserRequest = (CreateUserRequest) request;

        // Create user using domain service
        User user = userService.createUser(createUserRequest.getName(), createUserRequest.getEmail());

        // Create response
        CreateUserResponse response = new CreateUserResponse(true, "User created successfully", null, user);

        // Publish USER_CREATED event
        UserCreatedEvent event = new UserCreatedEvent(user);
        System.out.println("🚀 DEBUG: Publishing UserCreatedEvent for user: " + user.getName() + " (" + user.getEmail() + ")");
        System.out.println("🚀 DEBUG: Event ID: " + event.getAggregateId());
        eventPublisher.publish(event);
        System.out.println("✅ DEBUG: Event published successfully");

        presenter.present(response);
    }
}