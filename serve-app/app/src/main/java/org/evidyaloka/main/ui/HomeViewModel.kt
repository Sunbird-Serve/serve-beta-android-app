package org.evidyaloka.main.ui

import androidx.lifecycle.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.repository.MainRepository
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
        private val mainRepository: MainRepository
) : ViewModel() {




    fun getUser(): MutableLiveData<User?> {
        val liveData = MutableLiveData<User?>()
        try {
            mainRepository.getUser().let {
                liveData.value = it
            }

        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = null
        }
        return liveData
    }



}