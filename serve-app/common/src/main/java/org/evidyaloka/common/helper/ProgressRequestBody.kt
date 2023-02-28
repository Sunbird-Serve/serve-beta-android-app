package org.evidyaloka.common.helper

import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import java.io.InputStream

/**
 * @author Madhankumar
 * created on 06-01-2021
 *
 */
class ProgressRequestBody(data: InputStream,listener: UploadCallbacks) : RequestBody() {

    private var mData: InputStream = data
    private val mPath: String = ""
    private var mListener: UploadCallbacks = listener

    private val DEFAULT_BUFFER_SIZE = 2048

    interface UploadCallbacks {
        fun onProgressUpdate(percentage: Int)
        fun onError()
        fun onFinish()
    }

    /** Returns the Content-Type header for this body.  */
    override fun contentType(): MediaType? {

        // upload only images
        return "*".toMediaTypeOrNull()
    }

    /** Writes the content of this request to `sink`.  */
    override fun writeTo(sink: BufferedSink) {
        val fileSize = mData.available().toLong()
        val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
        var uploaded: Long = 0

        try {
            var read: Int
            while (mData.read(buffer).also { read = it } != -1) {
                mListener.onProgressUpdate((100 * uploaded / fileSize).toInt())
                uploaded += read.toLong()
                sink.write(buffer, 0, read)
            }
        } finally {
            mData.close()
            mListener.onFinish()
        }
    }


    override fun contentLength(): Long {
        return mData.available().toLong()
    }
}