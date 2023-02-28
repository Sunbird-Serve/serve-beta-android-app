package org.evidyaloka.core.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.partner.repository.MainRepository

class StudentsPromotionDataSourceFactory(private val userType: String, private val digitalSchoolId: Int, private val grade: Int?, private val offeringId: Int?, private val courseProviderId: Int?, private val filter: String?, private val repo: MainRepository, private val scope: CoroutineScope) : DataSource.Factory<Int, Student>() {

    private val mutableLiveData = MutableLiveData<StudentsPromotionDataSource>()

    override fun create(): DataSource<Int, Student> {
        val studentsDataSource = StudentsPromotionDataSource(
            userType, digitalSchoolId, grade, offeringId, courseProviderId, filter,repo, scope)
        mutableLiveData.postValue(studentsDataSource)
        return studentsDataSource
    }

    fun studentsLiveDataSource() = mutableLiveData

    val studentCountObserver = StudentsPromotionDataSource.studentCountObserver

}