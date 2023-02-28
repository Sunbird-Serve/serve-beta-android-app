package org.evidyaloka.student.worker

import android.content.Context
import androidx.work.*
import org.evidyaloka.core.Constants.StudentConst

object ContentWorkerBuilder {

    fun createOneTimeWorkRequest(appContext: Context, inputData: Data) {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        val workRequest = OneTimeWorkRequestBuilder<ContentViewStatusWorker>()
            .setConstraints(constraints)
            .setInputData(inputData)
            .build()
        WorkManager.getInstance(appContext).enqueue(
            workRequest
        )
    }

    fun createInputWorkData(
        sessionId: Int,
        contentId: Int,
        subtopicId: Int,
        progress: Long
    ): Data = Data.Builder()
        .putInt(StudentConst.SESSION_ID, sessionId)
        .putInt(StudentConst.CONTENT_DETAILS_ID, contentId)
        .putInt(StudentConst.SUBTOPIC_ID, subtopicId)
        .putLong(StudentConst.PROGRESS, progress)
        .build()
}