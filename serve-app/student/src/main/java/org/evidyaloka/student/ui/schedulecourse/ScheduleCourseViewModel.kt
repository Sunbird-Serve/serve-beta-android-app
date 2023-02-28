package org.evidyaloka.student.ui.schedulecourse

import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.model.Course
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class ScheduleCourseViewModel @Inject constructor(
    private val repo: StudentRepository
) : BaseViewModel() {

    fun getCourses() = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO) {
        var schoolOfferingResponse: Resource<List<Course>?>? = null
        var studentOfferingResponse: Resource<List<Course>?>? = null
        viewModelScope.async {
            schoolOfferingResponse = repo.getSchoolOfferings()
            studentOfferingResponse = repo.getStudentOfferings()
        }.await()
        var list: MutableList<Course> = mutableListOf()
        if (schoolOfferingResponse is Resource.Success && studentOfferingResponse is Resource.Success) {
            (schoolOfferingResponse as Resource.Success<List<Course>?>).data?.forEach { courseOffering ->
                (studentOfferingResponse as Resource.Success<List<Course>?>).data?.find { it.id == courseOffering.id }?.let {
                    courseOffering.isSelected = true
                }
                list.add(courseOffering)
            }
            emit(Resource.Success(list))
        } else {
            emit(schoolOfferingResponse)
        }
    }

    fun updateOffering(data: Map<String, Any>) = requestWithRetry {
        repo.updateOffering(data)
    }

    fun getSelectedStudent() = liveData {
        emit(repo._selectedStudent)
    }

    fun schedulecourse(data: Map<String, Any>) = requestWithRetry {
        repo.schedulecourse(data)
    }

    fun getSettings() = liveData() {
        emit(repo.getSettings())
    }

    fun updateStudentStatus(status:String): LiveData<Boolean> {
        var statusResponse: MutableLiveData<Boolean> = MutableLiveData()
        try {
            statusResponse.postValue(repo.updateStudentOnboardingStatus(status))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return statusResponse
    }

    fun setCourseStartDateDialogShouldShow(shouldShow: Boolean): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        try {
            repo.setCourseStartDateDialogShouldShow(shouldShow)
            liveData.value = true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = false
        }
        return liveData
    }
}