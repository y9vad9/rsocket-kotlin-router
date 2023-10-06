![GitHub release](https://img.shields.io/github/v/release/y9vad9/rsocket-kotlin-router) ![GitHub](https://img.shields.io/github/license/y9vad9/rsocket-kotlin-router)
# RSocket Router

`rsocket-kotlin-router` is a customisable library designed to streamline and simplify routing
for RSocket Kotlin server applications. This library offers a typesafe DSL for handling various
routes, serving as a declarative simplified alternative to manual routing that would
otherwise result in long-winded ternary logic or exhaustive when statements.

Library provides the following features:
- [Routing Builder](#how-to-use)
- [Interceptors](#Interceptors)
- [Request Versioning](router-versioning)
- [Request Serialization](router-serialization)

## How to use
First of all, you need to implement basic artifacts with routing support. For now, `rsocket-kotlin-router`
is available only at my self-hosted maven:
```kotlin
repositories {
    maven("https://maven.y9vad9.com")
}

dependencies {
    implementation("com.y9vad9.rsocket.router:router-core:$version")
}
```
> For now, it's available for JVM only, but as there is no JVM platform API used,
> new targets will be available [upon your request](https://github.com/y9vad9/rsocket-kotlin-router/issues/new).

Example of defining RSocket router:
```kotlin
val serverRouter = router {
    routeSeparator = '.'
    routeProvider { metadata: ByteReadPacket? -> 
        metadata?.read(RoutingMetadata)?.tags?.first() 
            ?: throw RSocketError.Invalid("No routing metadata was provided")
    }
        
    routing { // this: RoutingBuilder
        route("authorization") {
            requestResponse("register") { payload: Payload ->
                // just 4 example
                println(payload.data.readText())
                Payload.Empty
            }
        }
    }
}
```

See also what else is supported:

<details id="Interceptors">
  <summary>Interceptors</summary>
<i>Interceptors are experimental feature: API can be changed in the future.</i>

<b id="Preprocessors">Preprocessors</b>

Preprocessors are utilities that run before routing feature applies. For cases, when you need to transform input into something or propagate
values using coroutines – you can extend [`Preprocessor.Modifier`](https://github.com/y9vad9/rsocket-kotlin-router/blob/2a794e9a8c5d2ac53cb87ea58cfbe4a2ecfa217d/router-core/src/commonMain/kotlin/com.y9vad9.rsocket.router/interceptors/Interceptor.kt#L39) or [`Preprocessor.CoroutineContext`](https://github.com/y9vad9/rsocket-kotlin-router/blob/master/router-core/src/commonMain/kotlin/com.y9vad9.rsocket.router/interceptors/Interceptor.kt#L31). Here's an example:
```kotlin
class MyCoroutineContextElement(val value: String): CoroutineContext.Element {...}

@OptIn(ExperimentalInterceptorsApi::class)
class MyCoroutineContextPreprocessor : Preprocessor.CoroutineContext {
    override fun intercept(coroutineContext: CoroutineContext, input: Payload): CoroutineContext {
        return coroutineContext + MyCoroutineContextElement(value = "smth")
    }
}
```

<b id="RouteInterceptors">Route Interceptors</b>

In addition to the `Preprocessors`, `rsocket-kotlin-router` also provides API to intercept specific routes:
```kotlin
@OptIn(ExperimentalInterceptorsApi::class)
class MyRouteInterceptor : RouteInterceptor.Modifier {
    override fun intercept(route: String, input: Payload): Payload {
        return Payload.Empty // just for example
    }
}
```

<b>Installation</b>
```kotlin
val serverRouter = router {
    preprocessors {
        forCoroutineContext(MyCoroutineContextPreprocessor())
    }
    
    sharedInterceptors {
        forModification(MyRouteInterceptor())
    }
}
```
</details>

<details>
  <summary>Versioning support</summary>

To use request versioning in your project, use the following artifact:

```kotlin
dependencies {
    // ...
    implementation("com.y9vad9.rsocket.router:router-versioning-core:$version")
}
```
For details, please refer to the [versioning guide](router-versioning/README.md).
</details>

<details>
  <summary>Serialization support</summary>

To make type-safe requests with serialization/deserialization mechanisms, implement the following:

```kotlin
dependencies {
    implementation("com.y9vad9.rsocket.router:router-serialization-core:$version")
    // for JSON support
    implementation("com.y9vad9.rsocket.router:router-serialization-json:$version")
}
```
For details, please refer to the [serialization guide](router-serialization/README.md).
</details>

<details>
  <summary>Testing</summary>

`rsocket-kotlin-router` provides ability to test your routes with `router-test` artifact:

```kotlin
dependencies {
    implementation("com.y9vad9.rsocket.router:router-test:$version")
}
```

```kotlin
@Test
fun testRoutes() {
    runBlocking {
        val route1 = router.routeAtOrAssert("test")
        val route2 = router.routeAtOrAssert("test.subroute")

        route1.assertHasInterceptor<MyInterceptor>()
        route2.assertHasInterceptor<MyInterceptor>()

        route2.fireAndForgetOrAssert(buildPayload {
            data("test")
        })
    }
}
```

You can refer to the [example](router-core/test/src/jvmTest/kotlin/com/y9vad9/rsocket/router/test/RouterTest.kt) for more details.
</details>

## Bugs and Feedback
For bugs, questions and discussions please use the [GitHub Issues](https://github.com/y9vad9/rsocket-kotlin-router/issues).

## License
This library is licensed under [MIT License](LICENSE). Feel free to use, modify, and distribute it for any purpose.
