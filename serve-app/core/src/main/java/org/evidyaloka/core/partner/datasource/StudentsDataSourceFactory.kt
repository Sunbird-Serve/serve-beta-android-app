package org.evidyaloka.core.repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import org.evidyaloka.core.partner.model.Student
import org.evidyaloka.core.partner.repository.MainRepository

class StudentsDataSourceFactory(private val userType: String, private val courseProviderId: Int?, private val digitalSchoolId: Int, private val grade: Int?, private val offeringId: Int?, private val academicYearId: Int?, private val repo: MainRepository, private val scope: CoroutineScope) : DataSource.Factory<Int, Student>() {

    private val mutableLiveData = MutableLiveData<StudentsDataSource>()

    override fun create(): DataSource<Int, Student> {
        val studentsDataSource = StudentsDataSource(
            userType,courseProviderId, digitalSchoolId, grade, offeringId, academicYearId, repo, scope)
        mutableLiveData.postValue(studentsDataSource)
        return studentsDataSource
    }

    fun studentsLiveDataSource() = mutableLiveData

    val studentCountObserver = StudentsDataSource.studentCountObserver

}