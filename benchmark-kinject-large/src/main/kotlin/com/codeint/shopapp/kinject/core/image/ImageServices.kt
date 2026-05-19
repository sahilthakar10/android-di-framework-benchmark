package com.codeint.shopapp.kinject.core.image

import com.codeint.shopapp.kinject.core.network.HttpClient
import com.codeint.shopapp.kinject.core.storage.CacheManager
import me.tatarka.inject.annotations.Inject

@Inject class ImageLoader(private val httpClient: HttpClient, private val cacheManager: CacheManager) { fun load(url: String) = ByteArray(0) }
@Inject class ImageProcessor { fun resize(data: ByteArray, w: Int, h: Int) = data }
@Inject class ThumbnailGenerator(private val ip: ImageProcessor) { fun generate(data: ByteArray, size: Int) = ip.resize(data, size, size) }
