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

import org.gradle.caching.BuildCacheService
import org.gradle.caching.BuildCacheServiceFactory

/**
 * Factory used by Gradle to create AliyunBuildCache instances.
 */
class AliyunBuildCacheServiceFactory : BuildCacheServiceFactory<AliyunBuildCache> {
    override fun createBuildCacheService(
        buildCache: AliyunBuildCache,
        describer: BuildCacheServiceFactory.Describer,
    ): BuildCacheService {
        describer
            .type("Aliyun-backed")
            .config("endpoint", buildCache.endpoint)
            .config("bucketName", buildCache.bucketName)
            .config("isPushSupported", "${buildCache.isPush}")
            .config("isEnabled", "${buildCache.isEnabled}")
            .config("credentialsType", "${buildCache.credentials}")

        val service = AliyunBuildCacheService(
            credentials = buildCache.credentials,
            endpoint = buildCache.endpoint,
            bucketName = buildCache.bucketName,
            isPush = buildCache.isPush,
            isEnabled = buildCache.isEnabled
        )
        service.validateConfiguration()
        return service
    }
}
