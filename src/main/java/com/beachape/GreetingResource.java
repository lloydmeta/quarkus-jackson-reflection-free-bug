package com.beachape;

import com.beachape.dto.BatchRequest;
import com.beachape.dto.DequeBatchRequest;
import com.beachape.dto.GreetingRequest;
import com.beachape.dto.GuavaBatchRequest;
import com.beachape.dto.GuavaInvalidateRequest;
import com.beachape.dto.InvalidateRequest;
import com.beachape.dto.Item;
import com.beachape.dto.LinkedHashSetInvalidateRequest;
import com.beachape.dto.LinkedListBatchRequest;
import com.beachape.dto.SortedMapRequest;
import com.beachape.dto.SortedSetInvalidateRequest;
import com.beachape.dto.Token;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.stream.Collectors;

@Path("/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class GreetingResource {

    @POST
    @Path("/batch")
    public String batch(BatchRequest request) {
        String values = request.items().stream()
                .map(item -> switch (item) {
                    case Item.TypeA a -> a.value();
                })
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/guava-batch")
    public String guavaBatch(GuavaBatchRequest request) {
        String values = request.items().stream()
                .map(item -> switch (item) {
                    case Item.TypeA a -> a.value();
                })
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/invalidate")
    public String invalidate(InvalidateRequest request) {
        String values = request.tokens().stream()
                .map(Token::value)
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/guava-invalidate")
    public String guavaInvalidate(GuavaInvalidateRequest request) {
        String values = request.tokens().stream()
                .map(Token::value)
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/linkedlist-batch")
    public String linkedListBatch(LinkedListBatchRequest request) {
        String values = request.items().stream()
                .map(item -> switch (item) {
                    case Item.TypeA a -> a.value();
                })
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/linkedhashset-invalidate")
    public String linkedHashSetInvalidate(LinkedHashSetInvalidateRequest request) {
        String values = request.tokens().stream()
                .map(Token::value)
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/sortedset-invalidate")
    public String sortedSetInvalidate(SortedSetInvalidateRequest request) {
        String values = request.tokens().stream()
                .map(Token::value)
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/deque-batch")
    public String dequeBatch(DequeBatchRequest request) {
        String values = request.items().stream()
                .map(item -> switch (item) {
                    case Item.TypeA a -> a.value();
                })
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/sortedmap-batch")
    public String sortedMapBatch(SortedMapRequest request) {
        String values = request.entries().values().stream()
                .map(item -> switch (item) {
                    case Item.TypeA a -> a.value();
                })
                .collect(Collectors.joining(","));
        return "{\"values\":\"" + values + "\"}";
    }

    @POST
    @Path("/greeting")
    public String greeting(GreetingRequest request) {
        return "{\"message\":\"Hello " + request.name() + "\"}";
    }
}
