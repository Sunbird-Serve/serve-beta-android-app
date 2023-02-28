package org.evidyaloka.core.student.datasource.doubt

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import org.evidyaloka.core.student.model.Doubt
import org.evidyaloka.core.student.repository.StudentRepository

class DoubtDataSourceFactory(
    private val id: Int?,
    private val offeringId: Int?,
    private val topicId: Int?,
    private val subtopicId: Int?,
    private val repo: StudentRepository,
    private val scope: CoroutineScope
) : DataSource.Factory<Int, Doubt>() {

    private val mutableLiveData = MutableLiveData<DoubtDataSource>()
    private val doubtDetailObserver = MutableLiveData<Int>()

    override fun create(): DataSource<Int, Doubt> {
        val doubtDataSource = DoubtDataSource(
            id,
            offeringId,
            topicId, subtopicId,
            repo,
            scope,
            ::doubtDetails
        )
        mutableLiveData.postValue(doubtDataSource)
        return doubtDataSource
    }

    fun doubtDetails(count:Int){
        doubtDetailObserver.postValue(count)
    }

    fun doubtDataSource() = mutableLiveData

    fun doubtDetailObserver() = doubtDetailObserver

}