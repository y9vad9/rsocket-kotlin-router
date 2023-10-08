# Route Versioning

When a product grows and evolves, dealing with backward and forward compatibility becomes essential. For these purposes, the library provides necessary wrappers around `router-core` with versioning support.

> **Warning**
> This feature is experimental; migration steps might be required in the future.

## How It Works

Every type of request with the `router-versioning-core` artifact now has its extension with a DSL builder for versioning:

```kotlin
val router = {
    routeProvider { /*...*/ }
    versioning { coroutineContext, payload -> Version(/* ... */) }

    routing {
        route("authorization") {
            requestResponseV("register") {
                // available from version 1 up to 2
                version(1) { payload ->
                    TODO()
                }

                // available from version 2 and onwards
                version(2) { payload ->
                    TODO()
                }
            }
        }
    }
}
```
> **Note** <br>
> As for semantic versioning, you can also specify minor version for each new request within one major release if
> you need using `version(version: Version, block: suspend (T) -> R)`.

## Implementation
To implement this feature, add it to your dependencies as follows:
```kotlin
dependencies {
    implementation("com.y9vad9.rsocket.router:router-versioning-core:$version")
}
```

## Serialization support
To use [serialization feature](../router-serialization), implement the following dependency:
```kotlin
dependencies {
    implementation("com.y9vad9.rsocket.router:router-versioning-serialization:$version")
}
```
### Example
Here's example of how you can define type-safe requests with versioning support:
```kotlin
requestResponseV("register") {
    version<Foo, Bar>(1) { foo: Foo ->
        Bar(/* ... */)
    }
}
```
