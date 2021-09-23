package com.yorder.shop.interfaces

interface PriceWatcher {
    fun onPriceUpdated(productName: String,variationId:String,price:Int)

}