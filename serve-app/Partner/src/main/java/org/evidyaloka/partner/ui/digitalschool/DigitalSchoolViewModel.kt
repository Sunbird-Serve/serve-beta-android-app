package org.evidyaloka.partner.ui.digitalschool

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.FileDetails
import org.evidyaloka.core.partner.model.Generic
import org.evidyaloka.core.partner.model.UploadDocument
import org.evidyaloka.core.helper.ErrorData
import org.evidyaloka.core.partner.repository.MainRepository
import org.evidyaloka.partner.ui.BaseViewModel
import java.io.InputStream
import javax.inject.Inject

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
@HiltViewModel
class DigitalSchoolViewModel @Inject constructor(
    private val mainRepository: MainRepository
): BaseViewModel() {

    private var _progressPercentage = MutableLiveData<Int>()
    private var logoUpload = MutableLiveData<Resource<UploadDocument?>>()

    fun submitDigitalSchool( data:Map<String, Any>) = requestWithRetry{
        mainRepository.submitDigitalSchool(data)
    }

    fun getDigitalSchool( digitalSchoolId:Int) = requestWithRetry{
        mainRepository.getDigitalSchool(digitalSchoolId)
    }

    fun getUserList( ) = requestWithRetry{
        mainRepository.getUserList()
    }

    fun uploadFile( docType: Int, format:Int, filename:String, inputStream: InputStream) = requestWithRetry{
        mainRepository.uploadFile(docType,format,filename,inputStream) { progress: Int -> setProgress(progress)}
    }

    fun uploadFileAndSubmitSchool(files: ArrayList<FileDetails>, data: HashMap<String, Any>): LiveData<Resource<Generic?>> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO){
        var logoUploaded = false
        var uploadResponses: HashMap<Int, Resource<UploadDocument?>> = hashMapOf()
        for (file in files){
            uploadResponses[file.docType] = viewModelScope.async { mainRepository.uploadFile(file.docType,file.format,file.filename,file.inputStream,null) }.await()
       }
        for(response in uploadResponses){
            if(response.value is Resource.Success){
                (response.value as Resource.Success).data?.let {
                    when(response.key){
                        PartnerConst.DocType.SCHOOLLOGO.value-> {
                            data["logoId"] = it.id
                            logoUploaded = true
                        }
                        PartnerConst.DocType.SCHOOLBANNER.value-> {
                            data["bannerId"] = it.id
                        }
                    }

                }
            }else if(response.key == PartnerConst.DocType.SCHOOLLOGO.value && response.value is Resource.GenericError){
                logoUpload.postValue(response.value)
                return@liveData
            }
        }
        if(logoUploaded) {
            emit(mainRepository.submitDigitalSchool(data))
        }else{
            logoUpload.postValue(Resource.GenericError(123, ErrorData(123,"Logo is mandatory"),null))
        }
    }

    fun updateFileAndSchool(schoolId:Int,files: ArrayList<FileDetails>, data: HashMap<String, Any>): LiveData<Resource<Any?>> = liveData(context = viewModelScope.coroutineContext + Dispatchers.IO){
        var uploadResponses: HashMap<Int, Resource<UploadDocument?>> = hashMapOf()
        for (file in files){
            uploadResponses[file.docType] = viewModelScope.async { mainRepository.uploadFile(file.docType,file.format,file.filename,file.inputStream,null) }.await()
        }
        for(response in uploadResponses){
            if(response.value is Resource.Success){
                (response.value as Resource.Success).data?.let {
                    when(response.key){
                        PartnerConst.DocType.SCHOOLLOGO.value-> {
                            data["logoId"] = it.id
                        }
                        PartnerConst.DocType.SCHOOLBANNER.value-> {
                            data["bannerId"] = it.id
                        }
                    }

                }
            }else if(response.key == PartnerConst.DocType.SCHOOLLOGO.value){
                emit(response.value)
                return@liveData
            }
        }
        emit(mainRepository.updateDigitalSchool(schoolId,data))
    }
    fun getCourseProvider() = requestWithRetry {
        mainRepository.getCourseProviders()
    }

    private fun setProgress(progress: Int) {
        _progressPercentage.postValue(progress)
    }

    fun uploadProgress(): LiveData<Int> = _progressPercentage
    fun uploadLogo(): LiveData<Resource<UploadDocument?>> {
        logoUpload = MutableLiveData<Resource<UploadDocument?>>()
        return logoUpload
    }

}