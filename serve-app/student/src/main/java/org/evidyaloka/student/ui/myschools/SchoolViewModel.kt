package org.evidyaloka.student.ui.myschools

import android.util.Log
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.student.datasource.doubt.DoubtDataSource
import org.evidyaloka.core.student.datasource.doubt.DoubtDataSourceFactory
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.student.model.Student
import org.evidyaloka.core.student.repository.StudentRepository
import javax.inject.Inject

@HiltViewModel
class SchoolViewModel @Inject constructor(
    private val repo: StudentRepository
) : ViewModel() {
    private         val TAG = "SchoolViewModel"
    private var listDoubt: LiveData<PagedList<Doubt>> = MutableLiveData<PagedList<Doubt>>()
    private var doubtSourceLiveData = MutableLiveData<DoubtDataSource>()
    var doubtDetailsObserver = MutableLiveData<Int>()
    lateinit var config: PagedList.Config

    fun getDoubts(
        doubtId: Int? = null,
        offeringId: Int? = null,
        topicId: Int? = null,
        subtopicId: Int? = null
    ): LiveData<PagedList<Doubt>> {
        val factory =
            DoubtDataSourceFactory(doubtId, offeringId, topicId, subtopicId, repo, viewModelScope)
        doubtDetailsObserver = factory.doubtDetailObserver()
        doubtSourceLiveData = factory.doubtDataSource()
        listDoubt = LivePagedListBuilder(factory, config).build()
        return listDoubt
    }

    /**
     * Get Offerings opted by Student
     */
    fun getStudentOfferings() = liveData {
        emit(repo.getStudentOfferings())
    }

    /**
     * Get Offerings offered by School
     */
    fun getSchoolOfferings() = liveData {
        emit(repo.getSchoolOfferings())
    }

    fun getSelectedStudent() = repo.getSelectedStudent()

    fun getSettings() = liveData() {
        emit(repo.getSettings())
    }

    fun getStudyPreferences() = liveData {
        emit(repo.getStudyPreferences())
    }

    fun addAdditionalOfferings(data: Map<String, Any?>) = liveData{
        emit(repo.addAdditionalOfferings(data as HashMap<String, Any?>))
    }

    fun updateSchedule(data: Map<String, Any>) = liveData {
        emit(repo.updateSchedule(data))
    }

    override fun onCleared() {
        super.onCleared()
        Log.e(TAG, "onCleared: ")
    }


}