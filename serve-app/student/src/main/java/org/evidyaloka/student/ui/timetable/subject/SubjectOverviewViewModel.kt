package org.evidyaloka.student.ui.timetable.subject

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 26-03-2021
 *
 */
@HiltViewModel
class SubjectOverviewViewModel @Inject constructor(
    private val repo: StudentRepository
) : BaseViewModel() {

    fun getSubjectDetails(offeringId:Int, courseId : Int) = requestWithRetry {
        repo.getSubjectDetails(offeringId, courseId)
    }

    fun getSessionDetails(offeringId: Int, topicId: Int, subTopicId: Int) = requestWithRetry {
        repo.getSessionDetails(offeringId, topicId, subTopicId)
    }
}