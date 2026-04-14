package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/// Tests that explicit JSON `null` overrides a non-null default.
/// The codegen's `isNull()` check skips the field via `continue`, leaving the default intact.
public class DefaultValueRequest implements ApiModel {
    @JsonProperty("label")
    private String label = "default";

    public DefaultValueRequest() {}

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
