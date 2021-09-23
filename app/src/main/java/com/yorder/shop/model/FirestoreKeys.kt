package com.yorder.shop.model


class CollectionUser {
    companion object {
        // Collection name
        const val name = "Users"
        const val kArea = "area"
        const val kBirthDate = "birth_date"
        const val kCity = "city"
        const val kCountry = "country"
        const val kCreateAt = "created_at"
        const val kCreateBy = "created_by"
        const val kEmail = "email"
        const val kFirstName = "first_name"
        const val kHouseNumber = "house_number"
        const val kLastName = "last_name"
        const val kNotificationToken = "notification_token"
        const val kPhoneNumber = "phone_number"
        const val kPhoneVerified = "phone_verified"

        const val kPincode = "pincode"
        const val kProfileImage = "profile_image_url"
        const val kCustomerQRCodeImage = "customer_qr_code_image_url"

        const val kSellerDescription = "seller_description"
        const val kSellerInfo = "seller_info"
        const val kShopBannerImage = "shop_banner_image"
        const val kShopCategory = "shop_category"
        const val kShopName = "shop_name"
        const val kShopSmallInfo = "shop_small_info"
        const val kGeoHash ="geohash"
        const val kLatitude="latitude"
        const val kLongitude="longitude"
        const val kTimeTable = "time_table"
        const val kSocietyName = "society_name"
        const val kState = "state"
        const val kStreet = "street"
        const val kUpdateAt = "updated_at"
        const val kUpdateBy = "updated_by"

        const val kUserGeoLocation = "user_geo_location"
        const val kUserType = "user_type"
        const val kSearchKeywords = "search_keywords"
    }
}

class CollectionSubscriptions {
    companion object {
        const val name = "Subscriptions"
        const val kCustomerId = "customer_id"
        const val kSellerId = "seller_id"
        const val kSubStatus = "sub_status"
        const val kOutstandingAmount = "outstanding_amount"
    }
}

class CollectionProducts {
    companion object {
        const val name = "Products"
        const val kCreatedAt = "created_at"
        const val kCreatedBy = "created_by"
        const val kIsAvailable = "is_available"
        const val kProductAddOns = "product_add_ons"
        const val kProductImageUrl = "product_image_url"
        const val kProductCategory = "product_category"
        const val kProductName = "product_name"
        const val kProductPrice = "product_price"
        const val kProductSmallInfo = "product_small_info"
        const val kProductType = "product_type"
        const val kProductVariations = "product_variations"
        const val kProductSellerId = "seller_id"
        const val kProductSellingUnit = "selling_unit"
        const val kUpdatedAt = "updated_at"
        const val kUpdatedBy = "updated_by"
    }
}

class CollectionOrders {
    companion object {
        const val name = "Orders"
        const val kCreatedAt = "created_at"
        const val kCreatedBy = "created_by"
        const val kCustomerId = "customer_id"
        const val kOrderStatus = "order_status"
        const val kProductList = "product_list"
        const val kSellerId = "seller_id"
        const val kTotalPrice = "total_price"
        const val kUpdatedAt = "updated_at"
        const val kUpdatedBy = "updated_by"
        const val kProductAddOns = "productAddOns"
        const val kProductName = "productName"
        const val kProductPrice = "productPrice"
        const val kProductQuantity = "productQuantity"
        const val kProductVariations = "productVariation"
        const val kProductInstruction="product_special_instructions"
    }
}

class CollectionCart {
    companion object {
        const val name = "Cart"
        const val kCreatedAt = "created_at"
        const val kCreatedBy = "created_by"
        const val kCustomerId = "customer_id"
        const val kProductList = "product_list"
        const val kAddOnsList = "add_ons_list"
        const val kProductId = "product_id"
        const val kProductQuantity = "product_quantity"
        const val kVariationList = "variation_list"
        const val kSellerId = "seller_id"
        const val kTotalPrice = "total_price"
        const val kUpdatedAt = "updated_at"
        const val kUpdatedBy = "updated_by"
    }
}