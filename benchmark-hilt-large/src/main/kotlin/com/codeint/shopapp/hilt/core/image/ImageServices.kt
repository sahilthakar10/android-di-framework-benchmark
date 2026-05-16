package com.codeint.shopapp.hilt.core.image

import com.codeint.shopapp.hilt.core.network.HttpClient
import com.codeint.shopapp.hilt.core.storage.CacheManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton class ImageLoader @Inject constructor(private val httpClient: HttpClient, private val cacheManager: CacheManager) {
    fun load(url: String): ByteArray = cacheManager.get(url) as? ByteArray ?: downloadAndCache(url)
    private fun downloadAndCache(url: String): ByteArray { val data = ByteArray(0); cacheManager.put(url, data); return data }
}

@Singleton class ImageProcessor @Inject constructor() {
    fun resize(data: ByteArray, width: Int, height: Int): ByteArray = data
    fun compress(data: ByteArray, quality: Int): ByteArray = data
    fun applyCrop(data: ByteArray, x: Int, y: Int, w: Int, h: Int): ByteArray = data
}

@Singleton class ThumbnailGenerator @Inject constructor(private val imageProcessor: ImageProcessor) {
    fun generate(data: ByteArray, size: Int): ByteArray = imageProcessor.resize(data, size, size)
}
