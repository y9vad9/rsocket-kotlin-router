package com.y9vad9.maven.publish

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.the
import java.util.*

class DeployPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.apply(plugin = "maven-publish")

        val configuration = target.extensions.create<LibraryDeployExtension>(name = "deployLibrary")

        target.afterEvaluate {
            configuration.targets.forEach { (tag, data) ->
                configuration.apply {
                    data.host ?: return@forEach println("Skipping deployment of $tag, no host provided")
                    data.deployPath ?: error("`deployPath` should be defined in `deploy`")
                    data.componentName ?: error("`componentName` should be defined in `deploy`")
                    data.name ?: error("`name` should be defined in `deploy`")
                    data.description ?: error("`description` should be defined in `deploy`")
                }


                project.the<PublishingExtension>().apply {
                    publications {
                        create<MavenPublication>("deploy to $tag") {
                            group = data.group ?: project.group
                            artifactId = data.artifactId ?: project.name
                            version = data.version ?: error("shouldn't be null")

                            pom {
                                name.set(data.name ?: error("shouldn't be null"))
                                description.set(data.description ?: error("shouldn't be null"))
                            }
                            from(components[data.componentName ?: error("shouldn't be null")])
                        }
                    }
                    repositories {
                        maven {
                            name = data.name ?: error("shouldn't be null")
                            version = data.version ?: error("shouldn't be null")

                            url = uri(
                                "sftp://${data.user}@${data.host}:22/${data.deployPath}"
                            )

                            credentials {
                                username = data.user
                                password = data.password
                            }
                        }
                    }
                }
            }
        }
    }
}