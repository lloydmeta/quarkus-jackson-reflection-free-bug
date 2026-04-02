# quarkus-jackson-reflection-free-bug

Demonstrates two deserialization bugs in Quarkus's build-time generated reflection-free Jackson deserialisers (`$quarkusjacksondeserializer` classes) when `quarkus.rest.jackson.optimization.enable-reflection-free-serializers=true`.

This will become the default in Quarkus 3.35 ([PR #53161](https://github.com/quarkusio/quarkus/pull/53161)).

## The bugs

1. Collection types other than `List`/`Set`/`Collection`/`Map` lose generic type parameters

   The same payloads work correctly when the field is declared as `List<T>` or `Set<T>`.

2. Unknown JSON fields silently ignored

   The generated deserialiser ignores `DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES`

## Reproducing

```bash
./gradlew clean test
```

All 7 tests pass when the reflection-free serialisers are disabled (comment out or remove the `enable-reflection-free-serializers` line in `application.properties`).

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
