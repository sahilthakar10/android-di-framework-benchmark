package com.codeint.shopapp.hilt.core.image

import com.codeint.shopapp.hilt.core.network.HttpClient
import com.codeint.shopapp.hilt.core.storage.CacheManager
import javax.inject.Inject

class ImageLoader @Inject constructor(private val httpClient: HttpClient, private val cacheManager: CacheManager) {
    fun load(url: String): ByteArray = cacheManager.get(url) as? ByteArray ?: ByteArray(0)
}

class ImageProcessor @Inject constructor() {
    fun resize(data: ByteArray, width: Int, height: Int): ByteArray = data
    fun compress(data: ByteArray, quality: Int): ByteArray = data
}

class ThumbnailGenerator @Inject constructor(private val imageProcessor: ImageProcessor) {
    fun generate(data: ByteArray, size: Int): ByteArray = imageProcessor.resize(data, size, size)
}
