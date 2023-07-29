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

import io.github.neas.core.FileSystemStorageService
import io.github.neas.core.blobKey
import com.aliyun.oss.OSS
import com.aliyun.oss.OSSClientBuilder
import com.aliyun.oss.common.auth.CredentialsProvider
import com.aliyun.oss.common.auth.CredentialsProviderFactory
import org.gradle.api.logging.Logging
import org.gradle.caching.BuildCacheEntryReader
import org.gradle.caching.BuildCacheEntryWriter
import org.gradle.caching.BuildCacheKey
import org.gradle.caching.BuildCacheService
import java.io.ByteArrayOutputStream

/**
 * The service that responds to Gradle's request to load and store results for a given
 * [BuildCacheKey].
 *
 * @param bucketName The name of the bucket that is used to store all the gradle cache entries.
 * This essentially becomes the root of all cache entries.
 */
internal class AliyunBuildCacheService(
    credentials: AliyunCredentials,
    endpoint: String,
    bucketName: String,
    isPush: Boolean,
    isEnabled: Boolean,
    inTestMode: Boolean = false
) : BuildCacheService {

    private val client by lazy {
        clientOptions(credentials(credentials), endpoint)
    }
    private val storageService = if (inTestMode) {
        FileSystemStorageService(bucketName, isPush, isEnabled)
    } else {
        AliyunStorageService(bucketName, isPush, isEnabled, client, endpoint)
    }

    override fun load(key: BuildCacheKey, reader: BuildCacheEntryReader): Boolean {
        logger.info("Loading ${key.blobKey()}")
        val cacheKey = key.blobKey()
        val input = storageService.load(cacheKey) ?: return false
        reader.readFrom(input)
        return true
    }

    override fun store(key: BuildCacheKey, writer: BuildCacheEntryWriter) {
        logger.info("Storing ${key.blobKey()}")
        val cacheKey = key.blobKey()
        val output = ByteArrayOutputStream()
        output.use {
            writer.writeTo(output)
        }
        storageService.store(cacheKey, output.toByteArray())
    }

    override fun close() {
        storageService.close()
    }

    fun validateConfiguration() {
        storageService.validateConfiguration()
    }

    companion object {

        private val logger by lazy {
            Logging.getLogger("AliyunBuildCacheService")
        }

        private fun clientOptions(credentials: CredentialsProvider, endpoint: String): OSS {
            return OSSClientBuilder().build(endpoint, credentials)
        }

        private fun credentials(aliyunCredentials: AliyunCredentials): CredentialsProvider {
            return when (aliyunCredentials) {
                DefaultAliyunCredentials -> CredentialsProviderFactory.newSystemPropertiesCredentialsProvider()
                is SpecificCredentialsProvider -> aliyunCredentials.provider
                is ExportedAliyunCredentials -> CredentialsProviderFactory.newDefaultCredentialProvider(
                    aliyunCredentials.accessKeyId, aliyunCredentials.secretAccessKey
                )
            }
        }
    }
}
