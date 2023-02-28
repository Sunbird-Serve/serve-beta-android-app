package org.evidyaloka.partner.ui.course

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CourseViewModel @Inject constructor(
        private val mainRepository: MainRepository
) : BaseViewModel() {

    private val TAG ="CourseViewModel"

    val profileObserver: MutableLiveData<Resource<User?>> by lazy { MutableLiveData<Resource<User?>>().also { getProfile() } }

    fun getProfile() = viewModelScope.launch {
        profileObserver.value = mainRepository.getProfileData()
    }

    fun getCourseProvider(courseProviderCode: String? = null) = requestWithRetry {
        mainRepository.getCourseProviders(courseProviderCode)
    }

    fun getLanguages() = requestWithRetry {
        mainRepository.getLanguages()
    }

    fun getCourseOfferings(courseProviderId: Int? = null, digitalSchoolId: Int, grade: Int? = null, academicYearId
    : Int? = null) = requestWithRetry {
        mainRepository.getCourseOfferings(courseProviderId, digitalSchoolId, grade, academicYearId)
    }

    fun getCourse(grade: Int, courseProviderCode: String,languageId: Int? = null) = requestWithRetry {
        mainRepository.getCourse(grade, courseProviderCode,languageId)
    }

    fun getAcademicYear(cpId: Int) = requestWithRetry {
        mainRepository.getAcademicYear(cpId)
    }

    fun getUser(): MutableLiveData<Resource<User>> {
        val liveData = MutableLiveData<Resource<User>>()
        try {
            mainRepository.getUser()!!.let {
                liveData.value = Resource.Success(it)
            }

        } catch (e: Exception) {
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    fun saveUser(user: User): MutableLiveData<Resource<Generic>> {
        val liveData = MutableLiveData<Resource<Generic>>()
        try {
            mainRepository.saveUser(user)
            liveData.value = Resource.Success(Generic("1", "Successfully saved!"))
        } catch (e: Exception) {
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    /*
     * Add Course
     */

    fun addCourse(data: Map<String, Any>) = requestWithRetry {
        mainRepository.addCourse(data)
    }

    fun checkCourse(courseId:Int, digitalSchoolId : Int) = requestWithRetry {
        mainRepository.checkCourse(courseId, digitalSchoolId)
    }

    fun getSettings(): MutableLiveData<Resource<Settings>> {
        val liveData = MutableLiveData<Resource<Settings>>()
        try {
            mainRepository.getSettings()!!.let {
                liveData.value = Resource.Success(it)
            }

        } catch (e: Exception) {
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    fun saveSettings(settings: Settings): MutableLiveData<Resource<Generic>?> {
        val liveData = MutableLiveData<Resource<Generic>?>()
        try {
            mainRepository.saveSettings(settings)
            liveData.value = Resource.Success(Generic("1", "Successfully saved!"))
        } catch (e: Exception) {
            liveData.value = null
        }
        return liveData
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "onCleared: ")
    }


}