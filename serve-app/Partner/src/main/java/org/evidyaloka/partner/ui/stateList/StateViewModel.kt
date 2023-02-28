package org.evidyaloka.partner.ui.stateList

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.partner.datasource.PincodeDataSource
import org.evidyaloka.core.partner.datasource.PincodeDataSourceFactory
import org.evidyaloka.core.partner.model.Pincode
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class StateViewModel @Inject constructor(
        private val mainRepository: MainRepository
) : BaseViewModel() {

    private var listPincode : LiveData<PagedList<Pincode>> = MutableLiveData<PagedList<Pincode>>()
    private var mutableLiveData = MutableLiveData<PincodeDataSource>()

    lateinit var config: PagedList.Config
    init {
        config = PagedList.Config.Builder()
            .setPageSize(PartnerConst.PAGINATION_COUNT)
            .setEnablePlaceholders(false)
            .build()
    }

    fun getStateList() = requestWithRetry {
        mainRepository.getStateList()
    }

    fun getUserSchoolList() = requestWithRetry {
        mainRepository.getUserSchoolList(PartnerConst.RoleType.DSP)
    }

    fun getStatePincode(stateId: Int, searchQuery:String? = null ) : LiveData<PagedList<Pincode>> {
//        emit(Resource.Loading())
//        emit(mainRepository.getEnrolledStudents(userType, courseProviderId, digitalSchoolId, grade, offeringId, academicYearId))
        val factory = PincodeDataSourceFactory(stateId,searchQuery,mainRepository,viewModelScope)
        mutableLiveData = factory.pincodeLiveDataSource()
        listPincode = LivePagedListBuilder(factory,config).build()
        return listPincode
    }

    fun saveState(data: Map<String, Any>) = requestWithRetry {
        mainRepository.saveState(data)
    }

    fun deleteLocation(data: Map<String, Any>) = requestWithRetry {
        mainRepository.deleteLocation(data)
    }

    fun getDigitalSchool( digitalSchoolId:Int) = requestWithRetry{
        mainRepository.getDigitalSchool(digitalSchoolId)
    }
}