package com.beachape;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.hasKey;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

@QuarkusTest
class ReflectionFreeDeserializerTest {

    // -- Control cases: standard lib collections (should pass) --

    @Test
    void batch_stdList_shouldDeserializePolymorphicItems() {
        given()
                .contentType("application/json")
                .body("""
                        {"items": [{"type": "type_a", "value": "hello"}]}
                        """)
                .when()
                .post("/batch")
                .then()
                .statusCode(200)
                .body("values", is("hello"));
    }

    @Test
    void invalidate_stdSet_shouldDeserializeJsonValueWrappers() {
        given()
                .contentType("application/json")
                .body("""
                        {"tokens": ["tok_abc"]}
                        """)
                .when()
                .post("/invalidate")
                .then()
                .statusCode(200)
                .body("values", is("tok_abc"));
    }

    // -- Bug A: non-standard collections lose generic type parameters --
    // The generated deserializer only recognises java.util.List, java.util.Set, etc.
    // Any other collection type (even JDK ones like LinkedList) falls through to
    // FieldKind.OBJECT, losing generic type info. Elements are deserialized as
    // LinkedHashMap / String instead of the proper types.

    // Standard JDK collections not in the hardcoded list (no external deps needed):

    @Test
    void batch_linkedList_shouldDeserializePolymorphicItems() {
        given()
                .contentType("application/json")
                .body("""
                        {"items": [{"type": "type_a", "value": "hello"}]}
                        """)
                .when()
                .post("/linkedlist-batch")
                .then()
                .statusCode(200)
                .body("values", is("hello"));
    }

    @Test
    void invalidate_linkedHashSet_shouldDeserializeJsonValueWrappers() {
        given()
                .contentType("application/json")
                .body("""
                        {"tokens": ["tok_abc"]}
                        """)
                .when()
                .post("/linkedhashset-invalidate")
                .then()
                .statusCode(200)
                .body("values", is("tok_abc"));
    }

    // Abstract/interface JDK collection types (concreteCollectionType falls back to
    // HashSet/ArrayList, which aren't assignable to the declared type):

    @Test
    void invalidate_sortedSet_shouldDeserializeJsonValueWrappers() {
        given()
                .contentType("application/json")
                .body("""
                        {"tokens": ["tok_abc"]}
                        """)
                .when()
                .post("/sortedset-invalidate")
                .then()
                .statusCode(200)
                .body("values", is("tok_abc"));
    }

    @Test
    void batch_deque_shouldDeserializePolymorphicItems() {
        given()
                .contentType("application/json")
                .body("""
                        {"items": [{"type": "type_a", "value": "hello"}]}
                        """)
                .when()
                .post("/deque-batch")
                .then()
                .statusCode(200)
                .body("values", is("hello"));
    }

    @Test
    void batch_sortedMap_shouldDeserializePolymorphicItems() {
        given()
                .contentType("application/json")
                .body("""
                        {"entries": {"a": {"type": "type_a", "value": "hello"}}}
                        """)
                .when()
                .post("/sortedmap-batch")
                .then()
                .statusCode(200)
                .body("values", is("hello"));
    }

    // Guava collections (same bug, common real-world case):

    @Test
    void batch_guavaImmutableList_shouldDeserializePolymorphicItems() {
        given()
                .contentType("application/json")
                .body("""
                        {"items": [{"type": "type_a", "value": "hello"}]}
                        """)
                .when()
                .post("/guava-batch")
                .then()
                .statusCode(200)
                .body("values", is("hello"));
    }

    @Test
    void invalidate_guavaImmutableSet_shouldDeserializeJsonValueWrappers() {
        given()
                .contentType("application/json")
                .body("""
                        {"tokens": ["tok_abc"]}
                        """)
                .when()
                .post("/guava-invalidate")
                .then()
                .statusCode(200)
                .body("values", is("tok_abc"));
    }

    // -- Bug B: Unknown fields silently ignored (fixed in 3.34.3) --

    @Test
    void greeting_shouldRejectUnknownFields() {
        given()
                .contentType("application/json")
                .body("""
                        {"name": "world", "evil": "data"}
                        """)
                .when()
                .post("/greeting")
                .then()
                .statusCode(400);
    }

    // -- Bug C: PropertyNamingStrategy ignored --
    // The codegen derives field names at build time from raw Java names.
    // Neither ObjectMapper-level setPropertyNamingStrategy() nor class-level
    // @JsonNaming annotations are consulted.

    @Test
    void naming_mapperSnakeCase_shouldDeserialise() {
        given()
                .contentType("application/json")
                .body("""
                        {"first_name": "Alice"}
                        """)
                .when()
                .post("/mapper-snake-case")
                .then()
                .statusCode(200)
                .body("values", is("Alice"));
    }

    @Test
    void naming_mapperSnakeCase_shouldSerialise() {
        given()
                .when()
                .get("/mapper-snake-case-ser")
                .then()
                .statusCode(200)
                .body("first_name", is("Alice"));
    }

    @Test
    void naming_annotationUpperSnake_shouldDeserialise() {
        given()
                .contentType("application/json")
                .body("""
                        {"FIRST_NAME": "Bob"}
                        """)
                .when()
                .post("/annotation-naming")
                .then()
                .statusCode(200)
                .body("values", is("Bob"));
    }

    @Test
    void naming_annotationUpperSnake_shouldSerialise() {
        given()
                .when()
                .get("/annotation-naming-ser")
                .then()
                .statusCode(200)
                .body("FIRST_NAME", is("Bob"));
    }

    // -- Bug D: Optional<T> generic type loss --
    // java.util.Optional is vetoed by vetoedClassName(). Falls through to
    // FieldKind.OBJECT with readTreeAsValue(jsonNode, Optional.class), losing
    // the generic type parameter.

    @Test
    void optional_shouldPreserveGenericType() {
        given()
                .contentType("application/json")
                .body("""
                        {"item": {"type": "type_a", "value": "hello"}}
                        """)
                .when()
                .post("/optional-item")
                .then()
                .statusCode(200)
                .body("values", is("hello"));
    }

    // -- Bug E: Nulls.AS_EMPTY config override bypassed --
    // The codegen's isNull() check skips the field entirely. The ObjectMapper's
    // configOverride(Optional.class, Nulls.AS_EMPTY) is never consulted.

    @Test
    void optional_explicitNull_shouldBeEmpty() {
        given()
                .contentType("application/json")
                .body("""
                        {"label": null}
                        """)
                .when()
                .post("/optional-string")
                .then()
                .statusCode(200)
                .body("state", is("empty"));
    }

    // -- NON_ABSENT serialisation --
    // Verify Optional.empty() is omitted from serialised output.

    @Test
    void optional_empty_shouldBeOmittedFromResponse() {
        given()
                .when()
                .get("/optional-absent-ser")
                .then()
                .statusCode(200)
                .body("$", not(hasKey("label")));
    }

    // -- Bug F: @JsonAnySetter safety valve bypass --
    // The common 2-param method pattern is invisible to isSetterMethod().

    @Test
    void anySetter_shouldCaptureUnknownFields() {
        given()
                .contentType("application/json")
                .body("""
                        {"known": "x", "extra1": "y", "extra2": "z"}
                        """)
                .when()
                .post("/any-setter")
                .then()
                .statusCode(200)
                .body("known", is("x"))
                .body("extras_size", is(2));
    }

    // -- Bug G: Explicit null can't override non-null default --
    // The codegen's isNull() check skips the field, leaving the default intact.

    @Test
    void nullField_shouldOverrideDefault() {
        given()
                .contentType("application/json")
                .body("""
                        {"label": null}
                        """)
                .when()
                .post("/default-value")
                .then()
                .statusCode(200)
                .body("label", is(nullValue()));
    }

    // -- Sanity checks (probably work) --

    @Test
    void instant_shouldDeserialise() {
        given()
                .contentType("application/json")
                .body("""
                        {"timestamp": "2026-01-01T00:00:00Z"}
                        """)
                .when()
                .post("/instant")
                .then()
                .statusCode(200)
                .body("values", is("2026-01-01T00:00:00Z"));
    }

    @Test
    void enum_withJsonValue_shouldDeserialise() {
        given()
                .contentType("application/json")
                .body("""
                        {"status": "active"}
                        """)
                .when()
                .post("/enum-status")
                .then()
                .statusCode(200)
                .body("values", is("active"));
    }
}
