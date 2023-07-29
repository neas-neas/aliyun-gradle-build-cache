/*
 * Copyright 2023 The Android Open Source Project
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

package io.github.neas

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

/**
 * A simple functional test for the 'io.github.neas.AliyunBuildCache' plugin.
 */
class AliyunGradleBuildCachePluginFunctionalTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private fun getProjectDir() = tempFolder.root
    private fun getBuildFile() = getProjectDir().resolve("build.gradle.kts")
    private fun getSettingsFile() = getProjectDir().resolve("settings.gradle.kts")

    @Test
    fun `can run tasks task`() {
        getSettingsFile().writeText(
            """
            import io.github.neas.AliyunBuildCache
            import io.github.neas.ExportedAliyunCredentials
        
            plugins {
              id("io.github.neas-neas.alibuildcache")
            }
            
            buildCache {
              remote(AliyunBuildCache::class) {
                  bucketName = "bar"
                  credentials = ExportedAliyunCredentials("key-id", "secret-key")
              }
            }
            """.trimIndent()
        )
        getBuildFile().writeText("")

        // Run the build
        val runner = GradleRunner.create()
        runner.forwardOutput()
        runner.withPluginClasspath()
        runner.withArguments("tasks")
        runner.withProjectDir(getProjectDir())
        runner.build()
    }
}
