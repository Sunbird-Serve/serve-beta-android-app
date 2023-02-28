package org.evidyaloka.partner.ui.promotion

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.core.repository.StudentsDataSource
import org.evidyaloka.core.repository.StudentsDataSourceFactory
import org.evidyaloka.core.repository.StudentsPromotionDataSource
import org.evidyaloka.core.repository.StudentsPromotionDataSourceFactory
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class StudentsPromotionViewModel  @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel(){

    var studentCountObserver = MutableLiveData<Int>()
    private var listStudents : LiveData<PagedList<Student>> = MutableLiveData<PagedList<Student>>()
    private var mutableLiveData = MutableLiveData<StudentsPromotionDataSource>()

    private var config: PagedList.Config = PagedList.Config.Builder()
        .setPageSize(PartnerConst.PAGINATION_COUNT)
        .setEnablePlaceholders(false)
        .build()

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

    fun getAcademicYear(cpId : Int) = liveData {
        emit(mainRepository.getAcademicYear(cpId))
    }

    fun getStudentsForPormotion(userType: String,courseProviderId: Int?, digitalSchoolId: Int, grade: Int?, offeringId: Int?, filter: String?) : LiveData<PagedList<Student>> {
//        emit(Resource.Loading())
//        emit(mainRepository.getEnrolledStudents(userType, courseProviderId, digitalSchoolId, grade, offeringId, academicYearId))
        val factory = StudentsPromotionDataSourceFactory(userType,digitalSchoolId,grade,offeringId,courseProviderId,filter,mainRepository,viewModelScope)
        studentCountObserver = factory.studentCountObserver
        mutableLiveData = factory.studentsLiveDataSource()
        listStudents = LivePagedListBuilder(factory,config).build()
        return listStudents
    }
}