# Aliyun OSS backed Gradle Remote Cache

inspired by [gcp-gradle-build-cache
](https://github.com/androidx/gcp-gradle-build-cache)

## Usage

In your settings.gradle(.kts) file add the following

```kotlin
plugins {
  id("io.github.neas-neas.alibuildcache") version "1.0.0"
}

import io.github.neas.AliyunBuildCache
import io.github.neas.AliyunBuildCacheServiceFactory
import io.github.neas.ExportedAliyunCredentials

buildCache {
  registerBuildCacheService(AliyunBuildCache::class, AliyunBuildCacheServiceFactory::class)
  remote(AliyunBuildCache::class) {
    endpoint = "your-aliyun-oss-endpoint"
    bucketName = "bucket-name"
    credentials = ExportedAliyunCredentials("your-access-key-id", "your-secret-key")
    isPush = System.getenv().containsKey("CI")
  }
}
```

`endpoint` and `bucketName` are required.  
credentials can queried from aliyun oss website.  
`isPush` defaults to false

groovy
```groovy
plugins {
    id("io.github.neas-neas.alibuildcache") version "1.0.0"
}

import io.github.neas.AliyunBuildCache
import io.github.neas.AliyunBuildCacheServiceFactory
import io.github.neas.ExportedAliyunCredentials

buildCache {
    registerBuildCacheService(AliyunBuildCache, AliyunBuildCacheServiceFactory)
    remote(AliyunBuildCache) {
        endpoint = 'your-aliyun-oss-endpoint'
        bucketName = "bucket-name"
        credentials = new ExportedAliyunCredentials("your-access-key-id", "your-secret-key")
        push = System.getenv().containsKey("CI")
    }
}
```
