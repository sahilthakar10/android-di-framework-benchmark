package com.codeint.shopapp.metro.graph

import dev.zacsweers.metro.createGraph

object GraphFactory {
    fun create(): ShopAppGraph = createGraph<ShopAppGraph>()
}
