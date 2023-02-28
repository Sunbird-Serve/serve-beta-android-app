package org.evidyaloka.student.ui.rtc

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.activity.viewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.evidyaloka.common.receiver.InternetConnectionReceiver
import org.evidyaloka.common.util.PdfReaderActivity
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.model.School
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.student.worker.ContentViewStatusWorker
import org.evidyaloka.student.worker.ContentWorkerBuilder

@AndroidEntryPoint
class PDFreaderImp : PdfReaderActivity() {
    private val viewModel: RtcViewModel by viewModels()
    private var isInternetConnected: Boolean = false
    val offlineContent =
        intent?.extras?.getParcelable<CourseContentEntity>(StudentConst.OFFLINE_CONTENT)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val isDeeplink = intent?.extras?.getBoolean(StudentConst.IS_DEEP_LINK) == true
        if (isDeeplink) {
            offlineContent?.studentId?.let { id ->
                viewModel.getStudentById(id).observe(this,
                    Observer { student ->
                        viewModel.setSelectedStudent(
                            Student(
                                id = student.id ?: 0,
                                name = student.name ?: "",
                                grade = student.grade ?: "",
                                schools = School(
                                    id = student.schoolId,
                                    name = student.schoolName,
                                    logoUrl = student.logoUrl
                                )
                            )
                        ).observe(this, Observer { })
                    })
            }
        }

        binding.buttonDone.setOnClickListener {
            checkNetworkConnection()
        }
    }

    private fun checkNetworkConnection() {
        InternetConnectionReceiver(this@PDFreaderImp) { isConnected ->
            lifecycleScope.launch(Dispatchers.Main) {
                if(!isConnected){
                    val data = ContentWorkerBuilder.createInputWorkData(
                        offlineContent?.sessionId ?: 0,
                        offlineContent?.contentId ?: 0,
                        offlineContent?.subtopicId ?: 0,
                        100
                    )
                    ContentWorkerBuilder.createOneTimeWorkRequest(this@PDFreaderImp.applicationContext,data)
                }
                val resultIntent = Intent()
                intent?.extras?.let { bundle -> resultIntent.putExtras(bundle) }
                setResult(
                    RESULT_OK,
                    resultIntent
                ) // You can also send result without any data using setResult(int resultCode)
                finish()
            }
        }
    }
}