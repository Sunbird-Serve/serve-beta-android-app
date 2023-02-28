package org.evidyaloka.student.download

import android.app.DownloadManager
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.StatFs
import org.evidyaloka.common.helper.getMimeType
import org.evidyaloka.core.model.rtc.ContentAttributes
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLConnection


open class ContentDownload(val context: Context) {

    val INSUFFICIENT_SPACE = 1
    val FAILED_TO_DOWNLOAD = 2
    val NOT_ABLE_ACCESS_FILE_LOCATION = 3

    fun download(
        content: ContentAttributes,
        onSuccess: (downloadID: Long, uri: Uri) -> Unit,
        onFailed: (error: Int) -> Unit
    ) {
        getRemoteFileSize(URL(content.url))?.let {
            val remoteFileSize = it / (1024 * 1024)
            if ((checkFreeSpaceInMB() - 300) < remoteFileSize) { //300MB reserved for android
                onFailed(INSUFFICIENT_SPACE)
                return
            }
            createFile(content.id.toString())?.let { file ->
                val downloadId: Long? = initDownload(content, file)
                downloadId?.let { onSuccess(downloadId, Uri.fromFile(file)) } ?: onFailed(
                    FAILED_TO_DOWNLOAD
                )
            } ?: onFailed(NOT_ABLE_ACCESS_FILE_LOCATION)
        }
    }

    fun buildRequest(url: String?, file: File, title: String?): DownloadManager.Request {
        val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE) // Visibility of the download Notification
            .setDestinationUri(Uri.fromFile(file)) // Uri of the destination file
            .setTitle(title) // Title of the Download Notification
            .setDescription("Downloading") // Description of the Download Notification
            .setAllowedOverMetered(true) // Set if download is allowed on Mobile network
            .setAllowedOverRoaming(true) // Set if download is allowed on roaming network
            .setVisibleInDownloadsUi(false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            request.setRequiresCharging(false)
        } // Set if charging is required to begin the download
        return request
    }

    fun initDownload(content: ContentAttributes, file: File): Long? {

        val downloadManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
        return downloadManager?.enqueue(
            buildRequest(
                content.url,
                file,
                content.title
            )
        ) // enqueue puts the download request in the queue.

    }

    fun checkDownloadStatus(downloadId: Long): DownloadStat? {
        val mManager = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        val query = DownloadManager.Query()
        query.setFilterById(downloadId)
        val cursor: Cursor = mManager.query(query)
        if (cursor.moveToFirst()) {
            return DownloadStat(
                id = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_ID)),
                status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)),
                bytesSoFar = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR)),
                lastModifiedTimestamp = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LAST_MODIFIED_TIMESTAMP)),
                localUri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)),
                reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON)),
                totalSize = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES)),
                uri = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI)),
                mimeType = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_URI))
                    .getMimeType()
            )
        }
        return null
    }

    private fun createFile(fileName: String): File? {
        return try {
            val filesDir: File = File(
                context.getExternalFilesDir(null),
                "Download/"
            )

            if (!filesDir.exists()) filesDir.mkdirs()
            val file = File(filesDir, fileName)
            if (file.isFile && file.exists())
                file.delete()
            file
        } catch (e: Exception) {
            null
        }
    }

    fun moveToAppFolder(url: String, filename: String): String? {

        val sourceFile = File(Uri.parse(url).path)
        val inputStream = FileInputStream(sourceFile)
        try {
            val destUrl = File(context.filesDir, filename)
            if (destUrl.exists()) destUrl.delete()
            context.openFileOutput(destUrl.name, Context.MODE_PRIVATE).use {
                val buf = ByteArray(1024)
                var len: Int
                while (inputStream.read(buf).also { len = it } > 0) {
                    it.write(buf, 0, len)
                }
            }
            if (sourceFile.exists()) sourceFile.delete()
            return Uri.fromFile(File(context.filesDir, filename)).toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            inputStream.close()
        }
        return null
    }

    fun deleteFile(uri: String): Boolean {
        return try {
            val file = File(Uri.parse(uri).path)
            if (file.isFile && file.exists()) file.delete() else false
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }


    private fun getRemoteFileSize(url: URL): Int {
        var conn: URLConnection? = null
        return try {
            conn = url.openConnection()
            if (conn is HttpURLConnection) {
                (conn as HttpURLConnection?)?.requestMethod = "HEAD"
            }
            conn.getInputStream()
            conn.getContentLength()
        } catch (e: IOException) {
            e.printStackTrace()
            0
        } finally {
            if (conn is HttpURLConnection) {
                (conn as HttpURLConnection?)?.disconnect()
            }
        }
    }

    private fun checkFreeSpaceInMB(): Long {
        val stat = StatFs(context.getExternalFilesDir(null)?.path)
        return (stat.blockSizeLong * stat.availableBlocksLong)/ (1024 * 1024)
    }
}