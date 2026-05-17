package com.codeint.shopapp.metro.core.image

import com.codeint.shopapp.metro.core.network.HttpClient
import com.codeint.shopapp.metro.core.storage.CacheManager
import dev.zacsweers.metro.Inject

class ImageLoader @Inject constructor(private val httpClient: HttpClient, private val cacheManager: CacheManager) { fun load(url: String) = ByteArray(0) }
class ImageProcessor @Inject constructor() { fun resize(data: ByteArray, w: Int, h: Int) = data }
class ThumbnailGenerator @Inject constructor(private val ip: ImageProcessor) { fun generate(data: ByteArray, size: Int) = ip.resize(data, size, size) }
