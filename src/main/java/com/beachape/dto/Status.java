package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/// Enum with `@JsonValue` / `@JsonCreator` for custom string mapping.
public enum Status {
    ACTIVE("active"),
    INACTIVE("inactive");

    private final String wire;

    Status(String wire) {
        this.wire = wire;
    }

    @JsonValue
    public String toWire() {
        return wire;
    }

    @JsonCreator
    public static Status fromWire(String wire) {
        for (Status s : values()) {
            if (s.wire.equals(wire)) return s;
        }
        throw new IllegalArgumentException("Unknown status: " + wire);
    }
}
