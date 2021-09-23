package com.yorder.shop.utils

import android.app.Application
import com.yorder.shop.model.ModelDiscover
import com.yorder.shop.model.UserDTO


class AppDelegate: Application() {
    var userDTO: UserDTO? = null
    var selectedCategory = ModelDiscover("1", "All")

    init {
        instance = this
    }

    companion object {
        private var instance: AppDelegate? = null
        fun applicationContext(): AppDelegate {
            return instance ?: AppDelegate().also { instance = it }
        }
    }

}