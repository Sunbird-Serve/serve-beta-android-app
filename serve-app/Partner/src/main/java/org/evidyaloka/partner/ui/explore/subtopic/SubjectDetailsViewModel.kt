package org.evidyaloka.partner.ui.explore.subtopic

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 26-03-2021
 *
 */
@HiltViewModel
class SubjectDetailsViewModel @Inject constructor(
    private val repo: MainRepository
) : BaseViewModel() {

    fun getSubjectDetails(offeringId:Int, courseId : Int) = requestWithRetry {
        repo.getSubjectDetails(offeringId, courseId)
    }

    fun getSettings() = liveData() {
        emit(repo.getSettings())
    }

    fun ping() = liveData {
        emit(repo.ping())
    }
}