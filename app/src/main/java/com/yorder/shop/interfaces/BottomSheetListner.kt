package com.yorder.shop.interfaces

import com.yorder.shop.model.CartModel
import com.yorder.shop.model.ProductModel

interface BottomSheetListner {
        fun  onclick(productModel: ProductModel, cartModel: CartModel)
}