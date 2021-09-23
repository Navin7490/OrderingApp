package com.yorder.shop.model

import android.content.Context
import android.content.SharedPreferences

class LoginDetail(var context: Context){

    val sharedPreferences: SharedPreferences =context.getSharedPreferences("loginDetails",Context.MODE_PRIVATE)
    fun setName(name: String){
        sharedPreferences.edit().putString("Name",name).apply()

    }

    fun setEmail(email: String){
        sharedPreferences.edit().putString("Email",email).apply()

    }

    fun setPhone(phone:String){
        sharedPreferences.edit().putString("Phone",phone).apply()

    }
    fun setUserId(userId:String){
        sharedPreferences.edit().putString("UserId",userId).apply()

    }

    fun setPhoneVerified(phoneVerified:Boolean){
        sharedPreferences.edit().putBoolean("Phone_Verified",phoneVerified).apply()

    }


    fun setImage(image:String){
        sharedPreferences.edit().putString("Image",image).apply()

    }





}