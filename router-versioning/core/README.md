# `router-versioned-cpre`

This artifact is an auxiliary to the base RSocket Kotlin Router library. It enhances your RSocket
services with routing layer by adding support for semantic versioning.

## Version Providers

This artifact introduces the concept of Version Providers. This is encapsulated by
the `VersionPreprocessor` class used to extract and process version details from the incoming RSocket payload.
You must override this class to provide a custom method to fetch version details, typically fetched from the metadata of
the payload.

## Routing and Versioning

`router-versioned-core` provides an intuitive DSL for routing and versioning, simplifying the user interaction model.
Let's take a brief look at how to use it:

```kotlin
@OptIn(ExperimentalInterceptorsApi::class)
public class VersionProviderPreprocessor : VersionPreprocessor() {
    override fun version(payload: Payload): Version {
        TODO()
    }
}

public val Version.Companion.V1_0: Version by lazy { Version(1, 0) }
public val Version.Companion.V2_0: Version by lazy { Version(2, 0) }

val router: Router = router {
    preprocessors {
        forCoroutineContext(MyVersionPreprocessor())
    }

    routing {
        route("auth") {
            // short version
            requestResponseV("start") {
                version(1) { payload ->
                    // handle requests for version "1.0"
                    Payload.Empty
                }
                version(2) { payload ->
                    // handle requests for version "2.0"
                    Payload.Empty
                }
            }
            // or longer version
            requestStreamVersioned("confirm") {
                // you can specify version up to minor and patch
                version(Version.V1_0) { payload ->
                    // handle requests for version "1.0"
                    flow(Payload.Empty)
                }

                // you can specify version up to minor and patch
                version(Version.V2_0) { payload ->
                    // handle requests for version "2.0"
                    flow(Payload.Empty)
                }
            }
        }
    }
}
```
> **Note** <br>
> But, you shouldn't use patch version for versioning your requests. It's used only as annotation.
> 
> `minor` should also be used only as an annotation and only for versioning new requests ideally. Changing
> contract of specific request should always happen on new major version. Read more about [semantic versioning](https://semver.org/).

In this example, the DSL allows developers to define different handlers for each available version of their endpoints.
This way, clients running on different versions can coexist and interact with the service in a consistent fashion.

## Implementation
```kotlin
repositories {
    maven("https://maven.y9vad9.com")
}

dependencies {
    implementation("com.y9vad9.rsocket.router:router-versioned-core:$version")
}
```