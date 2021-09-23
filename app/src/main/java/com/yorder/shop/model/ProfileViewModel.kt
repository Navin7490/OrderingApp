package com.yorder.shop.model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ProfileViewModel: ViewModel() {
    val isEditing = MutableLiveData<Boolean>(false)

    fun setEditing(flag: Boolean) {
        isEditing.value = flag
    }
}