package com.example.cleanarch.common.infrastructure.messaging.contract;

import com.example.cleanarch.common.domain.events.contract.DomainEvent;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoEventHandler {
    Class<? extends DomainEvent> value();
}