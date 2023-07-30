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

import io.github.neas.core.RemoteGradleBuildCache

/**
 * Gradle Build Cache that uses Aliyun OSS buckets as a backing for load and store
 * Gradle results.
 */
abstract class AliyunBuildCache(
) : RemoteGradleBuildCache() {
    /**
     * The Aliyun OSS endpoint where bucket is located in.
     */
    lateinit var endpoint: String

    /**
     * The type of credentials to use to connect to Aliyun.
     */
    override var credentials: AliyunCredentials = DefaultAliyunCredentials
}
