package com.yorder.shop.model

data class ModelSellers(
    var notificationToken:String ="",
    var sellerId: String ="",
    var shopName: String="",
    var shopSmallInfo: String="",
    var shopDescription: String="",
    var shopBannerImage: String="",
    var city:String ="",
    var sellingUnit: String="",
    var price: String="",
    var variations: ArrayList<Map<String, String>> =  arrayListOf(),
    var addOns: ArrayList<Map<String, String>> = arrayListOf(),
    var isAvailable: Boolean = false
)

