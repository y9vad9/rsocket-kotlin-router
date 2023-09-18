![GitHub release (with filter)](https://img.shields.io/github/v/release/y9vad9/rsocket-kotlin-router)
![GitHub](https://img.shields.io/github/license/y9vad9/rsocket-kotlin-router)
# rsocket-kotlin-router
`rsocket-kotlin-router` is a customizable library designed to streamline and simplify routing
for RSocket Kotlin server applications. This library offers a typesafe DSL for handling various
routes, serving as a declarative simplified alternative to manual routing that would
otherwise, result in long-winded ternary logic or exhaustive when statements.

## Features
### Router
It's the basic thing in the `rsocket-kotlin-router` that's responsible for managing routes, their settings, etc. You
can define it in the following way:
```kotlin
val ServerRouter = router {
    router {
        routeSeparator = '.'
        routeProvider { metadata -> 
            metadata?.read(RoutingMetadata)?.tags?.first() 
                ?: throw RSocketError.Invalid("No routing metadata was provided")
        }
        
        routing { // this: RoutingBuilder
            // ...
        }
    }
}
```
To install it later, you can use `Router.installOn(RSocketRequestHandlerBuilder)` function:
```kotlin
fun ServerRequestHandler(router: Router): RSocket = RSocketRequestHandlerBuilder {
    router.installOn(this)
}
```
Or you can call `router` function directly in the `RSocketRequestHandlerBuilder` context – it will automatically
install router on the given context.

### Routing Builder
You can define routes using bundled DSL-Builder functions:
```kotlin
fun RoutingBuilder.usersRoute(): Unit = route("users") {
    // extension function that wraps RSocket `requestResponse` into `route` with given path.
    requestResponse("get") { payload -> TODO() }

    // ... other
}
```
> **Note** <br>
> The library does not include the functionality to add routing to a `metadataPush` type of request. I am not sure
> how it should be exactly implemented (API), so your ideas are welcome. For now, I consider it a per-project responsibility.
### Interceptors
> **Warning** <br>
> Interceptors are experimental feature: API can be changed in the future.

#### Preprocessors
Preprocessors are utilities that run before the routing feature applies. For cases, when you need to transform input into something or propagate
values using coroutines – you can extend [`Preprocessor.Modifier`](https://github.com/y9vad9/rsocket-kotlin-router/blob/8bace098e0a47e3cf514eec0dfb702f7e4e13591/router-core/src/commonMain/kotlin/com.y9vad9.rsocket.router/interceptors/Interceptor.kt#L35) or [`Preprocessor.CoroutineContext`](https://github.com/y9vad9/rsocket-kotlin-router/blob/8bace098e0a47e3cf514eec0dfb702f7e4e13591/router-core/src/commonMain/kotlin/com.y9vad9.rsocket.router/interceptors/Interceptor.kt#L27). Here's an example:
```kotlin
class MyCoroutineContextElement(val value: String): CoroutineContext.Element {...}

@OptIn(ExperimentalInterceptorsApi::class)
class MyCoroutineContextPreprocessor : Preprocessor.CoroutineContext {
    override fun intercept(coroutineContext: CoroutineContext, input: Payload): CoroutineContext {
        return coroutineContext + MyCoroutineContextElement(value = "smth")
    }
}
```

#### Route Interceptors
In addition to the `Preprocessors`, `rsocket-kotlin-router` also provides API to intercept specific routes:
```kotlin
@OptIn(ExperimentalInterceptorsApi::class)
class MyRouteInterceptor : RouteInterceptor.Modifier {
    override fun intercept(route: String, input: Payload): Payload {
        return Payload.Empty // just for example
    }
}
```
It has the same abilities as Preprocessors. You can take a look at it [here](https://github.com/y9vad9/rsocket-kotlin-router/blob/8bace098e0a47e3cf514eec0dfb702f7e4e13591/router-core/src/commonMain/kotlin/com.y9vad9.rsocket.router/interceptors/Interceptor.kt#L45).
### Testability
`rsocket-kotlin-router` provides ability to test your routes with `router-test` artifact:
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
You can refer to [full example](router-test/src/jvmTest/kotlin/com/y9vad9/rsocket/router/test/RouterTest.kt).

## Implementation
To implement this library, you should define the following:
```kotlin
repositories {
    maven("https://maven.y9vad9.com")
}

dependencies {
    implementation("com.y9vad9.rsocket.router:router-core:$version")
    // for testing
    implementation("com.y9vad9.rsocket.router:router-test:$version")
}
```
> For now, it's available for JVM only, but as there is no JVM platform API used,
> new targets will be available [upon your request](https://github.com/y9vad9/rsocket-kotlin-router/issues/new).
