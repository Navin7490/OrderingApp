package com.yorder.shop.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.graphics.drawable.IconCompat
import com.bumptech.glide.Glide
import com.yorder.shop.R
import com.yorder.shop.ui.MainActivity
import com.onesignal.OSNotificationReceivedEvent
import com.onesignal.OneSignal


class NotificationServiceExtension(val context: Context) :
    OneSignal.OSRemoteNotificationReceivedHandler {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun remoteNotificationReceived(
        context: Context?,
        notificationReceivedEvent: OSNotificationReceivedEvent?
    ) {


        val title = notificationReceivedEvent!!.notification.title
        val body = notificationReceivedEvent.notification.body
        val largeIcon = notificationReceivedEvent.notification.bigPicture
        val smallIcon = notificationReceivedEvent.notification.smallIcon
        val sound = notificationReceivedEvent.notification.sound
        val notificationId = notificationReceivedEvent.notification.notificationId.toInt()

        val uri = Uri.parse(sound)
        val imageView = ImageView(context)
        val imageViewSmall = ImageView(context)
        Glide.with(context!!).load(largeIcon).into(imageView)
        Glide.with(context).load(smallIcon).into(imageViewSmall)

        val drawable = imageView.drawable as BitmapDrawable
        val largeIcons = drawable.bitmap

        val drawableSmall = imageViewSmall.drawable as BitmapDrawable
        val bitmapSmall = drawableSmall.bitmap

        val smallIcons = IconCompat.createWithBitmap(bitmapSmall)

        showNotification(notificationId, title, body, largeIcons, smallIcons, uri)
    }

    // create notification
    @RequiresApi(Build.VERSION_CODES.O)
    private fun showNotification(
        notificationId: Int,
        title: String,
        messageBody: String,
        largeIcon: Bitmap,
        smallIcon: IconCompat,
        uri: Uri
    ) {
        val intent = Intent(context, MainActivity::class.java)

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            context, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.packageName + R.raw.notification2)

        val notificationBuilder = NotificationCompat.Builder(context, "channelId")
            .setSmallIcon(smallIcon)
            .setContentTitle(title)
            .setGroup("123")
            .setLargeIcon(largeIcon)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setSound(sound)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)


        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Since android Oreo notification channel is needed.

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "channelId",
                "Channel Name",
                NotificationManager.IMPORTANCE_DEFAULT
            )


            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
            channel.enableVibration(true)
            channel.setSound(sound, attributes)
            channel.enableLights(true)
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(
            notificationId /* ID of notification */,
            notificationBuilder.build()
        )
    }
}