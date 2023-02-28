package org.evidyaloka.student.ui.home

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.network.entity.ErrorData
import org.evidyaloka.core.student.datasource.SessionDataSource
import org.evidyaloka.core.student.datasource.SessionDataSourceFactory
import org.evidyaloka.core.student.model.Generic
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.core.student.model.User
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mainRepository: StudentRepository,
    private val errorObservable:  MutableLiveData<ErrorData>
) : BaseViewModel() {

    var mErrorObservable:  MutableLiveData<ErrorData> = errorObservable
    private var listSession : LiveData<PagedList<Session>> = MutableLiveData<PagedList<Session>>()
    private var studentSourceLiveData = MutableLiveData<SessionDataSource>()
    var sessionDetailsObserver = MutableLiveData<Map<String,Any>>()
    lateinit var config: PagedList.Config
    init {
        config = PagedList.Config.Builder()
            .setPageSize(StudentConst.PAGINATION_COUNT)
            .setEnablePlaceholders(false)
            .build()
    }

    fun getProgressObserable() = mainRepository.progressBarObservable

    fun getSelectedStudent() = mainRepository.getSelectedStudent()

    private val _text = MutableLiveData<String>().apply {
        value = "This is home Fragment"
    }
    val text: LiveData<String> = _text

    fun saveUser(user: User): MutableLiveData<Resource<Generic>> {
        val liveData = MutableLiveData<Resource<Generic>>()
        try {
            mainRepository.saveUser(user)
            liveData.value = Resource.Success(Generic("1", "Successfully saved!"))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    fun getUser(): MutableLiveData<User?> {
        val liveData:MutableLiveData<User?> = MutableLiveData<User?>()
        try {
            mainRepository.getUser().let {
                liveData.value = it
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = null
        }
        return liveData
    }

    fun clearUser(): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        try {
            mainRepository.clearUser()
            liveData.value = true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = false
        }
        return liveData
    }

    fun shouldShowcourseStartDate(): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        try {
            liveData.value = mainRepository.shouldShowcourseStartDate()
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = false
        }
        return liveData
    }

    fun setCourseStartDateDialogShow(shouldShow: Boolean): MutableLiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        try {
            mainRepository.setCourseStartDateDialogShouldShow(shouldShow)
            liveData.value = true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = false
        }
        return liveData
    }

    fun getStudentList() = mainRepository.getStudentList()

    fun getStudents() = liveData {
        val studentListResponse = mainRepository.getStudents()
        emit(studentListResponse)
        if(studentListResponse is Resource.Success){
            studentListResponse?.data?.let {
                mainRepository.setStudentList(it)
            }
        }

    }

    fun setSelectedStudent(student: Student)= liveData {
        mainRepository.setSelectedStudent(student)
        emit(true)
    }

    fun ping() = liveData {
        val response = mainRepository.ping()
        emit(response)
        if(response is Resource.Success){
            response?.data?.let {
                mainRepository.setSettings(it)
            }
        }
    }

    fun getSessions(startDate:Long, endDate:Long, offeringId : Int? = null, classType: Int? = 2):  LiveData<PagedList<Session>> {
        val factory = SessionDataSourceFactory(startDate, endDate, offeringId, classType, mainRepository,viewModelScope)
        sessionDetailsObserver = factory.sessionDetailsObserver()
        studentSourceLiveData = factory.sessionDataSource()
        listSession = LivePagedListBuilder(factory,config).build()
        return listSession
    }

    fun getSessionDetail(sessionId : Int)  = requestWithRetry {
        mainRepository.getSessionDetail(sessionId)
    }

    fun logout(map: HashMap<String, Any>) = requestWithRetry {
        mainRepository.logout(map)
    }

    fun getMissedClasses() = requestWithRetry {
        mainRepository.getMissedClasses()
    }

    fun updateFCMToken(data : HashMap<String, Any>) = requestWithRetry {
        mainRepository.updateFCMToken(data)
    }
}