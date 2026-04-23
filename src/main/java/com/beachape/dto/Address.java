package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Address(@JsonProperty("city") String city, @JsonProperty("country") String country)
    implements ApiModel {}
