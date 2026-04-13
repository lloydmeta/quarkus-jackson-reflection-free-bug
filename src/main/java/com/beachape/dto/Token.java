package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonValue;

/// A wrapper around a string value, similar to secret/opaque token types.
public record Token(@JsonValue String value) implements ApiModel, Comparable<Token> {
    @Override
    public String toString() {
        return "***";
    }

    @Override
    public int compareTo(Token other) {
        return this.value.compareTo(other.value);
    }
}
