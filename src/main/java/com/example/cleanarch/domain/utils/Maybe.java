package com.example.cleanarch.domain.utils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A container object which may or may not contain a non-null value.
 * Similar to Optional but designed for JSON serialization.
 *
 * Unlike Optional<T>, Maybe<T> is serializable and works seamlessly with Jackson.
 *
 * @param <T> the type of value
 */
@JsonSerialize(using = MaybeSerializer.class)
@JsonDeserialize(using = MaybeDeserializer.class)
public class Maybe<T> {

    private final T value;

    private Maybe(T value) {
        this.value = value;
    }

    /**
     * Returns a Maybe with the specified present non-null value.
     */
    public static <T> Maybe<T> of(T value) {
        return new Maybe<>(Objects.requireNonNull(value));
    }

    /**
     * Returns a Maybe with the specified value, which may be null.
     */
    public static <T> Maybe<T> ofNullable(T value) {
        return new Maybe<>(value);
    }

    /**
     * Returns an empty Maybe instance.
     */
    public static <T> Maybe<T> empty() {
        return new Maybe<>(null);
    }

    /**
     * If a value is present, returns true, otherwise false.
     */
    public boolean isPresent() {
        return value != null;
    }

    /**
     * If a value is not present, returns true, otherwise false.
     */
    public boolean isEmpty() {
        return value == null;
    }

    /**
     * If a value is present, returns the value, otherwise throws NoSuchElementException.
     */
    public T get() {
        if (value == null) {
            throw new NoSuchElementException("No value present");
        }
        return value;
    }

    /**
     * If a value is present, returns the value, otherwise returns other.
     */
    public T orElse(T other) {
        return value != null ? value : other;
    }

    /**
     * If a value is present, returns the value, otherwise returns the result
     * produced by the supplying function.
     */
    public T orElseGet(Supplier<? extends T> supplier) {
        return value != null ? value : supplier.get();
    }

    /**
     * If a value is present, returns the value, otherwise throws an exception
     * produced by the exception supplying function.
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return value;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise does nothing.
     */
    public void ifPresent(Consumer<? super T> action) {
        if (value != null) {
            action.accept(value);
        }
    }

    /**
     * If a value is present, performs the given action with the value,
     * otherwise performs the given empty-based action.
     */
    public void ifPresentOrElse(Consumer<? super T> action, Runnable emptyAction) {
        if (value != null) {
            action.accept(value);
        } else {
            emptyAction.run();
        }
    }

    /**
     * If a value is present, and the value matches the given predicate,
     * returns a Maybe describing the value, otherwise returns an empty Maybe.
     */
    public Maybe<T> filter(Predicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        if (isEmpty()) {
            return this;
        } else {
            return predicate.test(value) ? this : empty();
        }
    }

    /**
     * If a value is present, returns a Maybe describing the result of applying
     * the given mapping function to the value, otherwise returns an empty Maybe.
     */
    public <U> Maybe<U> map(Function<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            return Maybe.ofNullable(mapper.apply(value));
        }
    }

    /**
     * If a value is present, returns the result of applying the given
     * Maybe-bearing mapping function to the value, otherwise returns an empty Maybe.
     */
    public <U> Maybe<U> flatMap(Function<? super T, ? extends Maybe<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (isEmpty()) {
            return empty();
        } else {
            @SuppressWarnings("unchecked")
            Maybe<U> r = (Maybe<U>) mapper.apply(value);
            return Objects.requireNonNull(r);
        }
    }

    /**
     * If a value is present, returns a Maybe describing the value, otherwise
     * returns a Maybe produced by the supplying function.
     */
    public Maybe<T> or(Supplier<? extends Maybe<? extends T>> supplier) {
        Objects.requireNonNull(supplier);
        if (isPresent()) {
            return this;
        } else {
            @SuppressWarnings("unchecked")
            Maybe<T> r = (Maybe<T>) supplier.get();
            return Objects.requireNonNull(r);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Maybe)) {
            return false;
        }

        Maybe<?> other = (Maybe<?>) obj;
        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }

    @Override
    public String toString() {
        return value != null
            ? String.format("Maybe[%s]", value)
            : "Maybe.empty";
    }
}
