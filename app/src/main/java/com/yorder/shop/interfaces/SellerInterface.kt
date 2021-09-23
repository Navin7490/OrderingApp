package com.yorder.shop.interfaces

import com.yorder.shop.model.UserDTO

interface SellerInterface {
    fun sellerExist(arrayList: ArrayList<UserDTO>)
}