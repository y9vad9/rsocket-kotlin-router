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
            routing(routeSeparator = '.',) {
                route("users") {
                    requestResponse("get") { payload ->
                        TODO()
                    }
                }
            }
        }
    }
}
```

So, as you can see, library provides `routing` function for `RSocketRequestHandler` context. It has
a few customization settings for handling routing in your own style, for example, as
it's done in my project:

```kotlin
routing(
    // in my project, I propagate routes using coroutine context and interceptors
    routeProvider = { coroutineContext[AuthorizableRouteContext]!!.route },
    routeSeparator = '.',
) {
    usersApi(...)
    authorizationsApi(...)
}
```

### Routes declaration example
```kotlin
fun RoutingBuilder.users(
    service: RSocketUsersService,
): Unit = route("users") {
    requestResponse("email.edit") { payload ->
        payload.decoding<EditEmailRequest> { TODO() }
    }

    route("profile") {
        requestResponse("edit") { payload ->
            payload.decoding<SerializableUserPatch> { service.editUser(it).asPayload() }
        }

        requestResponse("list") { payload ->
            payload.decoding<GetUsersRequest> { service.getUsers(it.ids).asPayload() }
        }
    }
    
    // also, you can define a multiple types of request methods on a single route:
    route("profile") {
        route("get") {
            // to retrieve value only once
            requestResponse { payload -> TODO() }
            
            // to stream changes, for example
            requestStream { payload -> flowOf(TODO()) }
        }
    }
}
```
> `requestResponse(route: String, block: RSocket.(Payload) -> Payload)` is an extension function.

In addition to it, for convenience, library also provides its own interceptors API for
kotlin coroutines (example from my pet-project):
```kotlin
@OptIn(ExperimentalMetadataApi::class, ExperimentalRouterApi::class)
class AuthorizableRoutedRequesterInterceptor(
    private val authorizationProvider: AuthorizationProvider,
) : CoroutineContextInterceptor() {
    override fun coroutineContext(
        payload: Payload,
        coroutineContext: CoroutineContext
    ): CoroutineContext = with(payload) {
        val entries = metadata?.read(CompositeMetadata)?.entries
        val route = entries?.firstOrNull().route()
        val accessHash = entries?.getOrNull(1)?.accessHash()

        return coroutineContext + AuthorizableRouteContext(route, accessHash, authorizationProvider)
    }
}
```
You can take a look at CoroutineContextInterceptor sources [here](/src/commonMain/kotlin/io/timemates/backend/rsocket/routing/interceptors/CoroutineContextInterceptor.kt).

## Implementation
To implement this library, you should define next:
```kotlin
repositories {
    maven("https://maven.y9vad9.com")
}

dependencies {
    implementation("com.y9vad9.rsocket.router:core:1.0.0")
}
```
> For now, it's available for JVM only, but as there is no JVM platform API used,
> new targets will be available [upon your request](https://github.com/y9vad9/rsocket-kotlin-router/issues/new).
