package org.evidyaloka.common.extension

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.annotation.StringRes
import androidx.core.app.NotificationCompat
import org.evidyaloka.common.R
import java.util.*

fun Context.getStringByLocale(
    @StringRes stringRes: Int,
    locale: Locale,
    vararg formatArgs: Any
): String {
    val configuration = Configuration(resources.configuration)
    configuration.setLocale(locale)
    return createConfigurationContext(configuration).resources.getString(stringRes, *formatArgs)
}

fun Context.showNotification(
    id:Int,
    notificationBuilder:NotificationCompat.Builder,
    channelId:String = getString(R.string.app_notification_channel_id),
    channelName: String = "evidyaloka channel"
) {

    val notificationManager =
        getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Since android Oreo notification channel is needed.
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    notificationManager.notify(id /* ID of notification */, notificationBuilder.build())
}