package org.evidyaloka.main.firebase

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import dagger.hilt.android.AndroidEntryPoint
import org.evidyaloka.R
import org.evidyaloka.common.extension.showNotification
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.main.ui.SplashActivity
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class FCMInstanceIdService() : FirebaseMessagingService() {

    @Inject
    lateinit var myRepository: StudentRepository

    private val CLASS_REMINDER = "classReminder"
    private val SCHOOL_APPROVED = "schoolApproved"
    private val DOUBT_RESPONDED = "studentDoubtResponded"
    private val PARTNER_APPROVED = "partnerApproved"

    val TAG = "FCMInstanceIdService"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        if (remoteMessage.notification != null) {
            remoteMessage.notification?.let {
                try {
                    Log.d(TAG, "Message Notification Body: ${it.body}")
                    sendNotification(Random.nextInt(), it.title!!, it.body!!)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } else {
            remoteMessage.data?.let {
                try {
                    handleDataNotification(it)
                    Log.d(TAG, "Message payload : ${it.toString()}")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

    }

    private fun handleDataNotification(data: MutableMap<String, String>) {
        val jsonObject: JsonObject = JsonParser().parse(data["params"]).asJsonObject
        val id = data["id"]?.toInt() ?: 0
        var message: String? = null
        when (data["type"]) {
            CLASS_REMINDER -> {
                var subjects = mutableListOf<String>()
                jsonObject.getAsJsonArray("subjectNames").forEach {
                    if (!subjects.contains(it.asString))
                        subjects.add(it.asString)
                }
                var list = if (subjects.size > 1) {
                    String.format(
                        getString(R.string.notification_and),
                        subjects.take(subjects.size - 1).joinToString(),
                        subjects.last()
                    )
                } else {
                    String.format(
                        getString(R.string.notification_class),
                        subjects[0]
                    )
                }
                message = String.format(
                    getString(R.string.notification_1),
                    jsonObject.get("name").asString,
                    list
                )
            }
            SCHOOL_APPROVED -> {
                message = String.format(
                    getString(R.string.notification_schoolApproved),
                    jsonObject.get("userName").asString,
                    jsonObject.get("schoolName").asString
                )
            }
            DOUBT_RESPONDED -> {
                message = String.format(
                    getString(R.string.notification_doubtResponded),
                    jsonObject.get("name").asString,
                    jsonObject.get("subTopicName").asString,
                    jsonObject.get("subjectName").asString
                )
            }
            PARTNER_APPROVED -> {
                message = String.format(
                    getString(R.string.notification_partnerApproved),
                    jsonObject.get("partnerName").asString,
                    jsonObject.get("orgName").asString
                )
            }
        }
        message?.let { sendNotification(id, "Evidyaloka", it) }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "Message Notification token: ${token}")
    }

    private fun sendNotification(id: Int, title: String, messageBody: String) {
        val intent = Intent(this, SplashActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )

        val channelId = getString(R.string.app_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_logo_no_titile)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        showNotification(id, notificationBuilder, channelId)
    }

}