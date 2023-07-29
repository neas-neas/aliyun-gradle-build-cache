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

import io.github.neas.core.StorageService
import com.aliyun.oss.OSS
import com.aliyun.oss.model.PutObjectRequest
import org.gradle.api.logging.Logging
import java.io.InputStream
import com.aliyun.oss.model.DeleteObjectsRequest
import com.aliyun.oss.model.GetObjectRequest
import java.io.ByteArrayInputStream

class AliyunStorageService(
    override val bucketName: String,
    override val isPush: Boolean,
    override val isEnabled: Boolean,
    private val client: OSS,
    private val region: String,
    private val sizeThreshold: Long = BLOB_SIZE_THRESHOLD
) : StorageService {

    override fun load(cacheKey: String): InputStream? {
        if (!isEnabled) {
            logger.info("Not Enabled")
            return null
        }

        val request = GetObjectRequest(bucketName, cacheKey)
        logger.info("Loading $cacheKey via $request")
        return load(client, request, sizeThreshold)
    }

    override fun store(cacheKey: String, contents: ByteArray): Boolean {
        if (!isEnabled) {
            logger.info("Not Enabled")
            return false
        }

        if (!isPush) {
            logger.info("No push support")
            return false
        }

        if (contents.size > sizeThreshold) {
            logger.info("Cache item $cacheKey size is ${contents.size} and it exceeds $sizeThreshold. Will skip storing")
            return false
        }

        val request = PutObjectRequest(bucketName, cacheKey, ByteArrayInputStream(contents))
        logger.info("Storing $cacheKey via $request")
        return store(client, request)
    }

    override fun delete(cacheKey: String): Boolean {
        if (!isEnabled) {
            logger.info("Not Enabled")
            return false
        }

        if (!isPush) {
            logger.info("No push support")
            return false
        }

        val request = DeleteObjectsRequest(bucketName)
        request.withKey(cacheKey)
        logger.info("Deleting $cacheKey via $request")
        return delete(client, request)
    }

    override fun validateConfiguration() {
        val buckets = client.listBuckets()
        if (buckets.none { bucket -> bucket.name == bucketName }) {
            throw Exception("Bucket $bucketName under project $region cannot be found or it is not accessible using the provided credentials")
        }
    }

    override fun close() {
        client.shutdown()
    }

    companion object {

        private const val BLOB_SIZE_THRESHOLD = 50 * 1024 * 1024L

        private val logger by lazy {
            Logging.getLogger("AliyunStorageService")
        }

        private fun load(
            client: OSS,
            request: GetObjectRequest,
            sizeThreshold: Long,
        ): InputStream? {
            return try {
                val ossObject = client.getObject(request)
                val contentLength = ossObject.objectMetadata.contentLength
                val inputStream = ossObject.objectContent
                if (contentLength > sizeThreshold) {
                    logger.info("Cache item ${request.key} size is $contentLength and it exceeds $sizeThreshold. Will skip loading")
                    inputStream.close()
                    null
                } else {
                    inputStream
                }
            } catch (e: Exception) {
                logger.debug("Unable to load $request", e)
                null
            }
        }

        private fun store(
            client: OSS,
            request: PutObjectRequest
        ): Boolean {
            return try {
                client.putObject(request)
                true
            } catch (e: Exception) {
                logger.debug("Unable to store $request", e)
                false
            }
        }

        private fun delete(client: OSS, request: DeleteObjectsRequest): Boolean {
            return client.deleteObject(request).response.isSuccessful
        }
    }
}