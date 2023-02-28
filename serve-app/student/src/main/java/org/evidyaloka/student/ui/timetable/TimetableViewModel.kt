package org.evidyaloka.student.ui.timetable

import androidx.lifecycle.*
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.datasource.SessionDataSource
import org.evidyaloka.core.student.datasource.SessionDataSourceFactory
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.student.repository.StudentRepository
import org.evidyaloka.student.ui.BaseViewModel
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 26-03-2021
 *
 */
@HiltViewModel
class TimetableViewModel @Inject constructor(
    private val repo: StudentRepository
) : BaseViewModel() {

    private var listSession: LiveData<PagedList<Session>> = MutableLiveData<PagedList<Session>>()
    private var studentSourceLiveData = MutableLiveData<SessionDataSource>()
    var sessionDetailsObserver = MutableLiveData<Map<String, Any>>()
    lateinit var config: PagedList.Config
    init {
        config = PagedList.Config.Builder()
            .setPageSize(StudentConst.PAGINATION_COUNT)
            .setEnablePlaceholders(false)
            .build()
    }

    fun getTimetable(startDate: Long, endDate: Long, offeringId : Int? = null, classType: Int = 2): LiveData<PagedList<Session>> {
        val factory = SessionDataSourceFactory(startDate, endDate, offeringId, classType, repo, viewModelScope)
        sessionDetailsObserver = factory.sessionDetailsObserver()
        studentSourceLiveData = factory.sessionDataSource()
        listSession = LivePagedListBuilder(factory, config).build()
        return listSession
    }

    fun getStudentCourses() = requestWithRetry {
        repo.getStudentOfferings()
    }

    fun getSubjects() = requestWithRetry {
        repo.getSubjects()
    }
}