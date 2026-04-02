package com.beachape;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

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

    // -- Bug B: Unknown fields silently ignored --
    // The generated deserializer doesn't respect FAIL_ON_UNKNOWN_PROPERTIES.

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
}
