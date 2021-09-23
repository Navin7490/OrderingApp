package com.yorder.shop.model

import android.os.Parcelable
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.lang.StringBuilder

enum class SubscriptionStatus(val value: String) {
    NONE("None"),
    PENDING("Pending"),
    APPROVED("Approved"),
    CANCELLED("Cancelled");

    companion object {
        private val types = values().associateBy { it.value }

        fun findValue(value: String) =
                types[value]
                        ?: throw NullPointerException("No Subscription type exists for given String value: $value")
    }
}

enum class UserType(val value: String) {
    CUSTOMER("customer"),
    SELLER("seller"),
    EMPLOYEE("employee");

    companion object {
        private val types = values().associateBy { it.value }

        fun findValue(value: String) =
                types[value]
                        ?: throw NullPointerException("No user type exists for given String value")
    }
}

data class NetworkUserModel(
        @DocumentId
        val id: String = "",
        val area: String? = null,
        val birth_date: Timestamp? = null,
        val city: String? = null,
        val country: String? = null,
        @ServerTimestamp
        val created_at: Timestamp? = null,
        val customer_qr_code_image_url: String? = null,
        val email: String? = null,
        val first_name: String? = null,
        val house_number: String? = null,
        val last_name: String? = null,
        val notification_token: String? = null,
        val phone_number: String? = null,
        val pincode: String? = null,
        val profile_image_url: String? = null,
        val shop_name: String? = null,
        val shop_small_info: String? = null,
        val shop_banner_image: String? = null,
        val shop_category: List<String>? = null,
        val geohash: String? = null,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val society_name: String? = null,
        val state: String? = null,
        val street: String? = null,
        @ServerTimestamp
        val updated_at: Timestamp? = null,
        val user_geo_location: GeoPoint? = null,
        val user_type: String? = null
)

@Parcelize
data class UserDTO(
        val id: String,
        val area: String,
        val birthDate: Timestamp?,
        val city: String,
        val country: String,
        val createdAt: Timestamp,
        val email: String,
        val firstName: String,
        val houseNumber: String,
        val lastName: String,
        val notificationToken: String,
        val phoneNumber: String,
        val pincode: String,
        var profileImageUrl: String,
        var customerQRCodeUrl: String,
        var shopCategory: List<String>,
        val shopName: String,
        val shopSmallInfo: String,
        val shopBannerImageUrl: String,
        val geohash: String,
        val latitude: Double,
        val longitude: Double,
        val societyName: String,
        val state: String,
        val street: String,
        val updatedAt: Timestamp,
        val address: String,
        var subStatus: SubscriptionStatus,
        var subId: String,
        val userType: UserType
) : Parcelable {
    //val userGeoLocation: GeoPoint,
    //userGeoLocation = input.user_geo_location ?: GeoPoint(0.0,0.0),
    companion object {
        fun create(input: NetworkUserModel): UserDTO? {
            try {
                val userType = UserType.findValue(input.user_type ?: "")

                val address = makeAddress(
                        input.area ?: "",
                        input.city ?: "",
                        input.house_number ?: "",
                        input.pincode ?: "",
                        input.society_name ?: "",
                        input.street ?: ""
                )



                return UserDTO(
                        id = input.id,
                        area = input.area ?: "",
                        birthDate = input.birth_date,
                        city = input.city ?: "",
                        country = input.country ?: "",
                        createdAt = input.created_at ?: Timestamp.now(),
                        email = input.email
                                ?: throw NullPointerException("Email can not be null in UserDTO"),
                        firstName = input.first_name
                                ?: throw NullPointerException("First name can not be null in UserDTO"),
                        houseNumber = input.house_number ?: "",
                        lastName = input.last_name ?: "",
                        notificationToken = input.notification_token ?: "",
                        phoneNumber = input.phone_number
                                ?: throw NullPointerException("Phone number can not be null in UserDTO"),
                        pincode = input.pincode
                                ?: throw NullPointerException("Pincode can not be null in UserDTO"),
                        profileImageUrl = input.profile_image_url ?: "",
                        customerQRCodeUrl = input.customer_qr_code_image_url ?: "",
                        shopCategory = input.shop_category ?: listOf(),
                        shopName = input.shop_name ?: "",
                        shopSmallInfo = input.shop_small_info ?: "",
                        shopBannerImageUrl = input.shop_banner_image ?: "",
                        geohash = input.geohash ?: if (userType == UserType.CUSTOMER) {
                            ""
                        } else {
                            throw NullPointerException("Geohash can not be null in UserDTO")
                        },
                        latitude = input.latitude ?: if (userType == UserType.CUSTOMER) {
                            0.0
                        } else {
                            throw NullPointerException("Latitude can not be null in UserDTO")
                        },
                        longitude = input.longitude ?: if (userType == UserType.CUSTOMER) {
                            0.0
                        } else {
                            throw NullPointerException("Longitude can not be null in UserDTO")
                        },
                        societyName = input.society_name ?: "",
                        state = input.state ?: "",
                        street = input.street ?: "",
                        updatedAt = input.updated_at ?: Timestamp.now(),
                        address = address,
                        subStatus = SubscriptionStatus.NONE,
                        subId = "",
                        userType = userType
                )
            } catch (e: Exception) {
                Log.e("NST", "UserDTO.create() exception: ${e.message}")
                return null
            }
        }

        private fun makeAddress(
                area: String,
                city: String,
                houseNumber: String,
                pincode: String,
                societyName: String,
                street: String
        ): String {
            val stringBuilder = StringBuilder()
            if (houseNumber.isNotEmpty()) {
                stringBuilder.append(houseNumber)
            }
            if (societyName.isNotEmpty()) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(societyName)
                } else {
                    stringBuilder.append(" - ")
                    stringBuilder.append(societyName)
                }
            }
            if (street.isNotEmpty()) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(street)
                } else {
                    stringBuilder.append("\n")
                    stringBuilder.append(street)
                }
            }
            if (area.isNotEmpty()) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(street)
                } else {
                    stringBuilder.append(", ")
                    stringBuilder.append(area)
                }
            }
            if (pincode.isNotEmpty()) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(pincode)
                } else {
                    stringBuilder.append(" ")
                    stringBuilder.append(pincode)
                }
            }
            if (city.isNotEmpty()) {
                if (stringBuilder.isEmpty()) {
                    stringBuilder.append(pincode)
                } else {
                    stringBuilder.append(" ")
                    stringBuilder.append(city)
                }
            }
            return stringBuilder.toString()
        }
    }
}
