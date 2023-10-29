package com.y9vad9.rsocket.router.service

/**
 * Service is an interface that defines a contract to be implemented by classes that provide a service.
 *
 * Service is built on top of RSocket router in the end in an optimized way.
 */
public interface Service {
    /**
     * The unique identifier to be registered in the RSocket router.
     *
     * This variable is a public constant that represents the unique identifier associated with a service
     * that is to be registered in the RSocket router. The identifier is a string value and must be unique
     * within the scope of the router.
     *
     * It is recommended to use a meaningful and descriptive identifier that identifies the purpose or functionality
     * of the service. The identifier should not contain any special characters or spaces, and it is case-sensitive.
     *
     * @see Service
     */
    public val identifier: String
}