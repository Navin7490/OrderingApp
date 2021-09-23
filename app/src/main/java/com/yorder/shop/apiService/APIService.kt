package com.yorder.shop.apiService

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=874842667055"
    )
    @POST("/fcm/send")
    fun sendNotification(@Body user:String ): Call<String>?


}