package com.beachape;

import com.beachape.dto.AnnotationNamingRequest;
import com.beachape.dto.AnySetterRequest;
import com.beachape.dto.BatchRequest;
import com.beachape.dto.DefaultValueRequest;
import com.beachape.dto.DequeBatchRequest;
import com.beachape.dto.EnumRequest;
import com.beachape.dto.GreetingRequest;
import com.beachape.dto.GuavaBatchRequest;
import com.beachape.dto.GuavaInvalidateRequest;
import com.beachape.dto.InstantRequest;
import com.beachape.dto.InvalidateRequest;
import com.beachape.dto.Item;
import com.beachape.dto.LinkedHashSetInvalidateRequest;
import com.beachape.dto.LinkedListBatchRequest;
import com.beachape.dto.MapperSnakeCaseRequest;
import com.beachape.dto.OptionalItemRequest;
import com.beachape.dto.OptionalStringRequest;
import com.beachape.dto.SortedMapRequest;
import com.beachape.dto.SortedSetInvalidateRequest;
import com.beachape.dto.Token;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.Instant;
import java.util.Optional;
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

    // -- Naming strategy tests --

    @POST
    @Path("/mapper-snake-case")
    public String mapperSnakeCase(MapperSnakeCaseRequest request) {
        return "{\"values\":\"" + request.firstName() + "\"}";
    }

    @GET
    @Path("/mapper-snake-case-ser")
    public MapperSnakeCaseRequest mapperSnakeCaseSer() {
        return new MapperSnakeCaseRequest("Alice");
    }

    @POST
    @Path("/annotation-naming")
    public String annotationNaming(AnnotationNamingRequest request) {
        return "{\"values\":\"" + request.firstName() + "\"}";
    }

    @GET
    @Path("/annotation-naming-ser")
    public AnnotationNamingRequest annotationNamingSer() {
        return new AnnotationNamingRequest("Bob");
    }

    // -- Optional tests --

    @POST
    @Path("/optional-item")
    public String optionalItem(OptionalItemRequest request) {
        String value = request.item()
                .map(item -> switch (item) {
                    case Item.TypeA a -> a.value();
                })
                .orElse("empty");
        return "{\"values\":\"" + value + "\"}";
    }

    @POST
    @Path("/optional-string")
    public String optionalString(OptionalStringRequest request) {
        if (request.label() == null) {
            return "{\"state\":\"java_null\"}";
        }
        return request.label()
                .map(v -> "{\"state\":\"present\",\"value\":\"" + v + "\"}")
                .orElse("{\"state\":\"empty\"}");
    }

    @GET
    @Path("/optional-absent-ser")
    public OptionalStringRequest optionalAbsentSer() {
        return new OptionalStringRequest(Optional.empty());
    }

    // -- @JsonAnySetter test --

    @POST
    @Path("/any-setter")
    public String anySetter(AnySetterRequest request) {
        return "{\"known\":\"" + request.getKnown() + "\",\"extras_size\":" + request.getExtras().size() + "}";
    }

    // -- Null default test --

    @POST
    @Path("/default-value")
    public String defaultValue(DefaultValueRequest request) {
        return "{\"label\":" + (request.getLabel() == null ? "null" : "\"" + request.getLabel() + "\"") + "}";
    }

    // -- Instant test --

    @POST
    @Path("/instant")
    public String instant(InstantRequest request) {
        return "{\"values\":\"" + request.timestamp().toString() + "\"}";
    }

    // -- Enum test --

    @POST
    @Path("/enum-status")
    public String enumStatus(EnumRequest request) {
        return "{\"values\":\"" + request.status().toWire() + "\"}";
    }

    // -- Existing --

    @POST
    @Path("/greeting")
    public String greeting(GreetingRequest request) {
        return "{\"message\":\"Hello " + request.name() + "\"}";
    }
}
