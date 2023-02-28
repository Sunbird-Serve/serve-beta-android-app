package org.evidyaloka.student.download

import android.app.DownloadManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.evidyaloka.common.extension.showNotification
import org.evidyaloka.common.helper.HiltBroadcastReceiver
import org.evidyaloka.common.util.PdfReaderActivity
import org.evidyaloka.common.util.Utils
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.model.SubTopic
import org.evidyaloka.core.model.rtc.ContentAttributes
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.R
import org.evidyaloka.student.ui.StudentHomeActivity
import org.evidyaloka.student.ui.rtc.PDFreaderImp
import org.evidyaloka.student.ui.rtc.RtcViewFragmentArgs
import javax.inject.Inject

@AndroidEntryPoint
class DownloadCompleteReceiver : HiltBroadcastReceiver() {
    @Inject
    lateinit var mainRepository: StudentRepository

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)
        val downId = intent!!.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        when (intent.action) {
            DownloadManager.ACTION_DOWNLOAD_COMPLETE -> {
                if (context != null) {
                    GlobalScope.launch(Dispatchers.IO) {
                        ContentDownload(context).checkDownloadStatus(downId)?.let {
                            it.id?.toLong()?.let { id ->
                                mainRepository.updateDownloadStatus(
                                    id,
                                    it.status,
                                    it.reason.toString()
                                )
                                var content = mainRepository.downloadContentDetailsById(id)
                                when (it.status) {
                                    DownloadManager.STATUS_SUCCESSFUL -> {
                                        content?.localUrl?.let {
                                            val url = ContentDownload(context).moveToAppFolder(
                                                it,
                                                content?.contentId.toString()
                                            )
                                            mainRepository.updateUrl(id, url)
                                            content?.localUrl = url
                                            updateContentDownloadStatus(
                                                context,
                                                content,
                                                StudentConst.DownloadStatus.Complete
                                            )
                                        }
                                        content?.title?.let {
                                            showNotification(
                                                context,
                                                context.getString(R.string.downloaded),
                                                String.format(
                                                    context.getString(R.string.content_can_be_viewed_offline),
                                                    it
                                                ),
                                                content
                                            )
                                        }
                                    }
                                    DownloadManager.STATUS_FAILED -> {
                                        it.reason?.let { it1 ->
                                            showNotification(
                                                context,
                                                context.getString(R.string.error),
                                                handleError(it1),
                                                null
                                            )
                                        }
                                        content?.id?.let { contentId ->
                                            mainRepository.deleteByContentId(
                                                contentId
                                            )
                                        }

                                    }
                                    else -> {
                                    }
                                }
                            }

                        }

                    }
                }
            }
            else -> {
            }
        }
    }

    private fun handleError(reason: Int): String {
        return when (reason) {
            DownloadManager.ERROR_CANNOT_RESUME -> "Cannot resume the download."
            DownloadManager.ERROR_DEVICE_NOT_FOUND -> "Device not found error."
            DownloadManager.ERROR_FILE_ALREADY_EXISTS -> "Content already exists in the system."
            DownloadManager.ERROR_FILE_ERROR -> "Invalid file error. Not able to proceed further."
            DownloadManager.ERROR_TOO_MANY_REDIRECTS -> "Too many redirect from the url."
            DownloadManager.ERROR_UNHANDLED_HTTP_CODE,
            DownloadManager.ERROR_UNKNOWN,
            DownloadManager.ERROR_HTTP_DATA_ERROR -> "Server error.Not able to proceed further."
            DownloadManager.ERROR_INSUFFICIENT_SPACE -> "Insufficient space to download file."
            else -> "Unknown error.Not able to proceed further."
        }
    }

    private fun showNotification(
        context: Context,
        title: String,
        messageBody: String,
        content: CourseContentEntity?
    ) {
        val channelId = context.getString(R.string.download_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val pendingIntent = if (content?.contentType == "pdf") {
            getPDFPendingIntent(context,content)
        } else {
            getRTCPendingIntent(context,content)
        }
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_downloading)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(messageBody)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)

        context.showNotification(1, notificationBuilder, channelId)
    }

    suspend fun updateContentDownloadStatus(
        context: Context,
        content: CourseContentEntity,
        status: StudentConst.DownloadStatus
    ) {
        try {
            val data = HashMap<String, Any?>()
            data[StudentConst.CONTENT_DETAILS_ID] = content?.contentId
            data[StudentConst.SESSION_ID] = content?.sessionId
            data[StudentConst.SUBTOPIC_ID] = content?.subtopicId
            data[StudentConst.DEVICE_ID] = Utils.getDeviceId(context)
            data[StudentConst.DOWNLOAD_STATUS] = status.value
            mainRepository.updateContentDownloadStatus(data)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getPDFPendingIntent(
        context: Context,
        content: CourseContentEntity
    ): PendingIntent? {
        return buildDeepLinkPendingIntent(
            context,
            R.id.pdfReaderActivity,
            buildDocViewBundle(
                context,
                ContentAttributes(
                    id = content?.contentId ?: 0,
                    subtopicId = content?.subtopicId ?: 0,
                    url = content?.url,
                    title = content?.title,
                    subtitle = content?.subtitle,
                    localUrl = content?.localUrl,
                    subTopicTitle = content?.subTopicName,
                    contentHost = "s3"
                ),
                content
            )
        )
    }

    private fun getRTCPendingIntent(
        context: Context,
        content: CourseContentEntity?
    ): PendingIntent? {
        return buildDeepLinkPendingIntent(
            context,
            R.id.rtcnavigation,
            buildRtfcArgs(content)?.toBundle()
        )
    }

    private fun buildDeepLinkPendingIntent(
        context: Context,
        destId: Int,
        args: Bundle?
    ): PendingIntent? {
        return NavDeepLinkBuilder(context)
            .setGraph(R.navigation.mobile_navigation)
            .setDestination(destId)
            .setComponentName(StudentHomeActivity::class.java)
            .setArguments(args)
            .createPendingIntent()
    }

    private fun buildRtfcArgs(content: CourseContentEntity?): RtcViewFragmentArgs? {
        return content?.let{
            with(content) {
                return RtcViewFragmentArgs(
                    Session(
                        id = sessionId ?: 0,
                        topicId = topicId ?: 0,
                        topicName = topicName ?: "",
                        offeringId = offeringId ?: 0,
                        videoLink = "file:///data/user/0/org.evidyaloka/files/477"
                    ),
                    timetableId = timetableId ?: 0,
                    subTopic = SubTopic(subtopicId ?: 0, subTopicName ?: ""),
                    offlineContent = content,
                    isDeepLink = true
                )
            }
        }?: null
    }

    private fun buildDocViewBundle(context: Context,contentAttributes: ContentAttributes,content: CourseContentEntity?): Bundle {
        var title = contentAttributes.title?.let { it } ?: ""
        var url = contentAttributes.url?.let { it } ?: ""
        var contentType = contentAttributes.contentType?.let { it } ?: ""
        var bundle = Bundle()
        bundle.putString(StudentConst.WEB_URL, url)
        contentAttributes?.localUrl?.let {
            bundle.putString(StudentConst.LOCAL_WEB_URL, it)
        }
        bundle.putString(StudentConst.PAGE_TITLE, title)
        bundle.putString(StudentConst.CONTENT_TYPE, contentType)
        bundle.putParcelable(StudentConst.ACTIVITY_CONTENT, contentAttributes)
        bundle.putParcelable(StudentConst.OFFLINE_CONTENT, content)
        bundle.putBoolean(StudentConst.IS_DEEP_LINK, true)
        var intent: Intent = Intent(context, PDFreaderImp::class.java)
        intent.putExtras(bundle)
        return bundle
//        startActivity(intent)
//        resultLauncher.launch(intent)
    }
}