# quarkus-jackson-reflection-free-bug

Demonstrates two deserialization bugs in Quarkus's build-time generated reflection-free Jackson deserializers (`$quarkusjacksondeserializer` classes) when `quarkus.rest.jackson.optimization.enable-reflection-free-serializers=true`.

This will become the default in Quarkus 3.35 ([PR #53161](https://github.com/quarkusio/quarkus/pull/53161)).

## The bugs

### Bug A: Collection types other than `List`/`Set`/`Collection`/`Map` lose generic type parameters

When a REST endpoint accepts a request body containing a collection field whose declared type isn't one of the exact classes `java.util.List`, `java.util.Set`, `java.util.Collection`, `java.lang.Iterable`, or `java.util.Map`, the generated deserializer loses the generic type parameter. This affects:

* **Standard JDK types**: `LinkedList<T>`, `LinkedHashSet<T>`, `ArrayDeque<T>`, etc.
* **Guava types**: `ImmutableList<T>`, `ImmutableSet<T>`, etc.
* Any other collection implementation

Elements are deserialised as `LinkedHashMap` (for object types) or `String` (for `@JsonValue` wrapper types) instead of the expected typed objects, causing `ClassCastException` at runtime.

The same payloads work correctly when the field is declared as `List<T>` or `Set<T>`.

**Root cause**: [`JacksonCodeGenerator.registerTypeToBeGenerated()`](https://github.com/quarkusio/quarkus/blob/main/extensions/resteasy-reactive/rest-jackson/deployment/src/main/java/io/quarkus/resteasy/reactive/jackson/deployment/processor/JacksonCodeGenerator.java) only recognises a hardcoded set of exact class names. Everything else falls through to `FieldKind.OBJECT`, and the generated code calls `context.readTreeAsValue(jsonNode, RawCollectionType.class)` without any generic type info.

**Symptom**:
```
java.lang.ClassCastException: class java.util.LinkedHashMap cannot be cast to class com.beachape.dto.Item
```
```
java.lang.ClassCastException: class java.lang.String cannot be cast to class com.beachape.dto.Token
```

### Bug B: Unknown JSON fields silently ignored

The generated deserializer ignores `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES`. When a request body contains unrecognised fields, Jackson's standard deserializer rejects them with a 400 error. The generated deserializer silently ignores them and returns 200.

**Root cause**: [`JacksonDeserializerFactory.deserializeObjectFields()`](https://github.com/quarkusio/quarkus/blob/main/extensions/resteasy-reactive/rest-jackson/deployment/src/main/java/io/quarkus/resteasy/reactive/jackson/deployment/processor/JacksonDeserializerFactory.java) iterates JSON fields via a `StringSwitch`. When a field name doesn't match any known property, it falls through silently - there's no default case that checks `FAIL_ON_UNKNOWN_PROPERTIES` and throws.

## Reproducing

```bash
./gradlew clean test
```

All 7 tests pass when the reflection-free serializers are disabled (comment out or remove the `enable-reflection-free-serializers` line in `application.properties`).

### Expected results

All 7 tests should pass. In practice, the 2 control cases pass and the 5 bug cases fail:

| Test | Collection type | Bug | Expected | Actual |
|---|---|---|---|---|
| `batch_stdList_shouldDeserializePolymorphicItems` | `List<Item>` | (control) | 200 | 200 |
| `invalidate_stdSet_shouldDeserializeJsonValueWrappers` | `Set<Token>` | (control) | 200 | 200 |
| `batch_linkedList_shouldDeserializePolymorphicItems` | `LinkedList<Item>` | Bug A | 200 | 500 |
| `invalidate_linkedHashSet_shouldDeserializeJsonValueWrappers` | `LinkedHashSet<Token>` | Bug A | 200 | 500 |
| `batch_guavaImmutableList_shouldDeserializePolymorphicItems` | `ImmutableList<Item>` | Bug A | 200 | 500 |
| `invalidate_guavaImmutableSet_shouldDeserializeJsonValueWrappers` | `ImmutableSet<Token>` | Bug A | 200 | 500 |
| `greeting_shouldRejectUnknownFields` | N/A | Bug B | 400 | 200 |

Bug A is reproducible with pure JDK types (`LinkedList`, `LinkedHashSet`) - no external dependencies required. The Guava cases are included because they're common in real-world applications.

## Requirements

* Java 25
* Quarkus 3.34.1
