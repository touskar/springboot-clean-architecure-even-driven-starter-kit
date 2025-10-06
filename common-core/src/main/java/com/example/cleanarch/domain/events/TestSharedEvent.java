package com.example.cleanarch.common.domain.events;

import com.example.cleanarch.common.domain.events.contract.DomainEvent;

/**
 * Shared event for testing purposes - available to all modules
 */
public class TestSharedEvent extends DomainEvent<TestSharedEvent.TestData> {

    public TestSharedEvent(TestData data) {
        super(data);
    }

    @Override
    public String getAggregateId() {
        return data.getId();
    }

    public static class TestData {
        private final String id;
        private final String message;

        public TestData(String id, String message) {
            this.id = id;
            this.message = message;
        }

        public String getId() {
            return id;
        }

        public String getMessage() {
            return message;
        }
    }
}
