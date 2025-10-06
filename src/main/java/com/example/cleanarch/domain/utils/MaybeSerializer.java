package com.example.cleanarch.domain.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;

/**
 * Jackson serializer for Maybe<T>.
 * Serializes present values normally, and empty Maybe as null.
 */
public class MaybeSerializer extends JsonSerializer<Maybe<?>> {

    @Override
    public void serialize(Maybe<?> value, JsonGenerator gen, SerializerProvider serializers)
            throws IOException {
        if (value == null || value.isEmpty()) {
            gen.writeNull();
        } else {
            gen.writeObject(value.get());
        }
    }
}
