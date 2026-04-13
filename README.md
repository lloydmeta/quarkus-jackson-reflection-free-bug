# quarkus-jackson-reflection-free-bug

Reproducer for a deserialization bug in Quarkus's build-time generated reflection-free Jackson deserialisers (`$quarkusjacksondeserializer` classes) when `quarkus.rest.jackson.optimization.enable-reflection-free-serializers=true`.

This will become the default in Quarkus 3.35 ([PR #53161](https://github.com/quarkusio/quarkus/pull/53161)).

## Background

The [original issue (#53408)](https://github.com/quarkusio/quarkus/issues/53408) reported two bugs. [PR #53414](https://github.com/quarkusio/quarkus/pull/53414) (included in 3.34.3) fixed:

* `FAIL_ON_UNKNOWN_PROPERTIES` being silently ignored
* Concrete non-standard JDK collections (`LinkedList`, `LinkedHashSet`) losing generic type parameters

The remaining bug affects **abstract or interface collection/map types** - both JDK (`SortedSet`, `SortedMap`, `Deque`) and third-party (Guava `ImmutableSet`, `ImmutableList`).

## The bug

The generated deserialiser's `concreteCollectionType()` method falls back to `HashSet`/`ArrayList`/`HashMap` when the declared type is abstract or an interface. These fallback types aren't assignable to the declared type, causing `ClassCastException` at runtime. It also means Jackson module-provided deserialisers (e.g. `jackson-datatype-guava`) are bypassed entirely.

## Reproducing

```bash
./gradlew clean test
```

10 tests, 5 pass, 5 fail:

| Test | Collection type | Error |
|------|----------------|-------|
| `invalidate_sortedSet` | `SortedSet<Token>` | `HashSet cannot be cast to SortedSet` |
| `batch_deque` | `Deque<Item>` | `ArrayList cannot be cast to Deque` |
| `batch_sortedMap` | `SortedMap<String, Item>` | `HashMap cannot be cast to SortedMap` |
| `invalidate_guavaImmutableSet` | `ImmutableSet<Token>` | `HashSet cannot be cast to ImmutableSet` |
| `batch_guavaImmutableList` | `ImmutableList<Item>` | `ArrayList cannot be cast to ImmutableList` |

The `SortedSet`, `Deque`, and `SortedMap` cases are pure JDK - no external dependencies required. The Guava cases are included because they're common in real-world applications.

## Requirements

* Java 25
* Quarkus 3.34.3
