package com.yorder.shop.model

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.ServerTimestamp
import java.io.Serializable

const val kProductId: String = "product_id"
const val kProductSpecialInstruction:String ="product_special_instructions"
const val kProductQuantity: String = "product_quantity"
const val kProductVariationList: String = "variation_list"
const val kProductAddOnsList: String = "add_ons_list"

data class NetworkCartModel(
        @DocumentId
        val id: String = "",
        @get:PropertyName("customer_id")
        @set:PropertyName("customer_id")
        var customerId: String? = null,
        @get:PropertyName("seller_id")
        @set:PropertyName("seller_id")
        var sellerId: String? = null,
        @ServerTimestamp
        @get:PropertyName("created_at")
        @set:PropertyName("created_at")
        var createdAt: Timestamp? = null,
        @ServerTimestamp
        @get:PropertyName("updated_at")
        @set:PropertyName("updated_at")
        var updatedAt: Timestamp? = null,
        @get:PropertyName("created_by")
        @set:PropertyName("created_by")
        var createdBy: String? = null,
        @get:PropertyName("updated_by")
        @set:PropertyName("updated_by")
        var updatedBy: String? = null,
        var product_list: List<Map<String, Any>>? = null
) {
        companion object {
                fun create(input: CartModel): NetworkCartModel {
                        val productList = input.productList.map {
                                mapOf(
                                        kProductId to it.productId,
                                        kProductQuantity to it.productQuantity,
                                        kProductVariationList to it.productVariantsList,
                                        kProductAddOnsList to it.productAddOnsList

                                )
                        }
                        return NetworkCartModel(
                                id = input.id,
                                customerId = input.customerId,
                                sellerId = input.sellerId,
                                createdAt = input.createdAt,
                                updatedAt = Timestamp.now(),
                                createdBy = input.createdBy,
                                updatedBy = FirebaseAuth.getInstance().uid!!,
                                product_list = productList
                        )
                }
        }
}

data class CartModel (
        val id: String,
        val customerId: String,
        val sellerId: String,
        val productList: ArrayList<CartProductModel>,
        val createdBy: String,
        val updatedBy: String,
        val createdAt: Timestamp,
        val updatedAt: Timestamp): Serializable {
        companion object {
                fun create(input: NetworkCartModel): CartModel? {
                        try {
                                val cartProductList = if (input.product_list != null) {
                                        input.product_list!!.mapNotNull { CartProductModel.create(it) }
                                } else {
                                        listOf()
                                }
                                val cartProductArray = arrayListOf<CartProductModel>()
                                cartProductArray.addAll(cartProductList)
                                return CartModel(
                                        id = input.id,
                                        customerId = input.customerId ?: return null,
                                        sellerId = input.sellerId ?: return null,
                                        createdBy = input.createdBy ?: return null,
                                        updatedBy = input.updatedBy ?: return null,
                                        createdAt = input.createdAt ?: return null,
                                        productList = cartProductArray,
                                        updatedAt = input.updatedAt ?: return null
                                )
                        } catch (ex: Exception) {
                                Log.e("NST", "Exception in CartModel.create()")
                                Log.e("NST", ex.toString())
                                return null
                        }
                }
        }
}

data class CartProductModel(
        val productId: String,
        var productQuantity: Int,
        val productAddOnsList: List<String>,
        val productVariantsList: List<String>
): Serializable {
        companion object {
                fun create(input: Map<String, Any>): CartProductModel? {
                        try {
                                val productId: String? = input["product_id"] as? String
                                val productQ: Long? = input["product_quantity"] as? Long
                                val addOns: List<String>? = input["add_ons_list"] as? List<String>
                                val variants: List<String>? = input["variation_list"] as? List<String>

                                return CartProductModel(
                                        productId = productId ?: return null,
                                        productQuantity = productQ?.toInt() ?: return null,
                                        productAddOnsList = addOns ?: listOf(),
                                        productVariantsList = variants ?: listOf()
                                )
                        } catch (ex: Exception) {
                                Log.e("NST", "Exception in CartProductModel.create()")
                                Log.e("NST", ex.toString())
                                return null
                        }
                }
        }
}

