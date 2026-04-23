package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record UnwrappedResultsResponse(@JsonProperty("results") List<UnwrappedResult> results)
    implements ApiModel {}
