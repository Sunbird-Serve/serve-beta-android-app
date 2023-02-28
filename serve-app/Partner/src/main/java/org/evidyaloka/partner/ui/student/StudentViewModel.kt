package org.evidyaloka.partner.ui.student

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.core.repository.StudentsDataSource
import org.evidyaloka.core.repository.StudentsDataSourceFactory
import org.evidyaloka.partner.ui.BaseViewModel
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class StudentViewModel @Inject constructor(
        private val mainRepository: MainRepository
) : BaseViewModel() {

    private var _progressPercentage = MutableLiveData<Int>()
    val profileObserver: MutableLiveData<Resource<User?>> by lazy { MutableLiveData<Resource<User?>>().also { getProfile() } }
    private var listStudents : LiveData<PagedList<Student>> = MutableLiveData<PagedList<Student>>()
    private var mutableLiveData = MutableLiveData<StudentsDataSource>()

    lateinit var config: PagedList.Config
    init {
        config = PagedList.Config.Builder()
            .setPageSize(PartnerConst.PAGINATION_COUNT)
            .setEnablePlaceholders(false)
            .build()
    }
    fun getProfile() = viewModelScope.launch {
        profileObserver.value = mainRepository.getProfileData()
    }

    fun getCourseProvider(courseProviderCode: String? = null) = liveData {
        emit(mainRepository.getCourseProviders(courseProviderCode))
    }

    var studentCountObserver = MutableLiveData<Int>()

    fun getEnrolledStudents(userType: String,courseProviderId: Int?, digitalSchoolId: Int, grade: Int?, offeringId: Int?, academicYearId: Int?) : LiveData<PagedList<Student>> {
//        emit(Resource.Loading())
//        emit(mainRepository.getEnrolledStudents(userType, courseProviderId, digitalSchoolId, grade, offeringId, academicYearId))
        val factory = StudentsDataSourceFactory(userType,courseProviderId,digitalSchoolId,grade,offeringId,academicYearId,mainRepository,viewModelScope)
        studentCountObserver = factory.studentCountObserver
        mutableLiveData = factory.studentsLiveDataSource()
        listStudents = LivePagedListBuilder(factory,config).build()
        return listStudents
    }

    fun enrolledStudents(data: Map<String, Any>) = liveData {
        emit(mainRepository.enrolledStudents(data))
    }
    fun updateStudents(studentId:Int, digitalSchoolId : Int, data: Map<String, Any>) = liveData {
        emit(mainRepository.updateStudents(studentId,digitalSchoolId,data))
    }
    fun getStudentDetails(studentId:Int,digitalSchoolId:Int) = liveData {
        emit(mainRepository.getStudentDetails(studentId,digitalSchoolId))
    }

    fun getLanguages() = liveData {
        emit(mainRepository.getLanguages())
    }

    fun uploadFile( docType: Int, format:Int, filename:String, inputStream: InputStream) = liveData{
        emit(mainRepository.uploadFile(docType,format,filename,inputStream) { progress: Int -> setProgress(progress)})
    }

    private fun setProgress(progress: Int) {
        _progressPercentage.postValue(progress)
    }

    fun uploadProgress(): LiveData<Int> = _progressPercentage
    // fun getGrades() = liveData {
    //emit(Resource.Loading())
    //emit(mainRepository.getGrades())
    // }

    fun getCourseOfferings(courseProviderId: Int?, digitalSchoolId: Int, grade: Int?, academicYearId: Int?, languageId: Int? = null) = liveData {
        emit(mainRepository.getCourseOfferings(courseProviderId,digitalSchoolId,grade,academicYearId, languageId))
    }

    fun getCourse(grade : Int, courseProviderCode : String) = liveData {
        emit(mainRepository.getCourse(grade, courseProviderCode))
    }

    fun getAcademicYear(cpId : Int) = liveData {
        emit(mainRepository.getAcademicYear(cpId))
    }

    fun getUser(): MutableLiveData<Resource<User>> {
        val liveData = MutableLiveData<Resource<User>>()
        try {
            mainRepository.getUser()!!.let {
                liveData.value = Resource.Success(it)
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
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
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    /*
     * Add Course
     */

    fun addCourse(data: Map<String, Any>) = liveData{
        emit(mainRepository.addCourse(data))
    }

    fun getSettings(): MutableLiveData<Resource<Settings>> {
        val liveData = MutableLiveData<Resource<Settings>>()
        try {
            mainRepository.getSettings()!!.let {
                liveData.value = Resource.Success(it)
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    fun getDigitalSchool( digitalSchoolId:Int) = requestWithRetry{
        mainRepository.getDigitalSchool(digitalSchoolId)
    }

}