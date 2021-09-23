package com.yorder.shop.keyboard

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager

class HideKeyBoard(val context: Context) {


    fun isKeyboardHide(v:View){
        val inputMethodManager: InputMethodManager =
            context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(v.windowToken, 0)
    }

}