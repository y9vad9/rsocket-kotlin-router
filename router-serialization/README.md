# Requests Serialization

Often, we have a type-safe contract specifying what we accept and what we return in requests. Serializing this data can be challenging, especially when dealing with different formats or migrating to a new one. Even if it's not the case, defining your own wrappers or extensions from scratch takes time and, as mentioned before, can lead to potential problems in the future. That's why `rsocket-kotlin-router` provides a ready-to-use pragmatic serialization system.

> **Warning** <br>
> This feature is experimental, and migration steps might be required in the future.

## How to Use

### Implementation

First, add the necessary dependencies:

```kotlin
dependencies {
    implementation("com.y9vad9.rsocket.router:router-serialization-core:$version")
    // for JSON support
    implementation("com.y9vad9.rsocket.router:router-serialization-json:$version")
}
```
### Installation
To add serialization to your requests, install the required ContentSerializer in your router. For example, using JsonContentSerializer:
```kotlin
val router = router {
    // ...
    serialization { JsonContentSerializer() }
    // ...
}
```
### Usage
You can use the bundled extensions as follows:
```kotlin
routing {
    route("authorization") {
        requestResponse<Foo, Bar>("register") { foo: Foo ->
            return@requestResponse Bar(/* ... */)
        }
        // other types of the requests have the same extensions
    }
}
```
### Custom formats
To add support for other existing formats, you can simply extend `ContentSerializer`. You can take a look at
[`JsonContentSerializer`](json/src/commonMain/kotlin/com/y9vad9/rsocket/router/serialization/json/JsonContentSerializer.kt) 
as an example.