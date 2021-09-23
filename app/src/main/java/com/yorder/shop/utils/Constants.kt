package com.yorder.shop.utils

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.RequiresApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.yorder.shop.R
import kotlin.math.roundToInt


enum class UserType(val value: String) {
    CUSTOMER("customer"),
    SELLER("seller"),
    EMPLOYEE("employee");
    companion object {
        private val types = values().associateBy { it.value }


        fun findValue(value: String) =
                types[value] ?: throw NullPointerException("No user type exists for given String value")
    }
}

enum class SubscriptionStatus(val value: String) {
    PENDING("Pending"),
    APPROVED("Approved"),
    CANCELLED("Cancelled"),
}

enum class OrderStatus(val value: String) {
    NEW("New"),
    ONGOING("On Going"),
    READY("Ready"),
    OUT_FOR_DELIVERY("Out For Delivery"),
    COMPLETED("Completed"),
    PAID("Paid"),
    CANCELLED("Cancelled");
    companion object {
        private val types = values().associateBy { it.value }

        fun findValue(value: String) =
                types[value] ?: throw NullPointerException("No Order type exists for given String value")
    }
}

enum class MediaType(val value: String) {
    NONE("None"),
    GALLERY("gallery"),
    CAMERA("camera")
}

class Constants {
    companion object {

//        private const val PREF_NAME = BuildConfig.APPLICATION_ID
//        val sharedPref: SharedPreferences =
//            AppDelegate.applicationContext().getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        fun showAlertWithListeners(
                context: Context,
                title: String,
                message: String,
                positiveTitle: String,
                positiveListener: DialogInterface.OnClickListener?,
                negativeTitle: String,
                negativeListener: DialogInterface.OnClickListener?
        ) {

            val builder = MaterialAlertDialogBuilder(context, R.style.AlertDialogTheme)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setMessage(message)

            if (positiveListener == null && positiveTitle.isBlank()) {
                builder.setPositiveButton("OK") { _, _ -> }
            } else {
                builder.setPositiveButton(positiveTitle, positiveListener)
            }

            negativeListener?.let {
                builder.setNegativeButton(negativeTitle, it)
            }

            builder.show()
        }

        fun getBackgroundWith(
                context: Context,
                fillColor: Int?,
                borderColor: Int?,
                borderWidth: String,
                cornerRadius: String
        ): GradientDrawable {
            val bg = GradientDrawable()
            fillColor?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    bg.setColor(context.resources.getColor(fillColor, null))
                } else {
                    bg.setColor(context.resources.getColor(fillColor))
                }
            }

            bg.cornerRadius = getIntFromDp(cornerRadius, context).toFloat()

            borderColor?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    bg.setStroke(getIntFromDp(borderWidth, context), context.resources.getColor(borderColor, null))
                } else {
                    bg.setStroke(getIntFromDp(borderWidth, context), context.resources.getColor(borderColor))
                }
            }

            return bg
        }

        fun getIntFromDp(value: String, context: Context): Int {
            return TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    value.toFloat(),
                    context.resources.displayMetrics
            ).roundToInt()
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun getDeviceWidth(context: Context): Int {
            /*val displayMetrics = DisplayMetrics()
            val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.widthPixels*/
            val displayMetrics = DisplayMetrics()
            context.display?.getRealMetrics(displayMetrics)
            return displayMetrics.widthPixels
        }

        @RequiresApi(Build.VERSION_CODES.R)
        fun getDeviceHeight(context: Context): Int {
            val displayMetrics = DisplayMetrics()

            //val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
            context.display?.getRealMetrics(displayMetrics)
            //windowManager.defaultDisplay.getMetrics(displayMetrics)
            return displayMetrics.heightPixels
        }

    }
}