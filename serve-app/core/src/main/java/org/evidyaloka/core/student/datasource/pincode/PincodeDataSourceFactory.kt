package org.evidyaloka.core.student.datasource.pincode

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import kotlinx.coroutines.CoroutineScope
import org.evidyaloka.core.student.model.Pincode
import org.evidyaloka.core.student.repository.StudentRepository

class PincodeDataSourceFactory(
    private val searchText: String,
    private val repo: StudentRepository,
    private val scope: CoroutineScope
) : DataSource.Factory<Int, Pincode>() {

    private val TAG: String = "PincodeDataSourceFactory"
    private val mutableLiveData = MutableLiveData<PincodeDataSource>()
    private val pincodeDetailObserver = MutableLiveData<Int>()

    override fun create(): DataSource<Int, Pincode> {
        val pincodeDataSource = PincodeDataSource(
            searchText,
            repo,
            scope)
        mutableLiveData.postValue(pincodeDataSource)
        return pincodeDataSource
    }

    fun pincodeDataSource() = mutableLiveData

}