package com.yorder.shop.interfaces

import com.yorder.shop.model.ProductModel

interface SellerProductItemInterface {
    fun plusButtonTapped(productModel: ProductModel)
    fun minusButtonTapped(productModel: ProductModel)
    fun addButtonTapped(productModel: ProductModel)
    fun onViewCartUpdated(totalPrice:Int,size:Int)

}