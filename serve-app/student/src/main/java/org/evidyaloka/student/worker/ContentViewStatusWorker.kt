package org.evidyaloka.student.worker

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.*
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.coroutineScope
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.repository.StudentRepository

@HiltWorker
class ContentViewStatusWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    val studentRepo: StudentRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = coroutineScope {
        val data = HashMap<String, Any?>()
        inputData.getInt(StudentConst.SESSION_ID, 0).let { data[StudentConst.SESSION_ID] = it }
        inputData.getInt(StudentConst.CONTENT_DETAILS_ID, 0)
            .let { data[StudentConst.CONTENT_DETAILS_ID] = it }
        inputData.getInt(StudentConst.SUBTOPIC_ID, 0)
            .let { data[StudentConst.SUBTOPIC_ID] = it }
        inputData.getLong(StudentConst.PROGRESS, 0).let { data[StudentConst.PROGRESS] = it }
        val result = studentRepo.updateViewStatus(data)
        when (result) {
            is Resource.Success -> {
                Log.e("ContentViewStatusWorker","Successful")
                Result.success()
            }
            else -> {
                Log.e("ContentViewStatusWorker","Failed")
                Result.failure()
            }
        }
    }

}