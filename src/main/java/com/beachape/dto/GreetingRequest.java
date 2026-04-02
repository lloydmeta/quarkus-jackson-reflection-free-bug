package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/// Simple request for testing unknown field rejection (Bug B).
public record GreetingRequest(
        @JsonProperty("name") String name) implements ApiModel {}
