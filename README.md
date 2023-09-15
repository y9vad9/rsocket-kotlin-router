# rsocket-kotlin-router

`rsocket-kotlin-router` is a customisable library designed to streamline and simplify routing
for RSocket Kotlin server applications. This library offers a typesafe DSL for handling various
routes, serving as a declarative simplified alternative to manual routing that would
otherwise result in long-winded ternary logic or exhaustive when statements.

## Motivation

In transitioning from gRPC to RSocket, a key challenge faced was managing requests
safely and efficiently in the absence of a built-in routing system. Although RSocket
provides experimental support for retrieving route metadata, it fails to offer bundled
logic for declaring routes. This would typically result in a convoluted and unscaleable
routing setup for larger projects. `rsocket-kotlin-router` serves as a solution to this,
providing a neat and modular approach to managing RSocket routes and not only.

## Example

```kotlin
internal fun RSocketConnectionAcceptor(
    service: YourService,
): ConnectionAcceptor {
    return ConnectionAcceptor {
        RSocketRequestHandler {
            router {
                routeSeparator = '.'
                routeProvider { metadata -> metadata?.read(RoutingMetadata)?.tags?.first() ?: error("...") }
                
                preprocessors {
                    // executes before routing feature
                    forCoroutineContext(MyRequestPreprocessor())
                }
                
                sharedInterceptors {
                    forCoroutineContext(MyCoroutineContextInterceptor())
                }
                
                routing {
                    route("users") {
                        interceptors {
                            // registers for current route and sub-routes.
                            forModification(MyModificationInterceptor())
                        }
                        
                        requestResponse("get") { payload ->
                            TODO()
                        }
                    }
                }
            }
        }
    }
}
```

So, as you can see, library provides `router` function for `RSocketRequestHandler` context. It has
a few customization settings for handling routing in your own style. In addition to it, for convenience, library also provides its own interceptors API for
kotlin coroutines and modifications.

### Testing

`rsocket-kotlin-router` provides ability to test your routes with `router-test` artifact:

```kotlin
@Test
fun testRoutes() {
    runBlocking {
        val route1 = router.routeAtOrAssert("test")
        val route2 = router.routeAtOrAssert("test.subroute")

        route1.assertHasInterceptor<MyInterceptor>()
        route2.assertHasInterceptor<MyInterceptor>()

        route2.fireAndForgetOrAssert(emptyRSocket(), buildPayload {
            data("test")
        })
    }
}
```
You can refer to [full example](router-test/src/jvmTest/kotlin/com/y9vad9/rsocket/router/test/RouterTest.kt).

## Implementation
To implement this library, you should define next:
```kotlin
repositories {
    maven("https://maven.y9vad9.com")
}

dependencies {
    implementation("com.y9vad9.rsocket.router:router-core:1.1.0")
    // for testing
    implementation("com.y9vad9.rsocket.router:router-test:1.1.0")
}
```
> For now, it's available for JVM only, but as there is no JVM platform API used,
> new targets will be available [upon your request](https://github.com/y9vad9/rsocket-kotlin-router/issues/new).
