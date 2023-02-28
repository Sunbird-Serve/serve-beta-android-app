package org.evidyaloka.student.ui.rtc

import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.datasource.doubt.DoubtDataSource
import org.evidyaloka.core.student.datasource.doubt.DoubtDataSourceFactory
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class DoubtViewModel @Inject constructor(
    private val repo: StudentRepository
) : BaseViewModel() {

    private var listDoubt : LiveData<PagedList<Doubt>> = MutableLiveData<PagedList<Doubt>>()
    private var doubtSourceLiveData = MutableLiveData<DoubtDataSource>()
    var doubtDetailsObserver = MutableLiveData<Int>()
    lateinit var config: PagedList.Config
    init {
        config = PagedList.Config.Builder()
            .setPageSize(StudentConst.DOUBT_PAGINATION_COUNT)
            .setEnablePlaceholders(false)
            .build()
    }

    fun getDoubts(doubtId:Int? = null, offeringId:Int? = null, topicId : Int? = null, subtopicId: Int?= null):  LiveData<PagedList<Doubt>> {
        val factory = DoubtDataSourceFactory(doubtId, offeringId, topicId, subtopicId, repo,viewModelScope)
        doubtDetailsObserver = factory.doubtDetailObserver()
        doubtSourceLiveData = factory.doubtDataSource()
        listDoubt = LivePagedListBuilder(factory,config).build()
        return listDoubt
    }

    fun getStudentCourses() = requestWithRetry {
        repo.getStudentOfferings()
    }

    fun getSelectedStudent() = repo.getSelectedStudent()

}