package com.beachape.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.LinkedList;

/// Request using `LinkedList` - a standard JDK collection not in the hardcoded list.
public record LinkedListBatchRequest(
        @JsonProperty("items") LinkedList<Item> items) implements ApiModel {}
