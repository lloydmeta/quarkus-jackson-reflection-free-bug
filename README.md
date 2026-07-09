# quarkus-jackson-reflection-free-bug

Reproducer for bugs in Quarkus's build-time generated reflection-free Jackson (de)serialisers when `quarkus.rest.jackson.optimization.enable-reflection-free-serializers=true`.

Enabled by default since Quarkus 3.37 ([#54347](https://github.com/quarkusio/quarkus/pull/54347)). It was first flipped on in 3.35 ([#53161](https://github.com/quarkusio/quarkus/pull/53161)), reverted (default `false` in 3.36.3), then re-enabled for 3.37. This repo sets the flag explicitly, so it reproduces regardless of the version default.

## Status of previous issues

* [#53408](https://github.com/quarkusio/quarkus/issues/53408) (collection/map type bugs + `FAIL_ON_UNKNOWN_PROPERTIES` ignored) - fixed in 3.34.3 via [#53414](https://github.com/quarkusio/quarkus/pull/53414)
* [#53556](https://github.com/quarkusio/quarkus/issues/53556) (ClassCastException with abstract/interface collection and map types) - fixed in 3.34.5
* [#53588](https://github.com/quarkusio/quarkus/issues/53588) (naming strategy, `Optional<T>`, null defaults, `@JsonAnySetter`) - fixed in 3.34.6
* [#53765](https://github.com/quarkusio/quarkus/issues/53765) (`@JsonTypeInfo` discriminator missing, `@JsonUnwrapped` broken with a generated inner serialiser) - fixed (passes on 3.37.1)

## Current bugs

### Unknown-field rejection throws a plain `JsonMappingException`, bypassing `MismatchedInputException` mappers

Since [#53414](https://github.com/quarkusio/quarkus/pull/53414) the generated deserialiser rejects unknown fields under `fail-on-unknown-properties=true` - but it does so by throwing a **plain `com.fasterxml.jackson.databind.JsonMappingException`**, not the `UnrecognizedPropertyException` (a `MismatchedInputException`) that reflection-based Jackson throws.

A custom `ExceptionMapper<MismatchedInputException>` (a very common pattern - it's the type Quarkus' own built-in `BuiltinMismatchedInputExceptionMapper` targets) is therefore bypassed, and the request falls through to a generic `400` instead of the application's error body.

```java
@Provider
public class MismatchedInputExceptionMapper implements ExceptionMapper<MismatchedInputException> {
    @Override
    public Response toResponse(MismatchedInputException exception) {
        return Response.status(422).type("application/json")
                .entity("{\"handledBy\":\"MismatchedInputExceptionMapper\"}").build();
    }
}
```

* Expected: the mapper handles the unknown field (`422` + `{"handledBy":"MismatchedInputExceptionMapper"}`)
* Actual: mapper bypassed, generic `400`

Tracked in [#55255](https://github.com/quarkusio/quarkus/issues/55255).

## Reproducing

```bash
./gradlew clean test
```

26 tests total. 1 fails, the rest pass:

| Test | Status | What it checks |
|------|--------|---------------|
| `unknownField_shouldReachCustomMismatchedInputExceptionMapper` | **FAIL** | unknown field throws a plain `JsonMappingException`; custom `ExceptionMapper<MismatchedInputException>` bypassed (`400` instead of `422`) |
| `greeting_shouldRejectUnknownFields` | PASS | unknown field is rejected with `422` (#53408, fixed) |
| `polymorphicItem_shouldIncludeTypeDiscriminator` | PASS | #53765 (fixed) |
| `unwrapped_successResult_shouldFlattenFieldsWithDiscriminator` | PASS | #53765 (fixed) |
| `unwrapped_failedResult_shouldFlattenFieldsWithDiscriminator` | PASS | #53765 (fixed) |

Toggling `quarkus.rest.jackson.optimization.enable-reflection-free-serializers=false` makes the failing test pass: reflection-based Jackson throws `UnrecognizedPropertyException` (a `MismatchedInputException`), which the mapper handles.

## Requirements

* Java 25
* Quarkus 3.37.1
