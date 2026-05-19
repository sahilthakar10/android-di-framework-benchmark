package com.codeint.shopapp.kinject.component

object ComponentFactory {
    fun create(): ShopAppComponent = ShopAppComponent::class.create()
}
