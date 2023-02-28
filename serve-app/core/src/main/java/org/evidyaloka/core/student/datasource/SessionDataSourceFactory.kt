package org.evidyaloka.core.student.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import org.evidyaloka.core.model.Session
import org.evidyaloka.core.student.repository.StudentRepository

class SessionDataSourceFactory(
    private val startDate: Long,
    private val endDate: Long,
    private val offeringId: Int?,
    private val classType: Int?,
    private val repo: StudentRepository,
    private val scope: CoroutineScope
) : DataSource.Factory<Int, Session>() {

    private val mutableLiveData = MutableLiveData<SessionDataSource>()
    private val sessionDetailsObserver = MutableLiveData<Map<String, Any>>()

    override fun create(): DataSource<Int, Session> {
        val sessionDataSource = SessionDataSource(
            startDate,
            endDate,
            offeringId,
            classType,
            repo,
            scope,
            ::sessionDetails
        )
        mutableLiveData.postValue(sessionDataSource)
        return sessionDataSource
    }

    fun sessionDetails(map:Map<String, Any>){
        sessionDetailsObserver.postValue(map)
    }

    fun sessionDataSource() = mutableLiveData

    fun sessionDetailsObserver() = sessionDetailsObserver

}