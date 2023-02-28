package org.evidyaloka.partner.ui.promotion

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class PromotionTypeViewModel  @Inject constructor(
    private val mainRepository: MainRepository
) : BaseViewModel() {

    fun promoteStudent(data: HashMap<String, Any>) = liveData {
        emit(mainRepository.promoteStudents(data))
    }

}