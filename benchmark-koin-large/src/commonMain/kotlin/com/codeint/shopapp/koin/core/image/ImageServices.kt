package com.codeint.shopapp.koin.core.image

import com.codeint.shopapp.koin.core.network.HttpClient
import com.codeint.shopapp.koin.core.storage.CacheManager

class ImageLoader(private val httpClient: HttpClient, private val cacheManager: CacheManager) { fun load(url: String) = ByteArray(0) }
class ImageProcessor { fun resize(data: ByteArray, w: Int, h: Int) = data }
class ThumbnailGenerator(private val ip: ImageProcessor) { fun generate(data: ByteArray, size: Int) = ip.resize(data, size, size) }
