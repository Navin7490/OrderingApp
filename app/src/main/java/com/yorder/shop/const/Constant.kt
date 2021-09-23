package com.yorder.shop.const

import android.content.Context
import android.content.DialogInterface
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.onesignal.OneSignal

class Constant(val context: Context) {

    fun setupID(){
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE)
        OneSignal.initWithContext(context)
        OneSignal.setAppId(appId)

    }

    companion object{
        private const val appId="6e16afc4-052c-4fcf-ae4f-ab6dce00cf78"

        fun showAlertWithListeners(
                context: Context,
                title: String,
                message: String,
                positiveTitle: String,
                positiveListener: DialogInterface.OnClickListener?,
                negativeTitle: String,
                negativeListener: DialogInterface.OnClickListener?
        ) {

            val builder = MaterialAlertDialogBuilder(context)
            builder.setTitle(title)
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
    }
}
enum class MediaType(val value: String) {
    NONE("None"),
    GALLERY("gallery"),
    CAMERA("camera")
}