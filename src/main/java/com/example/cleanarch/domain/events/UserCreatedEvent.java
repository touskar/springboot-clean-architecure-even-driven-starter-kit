package com.example.cleanarch.domain.events;

import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.domain.events.contract.DomainEvent;

public class UserCreatedEvent extends DomainEvent<User> {

    public UserCreatedEvent(User user) {
        super(user);
    }

    @Override
    public String getAggregateId() {
        return data.getId();
    }
}