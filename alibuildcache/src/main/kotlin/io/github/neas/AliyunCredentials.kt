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

import io.github.neas.core.Credentials
import com.aliyun.oss.common.auth.CredentialsProvider

/**
 * [DefaultAliyunCredentials] or [ExportedAliyunCredentials] to use to authenticate to Aliyun OSS.
 */
sealed interface AliyunCredentials : Credentials

/**
 * Use DefaultCredentialsProvider to authenticate to Aliyun.
 */
object DefaultAliyunCredentials : AliyunCredentials

/**
 * Use a specific credentials provider
 * */
class SpecificCredentialsProvider(val provider: CredentialsProvider) : AliyunCredentials

/**
 * Use provided keys to authenticate to Aliyun.
 */
class ExportedAliyunCredentials(val accessKeyId: String, val secretAccessKey: String) : AliyunCredentials
