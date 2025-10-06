package com.example.cleanarch.domain.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;

import java.io.IOException;

/**
 * Jackson deserializer for Maybe<T>.
 * Handles null values and creates appropriate Maybe instances.
 */
public class MaybeDeserializer extends JsonDeserializer<Maybe<?>> implements ContextualDeserializer {

    private JavaType valueType;

    public MaybeDeserializer() {
    }

    public MaybeDeserializer(JavaType valueType) {
        this.valueType = valueType;
    }

    @Override
    public Maybe<?> deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException {

        // Handle null token
        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return Maybe.empty();
        }

        // Deserialize the actual value
        if (valueType != null && valueType.containedTypeCount() > 0) {
            JavaType type = valueType.containedType(0);
            Object value = ctxt.readValue(p, type);
            return Maybe.ofNullable(value);
        } else {
            // Fallback to Object if type is not available
            Object value = p.readValueAs(Object.class);
            return Maybe.ofNullable(value);
        }
    }

    @Override
    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty property) {
        JavaType type = ctxt.getContextualType() != null
            ? ctxt.getContextualType()
            : property.getType();
        return new MaybeDeserializer(type);
    }
}
