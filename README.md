# quarkus-jackson-reflection-free-bug

Reproducer for bugs in Quarkus's build-time generated reflection-free Jackson (de)serialisers when `quarkus.rest.jackson.optimization.enable-reflection-free-serializers=true`.

This became the default in Quarkus 3.35 ([PR #53161](https://github.com/quarkusio/quarkus/pull/53161)).

## Status of previous issues

* [#53408](https://github.com/quarkusio/quarkus/issues/53408) (collection/map type bugs) - fixed in 3.34.3 via [#53414](https://github.com/quarkusio/quarkus/pull/53414)
* [#53588](https://github.com/quarkusio/quarkus/issues/53588) (naming strategy, `Optional<T>`, null defaults, `@JsonAnySetter`) - fixed in 3.34.6

The repo has been bumped to Quarkus 3.34.6 (BOM) / 3.35.0 (plugin). All previously-failing tests now pass.

## Current bugs

### 1. `@JsonTypeInfo` discriminator missing

The generated serialiser for concrete subtypes of a `@JsonTypeInfo`-annotated sealed interface doesn't write the type discriminator property. Clients expecting standard Jackson polymorphic format can't deserialise the response.

```java
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({ @JsonSubTypes.Type(value = Item.TypeA.class, name = "type_a") })
public sealed interface Item permits Item.TypeA {
    record TypeA(String value) implements Item {}
}
```

* Expected: `{"item": {"type": "type_a", "value": "hello"}}`
* Actual: `{"item": {"value": "hello"}}` - discriminator absent

### 2. `@JsonUnwrapped` broken when inner type has a generated serialiser

Quarkus detects `@JsonUnwrapped` on the containing type and skips serialiser generation for it (falling back to reflection). But if the **inner** (unwrapped) type has a generated `$quarkusjacksonserializer` - because some other endpoint returns that type directly - `@JsonUnwrapped` breaks and fields are nested instead of flattened. The exact mechanism isn't fully traced, but the presence of the generated serialiser for the inner type is the determining factor.

The `/detail` and `/error-info` endpoints exist solely to trigger serialiser generation for `Detail` and `ErrorInfo`. Without them, `@JsonUnwrapped` works fine because the inner types use reflection-based serialisation.

## Reproducing

```bash
./gradlew clean test
```

25 tests total. 3 serialiser tests fail, the rest pass:

| Test | Status | What it checks |
|------|--------|---------------|
| `unwrapped_simple_shouldFlattenFields` | PASS | `@JsonUnwrapped` works when inner type has no generated serialiser |
| `polymorphicItem_shouldIncludeTypeDiscriminator` | **FAIL** | Bug 1: discriminator missing |
| `unwrapped_successResult_shouldFlattenFieldsWithDiscriminator` | **FAIL** | Bug 1 + 2: discriminator missing, fields nested |
| `unwrapped_failedResult_shouldFlattenFieldsWithDiscriminator` | **FAIL** | Bug 1 + 2: discriminator missing, fields nested |

## Requirements

* Java 25
* Quarkus 3.34.6
