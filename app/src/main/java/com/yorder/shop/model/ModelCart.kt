package com.yorder.shop.model

import android.os.Parcelable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ModelCart(
   var productId: String = "",
   var customerId: String = "",
   var sellerID: String = "",
   var productQuantity: String = "",
   var productName: String = "",
   var productPrice: String = "",
   var productVariation: String = "",
   var productAddOns: String = ""


) :Parcelable{
   companion object{
      fun create(productModel: ProductModel,cartProductModel: CartProductModel):ModelCart{


         var nameAddOns = ""
         var nameVariation = ""
         var productPrice = 0

         productPrice += productModel.productPrice.toInt()
         if (productModel.productVariants.isNotEmpty() || productModel.productAddOns.isNotEmpty()) {

           val sbAddOns = StringBuffer()
           val sbVariation = StringBuffer()
            productModel.productVariants.forEach { variationItem ->

              val  variation = cartProductModel.productVariantsList.filter {
                  it == variationItem.id
               }
               variation.forEach {
                  if (it == variationItem.id) {
                     productPrice += variationItem.price.toInt()

                     nameVariation =
                        sbVariation.append(variationItem.optionName).toString()

                  }
               }

            }
            productModel.productAddOns.forEach { addOnsItem ->

             val  addOns = cartProductModel.productAddOnsList.filter {
                  it == addOnsItem.id
               }
               addOns.forEach {
                  if (it == addOnsItem.id) {
                     productPrice += addOnsItem.price.toInt()

                     nameAddOns =
                        sbAddOns.append(addOnsItem.optionName).toString()
                     nameAddOns = sbAddOns.append(". ").toString()
                  }
               }

            }

         }
         val totalPrice= productPrice *cartProductModel.productQuantity

         return ModelCart(
            productId = productModel.id,
            customerId = Firebase.auth.uid?:"",
            sellerID = productModel.sellerId,
            productQuantity = cartProductModel.productQuantity.toString(),
            productName =productModel.productName,
            productPrice=totalPrice.toString(),
            productVariation = nameVariation,
            productAddOns = nameAddOns

         )

      }
   }
}
