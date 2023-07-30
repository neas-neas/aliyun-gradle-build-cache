/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

plugins {
    id("maven-publish")
    id("signing")
    id("com.gradle.plugin-publish") version "1.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("com.vanniktech.maven.publish") version "0.25.3"
}

dependencies {
    implementation(gradleApi())
    implementation("com.aliyun.oss:aliyun-sdk-oss:3.17.0")
}

gradlePlugin {
    website.set("https://github.com/neas-neas/aliyun-gradle-build-cache")
    vcsUrl.set("https://github.com/neas-neas/aliyun-gradle-build-cache")
    plugins {
        create("alibuildcache") {
            id = "io.github.neas-neas.alibuildcache"
            displayName = "Gradle Aliyun Build Cache Plugin"
            description = "Gradle remote build cache backed by Aliyun"
            implementationClass = "io.github.neas.AliyunGradleBuildCachePlugin"
            tags.set(listOf("buildcache", "aliyun", "caching"))
        }
    }
}

group = "io.github.neas-neas"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

testing {
    suites {
        // Configure the built-in test suite
        val test by getting(JvmTestSuite::class) {
            useJUnit()
        }

        // Create a new test suite
        val functionalTest by registering(JvmTestSuite::class) {
            useJUnit()

            dependencies {
                // functionalTest test suite depends on the production code in tests
                implementation(project())
            }

            targets {
                all {
                    // This test suite should run after the built-in test suite has run its tests
                    testTask.configure { shouldRunAfter(test) }
                }
            }
        }
    }
}

gradlePlugin.testSourceSets(sourceSets["functionalTest"])

tasks.named<Task>("check") {
    // Include functionalTest as part of the check lifecycle
    dependsOn(testing.suites.named("functionalTest"))
}

tasks.withType<Sign>().configureEach {
    onlyIf { project.hasProperty("signing.keyId") }
}
