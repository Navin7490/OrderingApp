package com.yorder.shop.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable
import kotlin.Exception

class ModelProductList {
    var sellerId:String? = null
    var shopBannerImage:String? = null
    var shopName:String? = null
    var upDateId:String? = null
    var customerId:String? = null
    var productId: String? = null
    var productName: String? = null
    var productPrice: String? = null
    var productImage: String? = null
    var productVariations: ArrayList<*>? = null
}

data class NetworkProductModel(
        @DocumentId
        val id: String = "",
        @JvmField
        var is_available: Boolean = false,
        val product_image_url: String? = null,
        val product_category:String? = null,
        val product_name: String? = null,
        val product_price: String? = null,
        val product_small_info: String? = null,
        val product_type: String? = null,
        val seller_id:  String? = null,
        val selling_unit: String? = null,
        val product_add_ons: List<Map<String, Any>>? = null,
        val product_variations: List<Map<String, Any>>? = null,
        @ServerTimestamp
        var created_at: Timestamp? = null,
        @ServerTimestamp
        var updated_at: Timestamp? = null,
        var created_by: String? = null,
        var updated_by: String? = null
)

data class ProductModel(
        val id: String,
        val isAvailable: Boolean,
        val productImageUrl: String,
        val productCategory: String,
        val productName: String,
        val productPrice: String,
        val productSmallInfo: String,
        val productType: String,
        val sellerId: String,
        val sellingUnit: String,
        val productAddOns: List<ProductCustomOption>,
        val productVariants: List<ProductCustomOption>,
        val createdAt: Timestamp,
        val updateAt: Timestamp,
        val createdBy: String,
        val updatedBy: String
): Serializable {
    companion object {
        fun create(input: NetworkProductModel): ProductModel? {
            try {
                val addOns = if (input.product_add_ons != null) {
                    input.product_add_ons.mapNotNull { ProductCustomOption.create(it) }
                } else {
                    listOf<ProductCustomOption>()
                }

                val variants = if (input.product_variations != null) {
                    input.product_variations.mapNotNull { ProductCustomOption.create(it) }
                } else {
                    listOf<ProductCustomOption>()
                }




                return ProductModel(
                        id = input.id,
                        isAvailable = input.is_available,
                        productImageUrl = input.product_image_url ?: "",
                        productCategory = input.product_category?: return null,
                        productName = input.product_name ?: return null,
                        productPrice = input.product_price ?: return null,
                        productSmallInfo = input.product_small_info ?: "",
                        productType = input.product_type ?: return null,
                        sellerId = input.seller_id ?: return null,
                        sellingUnit = input.selling_unit ?: return null,
                        productAddOns = addOns,
                        productVariants = variants,
                        createdAt = input.created_at ?: Timestamp.now(),
                        updateAt = input.created_at ?: Timestamp.now(),
                        createdBy = input.created_by ?: return null,
                        updatedBy = input.updated_by ?: return null
                )
            } catch (ex: Exception) {
                Log.e("NST", "Exception in ProductModel.create()")
                Log.e("NST", ex.toString())
                return null
            }
        }
    }
}

data class ProductCustomOption(
        val id: String,
        val optionName: String,
        val price: String,
        var isSelected: Boolean = false
): Serializable {
    companion object {
        fun create(input: Map<String, Any>): ProductCustomOption? {
            /*
            Soft Bun: 20
            id: optionId
             */
            var kId: String? = null
            var kOptionName: String? = null
            var kPrice: String? = null

            for ((key, value) in input) {
                if (key == "id" && kId == null) {
                    kId = value as String
                } else {
                    kOptionName = key
                    kPrice = value as String
                }
            }

            return ProductCustomOption(
                    id = kId ?: return null,
                    optionName = kOptionName ?: return null,
                    price = kPrice ?: return null
            )
        }
    }
}

data class ProductCategoryModel(
        val categoryName: String,
        val productList: List<ProductModel>
)



