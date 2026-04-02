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

**Note**: Bug 1 is reproducible with pure JDK types (`LinkedList`, `LinkedHashSet`) - no external dependencies required. The Guava cases are included because they're common in real-world applications.

## Requirements

* Java 25
* Quarkus 3.34.1
