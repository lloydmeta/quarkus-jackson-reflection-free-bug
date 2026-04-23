package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ErrorInfo(@JsonProperty("code") String code, @JsonProperty("message") String message)
    implements ApiModel {}
