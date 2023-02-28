package org.evidyaloka.student.ui.login

import android.os.CountDownTimer
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import dagger.hilt.android.lifecycle.HiltViewModel
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.CommonConst.RESEND_TIMEOUT
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.student.model.Generic
import org.evidyaloka.core.student.model.User
import org.evidyaloka.core.student.repository.StudentRepository
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val mainRepository: StudentRepository
) : ViewModel() {
    private val TAG = "AuthViewModel"

    private val countDownTimer : CountDownTimer
    private var timeLeft = MutableLiveData<Int?>()

    init {
        timeLeft?.value = RESEND_TIMEOUT
        countDownTimer = object : CountDownTimer(RESEND_TIMEOUT * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeft?.let {
                    it.value?.let {
                        timeLeft.value = it-1
                    }
                }
            }

            override fun onFinish() {
                timeLeft.value = 0
            }
        }
    }

    /**
     */
    fun generateOTP(data: Map<String, Any>) = liveData {
        emit(mainRepository.generateOTP(data))
    }

    /**
     * Function to do registration and convert response to live data
     */
    fun verifyOTP(data: Map<String, Any>) = liveData {
        val response = mainRepository.verifyOTP(data)
        if(response is Resource.Success){
            response?.data?.let {
                var user = it
                user.roles = data[CommonConst.USER_TYPE]?.toString().let {
                    it?.let {
                        listOf(it)
                    } ?: listOf(CommonConst.PersonaType.Parent.toString())
                } ?: listOf(org.evidyaloka.core.Constants.CommonConst.PersonaType.Parent.toString())

                mainRepository.saveUser(user)
            }
        }
        emit(response)
    }

    /**
     *
     */
    fun duration(): MutableLiveData<Int?> {
        return timeLeft
    }

    /**
     * Function to start CountDown
     */
    fun startCountdown() {
        countDownTimer?.cancel()
        timeLeft?.value = RESEND_TIMEOUT
        countDownTimer?.start()
    }

    /**
     * To cancel countdown
     */
    fun cancelCountdown() {
        countDownTimer?.cancel()
    }

    fun saveUser(user: User): MutableLiveData<Resource<Generic>> {
        val liveData = MutableLiveData<Resource<Generic>>()
        try {
            mainRepository.saveUser(user)
            liveData.value = Resource.Success(Generic("1", "Successfully saved!"))
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            liveData.value = Resource.GenericError(0, null, null)
        }
        return liveData
    }

    fun getProgressObserable() = mainRepository.progressBarObservable

    override fun onCleared() {
        super.onCleared()
        cancelCountdown()
    }

}