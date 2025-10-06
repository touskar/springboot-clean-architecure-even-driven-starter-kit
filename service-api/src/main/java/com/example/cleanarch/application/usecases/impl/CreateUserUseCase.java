package com.example.cleanarch.api.application.usecases.impl;

import com.example.cleanarch.common.application.usecases.contracts.IUseCase;
import com.example.cleanarch.common.application.dto.contracts.IUseCaseRequest;
import com.example.cleanarch.common.application.dto.contracts.IPresenter;
import com.example.cleanarch.api.application.dto.impl.CreateUserRequest;
import com.example.cleanarch.api.application.dto.impl.CreateUserResponse;
import com.example.cleanarch.api.domain.events.UserRegisteredEvent;
import com.example.cleanarch.common.domain.entities.User;
import com.example.cleanarch.common.domain.events.TestSharedEvent;
import com.example.cleanarch.common.domain.repositories.IUserRepository;
import com.example.cleanarch.common.domain.contracts.IDomainEventPublisher;
import com.example.cleanarch.common.infrastructure.utils.UlidGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CreateUserUseCase implements IUseCase {

    @Autowired
    private IUserRepository userRepository;

    @Autowired
    private IDomainEventPublisher eventPublisher;

    @Override
    public void execute(IUseCaseRequest request, IPresenter presenter) {
        CreateUserRequest createUserRequest = (CreateUserRequest) request;

        // Create user using repository
        User user = User.builder()
            .email(createUserRequest.getEmail())
            .build();
        user = userRepository.save(user);

        // Create response
        CreateUserResponse response = new CreateUserResponse(true, "User created successfully", null, user);

        // 1. Publish MODULE-SPECIFIC event (UserRegisteredEvent)
        UserRegisteredEvent userEvent = new UserRegisteredEvent(user);
        System.out.println("\n🚀 Publishing MODULE-SPECIFIC event: UserRegisteredEvent");
        System.out.println("   User: " + user.getName() + " (" + user.getEmail() + ")");
        eventPublisher.publish(userEvent);
        System.out.println("✅ UserRegisteredEvent published\n");

        // 2. Publish SHARED event (TestSharedEvent)
        TestSharedEvent.TestData testData = new TestSharedEvent.TestData(
            UlidGenerator.generate(),
            "Testing shared event from API service for user: " + user.getEmail()
        );
        TestSharedEvent testEvent = new TestSharedEvent(testData);
        System.out.println("🚀 Publishing SHARED event: TestSharedEvent");
        System.out.println("   Message: " + testData.getMessage());
        eventPublisher.publish(testEvent);
        System.out.println("✅ TestSharedEvent published\n");

        presenter.present(response);
    }
}