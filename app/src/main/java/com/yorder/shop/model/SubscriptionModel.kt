package com.yorder.shop.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.firestore.DocumentId
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SubscriptionNetwork(
        @DocumentId
        val id: String = "",
        val customer_id: String? = null,
        val seller_id: String? = null,
        val sub_status: String? = null,
        val outstanding_amount: Double? = null
): Parcelable

@Parcelize
data class SubscriptionDTO(
    val id: String,
    val customerId: String,
    val sellerId: String,
    val subscriptionStatus: SubscriptionStatus,
    val outstandingAmount: Int,
): Parcelable {
    companion object {
        fun create(input: SubscriptionNetwork): SubscriptionDTO? {
            try {
                val status: SubscriptionStatus = SubscriptionStatus.findValue(input.sub_status ?: "")
                return SubscriptionDTO(
                        id = input.id,
                        customerId = input.customer_id ?: throw NullPointerException("Customer Id can not be null in SubscriptionDTO"),
                        sellerId = input.seller_id ?: throw NullPointerException("Seller Id can not be null in SubscriptionDTO"),
                        subscriptionStatus = status,
                        outstandingAmount = input.outstanding_amount?.toInt() ?: 0
                )
            } catch (exception: Exception) {
                Log.e("NST", "Exception in SubscriptionDTO.create(): ${exception.message}")
                return null
            }
        }
    }
}
