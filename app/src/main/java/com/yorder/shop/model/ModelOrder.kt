package com.yorder.shop.model

import android.os.Parcelable
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize


data class NetworkOrderModel(
    @DocumentId
    val id:String="",
    @ServerTimestamp
    val created_at: Timestamp?= null,
    val created_by: String?= null,
    val customer_id:String?= null,
    val order_status: String?= null,
    val product_special_instructions:String= "",
    val product_list:List<Map<String,Any>>?= null,
    val seller_id:String?= null,
    val total_price:String?= null,
    @ServerTimestamp
    val updated_at:Timestamp?= null,
    val updated_by:String?= null

)


@Parcelize
data class OrderModel(
    var isexpand:Boolean=false,
    val orderId :String,
    val orderStatus:String,
    val productInstruction:String="",
    val customerId:String,
    val sellerId :String,
    val createdBy:String,
    val createdAt: Timestamp,
    val updatedAt:Timestamp,
    val updatedBy:String,
    val totalPrice:String,
    var sellerModel: UserDTO?=null,
    var productItemList:ArrayList<ModelOrderItem>

):Parcelable{
    companion object {
        fun create(input:NetworkOrderModel) :OrderModel{

            val itemList =if(input.product_list!=null) {
                input.product_list.map { ModelOrderItem.create(it) }
            } else {
                arrayListOf()
            }
            return OrderModel(
                isexpand = false,
                orderId = input.id,
                orderStatus = input.order_status?:throw NullPointerException("can't empty status"),
                    productInstruction = input.product_special_instructions,

                    customerId = input.customer_id?:throw NullPointerException("can't empty customer id"),
                sellerId = input.seller_id?:throw NullPointerException("can't empty seller id"),
                createdBy = input.created_by?:throw NullPointerException("can't empty created by"),
                createdAt =  input.created_at?:throw NullPointerException("can't empty created at"),
                updatedAt = input.updated_at?:throw NullPointerException("can't empty updated at"),
                updatedBy = input.updated_by?:throw NullPointerException("can't empty updated by"),
                totalPrice = input.total_price?:throw NullPointerException("can't empty total price"),
                sellerModel = null,
                productItemList = itemList as ArrayList<ModelOrderItem>)
        }
    }
}




@Parcelize
data class ModelOrderItem(
    val name:String,
    val itemVariation:String,
    val itemAddons:String,
    val qty:String,
    val price:String
):Parcelable{
    companion object{
        fun create(input:Map<String,Any>):ModelOrderItem{


            return ModelOrderItem(
                name = input[CollectionOrders.kProductName].toString(),
                itemVariation = input[CollectionOrders.kProductVariations].toString(),
                itemAddons = input[CollectionOrders.kProductAddOns].toString(),
                qty =input[CollectionOrders.kProductQuantity].toString(),
                price = input[CollectionOrders.kProductPrice].toString()
                )
        }
    }
}


@Parcelize
data class CreateOrder(
    var productQuantity: String ="" ,
    var productName:String ="",
    var productPrice: String ="",
    var productVariation:String ="",
    var productAddOns:String = "",
):Parcelable{
    companion object{
        fun create(cart: ModelCart):CreateOrder{
            return CreateOrder(
                productQuantity =cart.productQuantity,
                productName = cart.productName,
                productPrice = cart.productPrice,
                productVariation = cart.productVariation,
                productAddOns = cart.productAddOns
            )
        }
    }
}
