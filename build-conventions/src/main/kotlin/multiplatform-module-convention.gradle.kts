import org.jetbrains.kotlin.gradle.dsl.*

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvm()
    jvmToolchain(11)

    explicitApi = ExplicitApiMode.Strict
}

mavenPublishing {
    pom {
        url.set("https://github.com/y9vad9/rsocket-kotlin-router")
        inceptionYear.set("2023")

        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
                distribution.set("https://opensource.org/licenses/MIT")
            }
        }

        developers {
            developer {
                id.set("y9vad9")
                name.set("Vadym Yaroshchuk")
                url.set("https://github.com/y9vad9/")
            }
        }

        scm {
            url.set("https://github.com/y9vad9/rsocket-kotlin-router")
            connection.set("scm:git:git://github.com/y9vad9/rsocket-kotlin-router.git")
            developerConnection.set("scm:git:ssh://git@github.com/y9vad9/rsocket-kotlin-router.git")
        }

        issueManagement {
            system.set("GitHub Issues")
            url.set("https://github.com/y9vad9/rsocket-kotlin-router/issues")
        }
    }
}

publishing {
    repositories {
        maven {
            name = "y9vad9Maven"

            url = uri(
                "sftp://${System.getenv("SSH_HOST")}:22/${System.getenv("SSH_DEPLOY_PATH")}"
            )

            credentials {
                username = System.getenv("SSH_USER")
                password = System.getenv("SSH_PASSWORD")
            }
        }
    }
}