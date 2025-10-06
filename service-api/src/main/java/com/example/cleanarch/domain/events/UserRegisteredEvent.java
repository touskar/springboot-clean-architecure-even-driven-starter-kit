package com.example.cleanarch.api.domain.events;

import com.example.cleanarch.common.domain.entities.User;
import com.example.cleanarch.common.domain.events.contract.DomainEvent;

/**
 * API-specific event: Fired when a user completes registration through the API
 */
public class UserRegisteredEvent extends DomainEvent<User> {

    public UserRegisteredEvent(User user) {
        super(user);
    }

    @Override
    public String getAggregateId() {
        return data.getId();
    }
}
