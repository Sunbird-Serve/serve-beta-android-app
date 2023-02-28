package org.evidyaloka.core.partner.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.get
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.partner.model.AcademicYear
import org.evidyaloka.core.partner.model.Settings
import org.evidyaloka.core.partner.model.UploadDocument
import org.evidyaloka.core.partner.model.User
import org.evidyaloka.core.partner.network.PartnerApiService
import org.evidyaloka.core.partner.network.mapper.*
import org.evidyaloka.core.repository.BaseRepository
import org.evidyaloka.core.set
import retrofit2.http.Body
import java.io.InputStream

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
class MainRepository constructor(private val partnerApiService: PartnerApiService, private val prefs: SharedPreferences) : BaseRepository() {

    private val TAG = "MainRepository"
    private lateinit var _user: User
    private lateinit var _settings: Settings

    /**
     * Function to do make safe api call on IO dispatcher and process response data.
     */

    /**
     * @param data String login body data as string
     * @return UserEntity
     */
    suspend fun doLogin(data: Map<String, Any>) = safeApiCall(LoginResponseMapper()::mapFromEntity) { partnerApiService.doLogin(data) }

    /**
     * @param data String registration body data as string
     * @return Generic
     */
    suspend fun doRegister(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) { partnerApiService.doRegister(data) }

    /**
     * @return User
     */
    suspend fun getProfileData() =
        safeApiCall(UserResponseMapper()::mapFromEntity) { partnerApiService.getProfile(getUser().id) }

    /**
     *@param data profile body as Map<String, Any>
     * @return Generic
     */
    suspend fun updateProfileData(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) { partnerApiService.saveProfile(getUser().id, data) }

    suspend fun submitDigitalSchool(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity){
            var schoolMap = data as HashMap<String, Any>
            schoolMap.put(PartnerConst.PARTNER_ID, getUser().partnerId)
            partnerApiService.submitDigitalSchool( data)
        }
    suspend fun updateDigitalSchool(schoolId: Int,data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity){
            var schoolMap = data as HashMap<String, Any>
            schoolMap.put(PartnerConst.PARTNER_ID, getUser().partnerId)
            partnerApiService.updateDigitalSchool(
                schoolId,
                data)
        }

    suspend fun getUserSchoolList(userType: PartnerConst.RoleType) =
        safeApiCall(SchoolResponseMapper()::mapFromEntity) {
            partnerApiService.getUserSchoolList(userType.name, getUser().partnerId)
        }

    suspend fun addUser(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            partnerApiService.addUser(data)
        }

    suspend fun getUserList() =
        safeApiCall(UserListResponseMapper()::mapFromEntity) { partnerApiService.getUserList() }

    /**
     * @param data String login body data as string
     * @return UserEntity
     */
    suspend fun changePassword(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            var userMap = data as HashMap<String, Any>
            userMap.put(PartnerConst.USER_ID, _user.id)
            partnerApiService.changePassword(data)
        }

    suspend fun getDigitalSchool(id: Int) = safeApiCall(SchoolResponseMapper()::mapFromEntity) { partnerApiService.getDigitalSchool(
        if(getUser().roles.contains(PartnerConst.RoleType.DSP.name)) PartnerConst.RoleType.DSP.name else PartnerConst.RoleType.DSM.name, getUser().partnerId ,id) }

    suspend fun uploadFile(docType: Int, format:Int, filename: String, inputStream: InputStream, updateProgress: ((progress: Int) -> Unit)?): Resource<UploadDocument?> = withContext(Dispatchers.IO) {
//        updateProgress(1)
//        val fileStream = ProgressRequestBody(inputStream, object : ProgressRequestBody.UploadCallbacks {
//            override fun onProgressUpdate(percentage: Int) {
//                updateProgress(percentage)
//            }
//
//            override fun onError() {
//                updateProgress(-1) // -1 represents error in progress value
//            }
//
//            override fun onFinish() {
//                updateProgress(100)
//            }
//        })
        val requestFile: RequestBody = inputStream.readBytes()
            .toRequestBody(
                "multipart/form-data".toMediaTypeOrNull()
            )
        val file = MultipartBody.Part.createFormData("file", filename, requestFile)
        val mFilename: RequestBody = filename.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return@withContext safeApiCall(UploadDocumentResponseMapper()::mapFromEntity) {
            partnerApiService.uploadFile(docType, format, mFilename, file)
        }
    }

    fun getUser(): User =
        if (this::_user.isInitialized) {
            _user
        } else {
            Gson().fromJson(prefs.get<String>(PartnerConst.USER_DETAILS), User::class.java)
        }

    fun saveUser(user: User) {
        prefs[PartnerConst.USER_DETAILS] = Gson().toJson(user)
        user.sessionId.isNotEmpty()?.let {
            prefs[PartnerConst.SESSION_ID] = user.sessionId
        }
        _user = user
    }

    fun clearUser() {
        prefs.edit().clear().apply()
        _user = User()
    }

    fun updateUser(user: User) {
        user.apply {
            organization = _user.organization
        }
        prefs[PartnerConst.USER_DETAILS] = user
        _user = user
    }

    suspend fun getAllUsers(page : Int, searchKey : String) =
        safeApiCall(UserListResponseMapper()::mapFromEntity,false) { partnerApiService.getAllUsers(
            PartnerConst.RoleType.DSM.name, getUser().partnerId, page, PartnerConst.PAGINATION_COUNT, searchKey) }

    suspend fun getUserDetails(userId: Int) =
        safeApiCall(UserListResponseMapper()::mapFromEntity) { partnerApiService.getUserDetail(
            PartnerConst.RoleType.DSM.name, getUser().partnerId, userId) }

    suspend fun getUser(userId: Int) =
        safeApiCall(UserResponseMapper()::mapFromEntity) { partnerApiService.getUser(userId) }

    suspend fun assignUserSchool(dsmId: Int, schoolId: Int) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            partnerApiService.assignDsmToSchool(
                dsmId,
                schoolId
            )
        }

    suspend fun updateDsmUser(dsmId: Int, data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            partnerApiService.updateUser(
                dsmId,
                data
            )
        }

    /*
     *Courses Page API
     */

    suspend fun getAcademicYear(cpId : Int):Resource<List<AcademicYear>?> = safeApiCall(AcademicYearResponseMapper()::mapFromEntity) { partnerApiService.getAcademicYear(cpId) }

    suspend fun getCourseProviders(courseProviderCode : String? = null) = safeApiCall(CourseProviderResponseMapper()::mapFromEntity) { partnerApiService.getCourseProvider(courseProviderCode) }

    suspend fun getCourse(grade : Int, courseProvider : String,languageId: Int? = null) = safeApiCall(CourseResponseMapper()::mapFromEntity) { partnerApiService.getCourse(grade, courseProvider,languageId) }

    suspend fun getCourseOfferings(courseProviderId : Int? = null, digitalSchoolId: Int, gradeId: Int?= null, academicYearId : Int?= null, languageId: Int? = null) = safeApiCall(CourseOfferingsResponseMapper()::mapFromEntity) { partnerApiService.getCourseOfferings(courseProviderId, digitalSchoolId, gradeId, academicYearId, languageId) }

    suspend fun addCourse(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) { partnerApiService.addCourse(data) }

    suspend fun checkCourse(courseId:Int, digitalSchoolId : Int) = safeApiCall(CheckCourseResponseMapper()::mapFromEntity) { partnerApiService.checkCourse(courseId,digitalSchoolId,getUser().partnerId) }

    suspend fun getEnrolledStudents(userType: String,courseProviderId: Int?, digitalSchoolId: Int, grade: Int?, offeringId: Int?, academicYearId: Int?,page: Int) = safeApiCall(EnrolledStudentsMapper()::mapFromEntity,false) {
        partnerApiService.getEnrolledStudents(userType,courseProviderId, digitalSchoolId, grade, offeringId, academicYearId,page,
            PartnerConst.PAGINATION_COUNT) }

    suspend fun enrolledStudents(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) {
        var map = data as HashMap<String, Any>
        map.put(PartnerConst.PARTNER_ID, getUser().partnerId)
        partnerApiService.enrolledStudents(map)
    }

    suspend fun updateStudents(studentId:Int, digitalSchoolId : Int, data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) {
        var map = data as HashMap<String, Any>
        map.put(PartnerConst.PARTNER_ID, getUser().partnerId)
        partnerApiService.updateStudents(studentId,digitalSchoolId,map)
    }

    suspend fun getStudentDetails(studentId:Int,digitalSchoolId:Int) = safeApiCall(StudentsResponseMapper()::mapFromEntity) { partnerApiService.getStudentDetails(studentId,digitalSchoolId) }

    suspend fun getLanguages() = safeApiCall(LanguageResponseMapper()::mapFromEntity) { partnerApiService.getLanguages() }

    /*
   *PING
   *@return Settings
   */
    suspend fun ping() = safeApiCall(PingResponseMapper()::mapFromEntity) {
        var partnerMap = HashMap<String, Any>()
        partnerMap.put(PartnerConst.PARTNER_ID, getUser().partnerId)
        partnerApiService.ping(partnerMap)
    }

    suspend fun logout(@Body data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) {
        partnerApiService.logout(data)
    }

    fun getSettings(): Settings =
        if (this::_settings.isInitialized) {
            _settings
        } else {
            Gson().fromJson(prefs.get<String>(PartnerConst.USER_SETTINGS), Settings::class.java)
        }

    fun saveSettings(settings: Settings) {
        prefs[PartnerConst.USER_SETTINGS] = Gson().toJson(settings)
        _settings = settings
    }

    suspend fun getStateList() =
        safeApiCall(StateListResponseMapper()::mapFromEntity) {
            partnerApiService.getStateList()
        }

    suspend fun getPincode(stateId: Int, searchQuery:String?, page : Int) =
        safeApiCall(PincodeListResponseMapper()::mapFromEntity,false) { partnerApiService.getPincode(
            stateId, searchQuery,getUser().partnerId, page, PartnerConst.PAGINATION_COUNT) }

    suspend fun saveState(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) {
        var map = data as HashMap<String, Any>
        map.put(PartnerConst.PARTNER_ID, getUser().partnerId)
        map.put(PartnerConst.USER_TYPE,PartnerConst.RoleType.DSP.name)
        partnerApiService.saveState(map)
    }

    suspend fun deleteLocation(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) {
        var map = data as HashMap<String, Any>
        map.put(PartnerConst.PARTNER_ID, getUser().partnerId)
        map.put(PartnerConst.USER_TYPE,PartnerConst.RoleType.DSP.name)
        partnerApiService.deleteLocation(map)
    }

    /**
     *@param data profile body as Map<String, Any>
     * @return Generic
     */
    suspend fun sendOtp(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) { partnerApiService.sendOtp(data) }

    suspend fun verifyOtp(data: Map<String, Any>) = safeApiCall(LoginResponseMapper()::mapFromEntity) { partnerApiService.verifyOtp(data) }

    suspend fun getCoursePlayer(sessionId:Int?, offeringId:Int, topicId : Int, timetableId : Int?, subtopicId: Int) = safeApiCall(
        CoursePlayerResponseMapper()::mapFromEntity) {
        val userType = if(getUser().roles.contains(PartnerConst.RoleType.DSP.name)) PartnerConst.RoleType.DSP.name else PartnerConst.RoleType.DSM.name
        partnerApiService.getCoursePlayer( null, getUser().id, userType, sessionId,offeringId, topicId, timetableId, subtopicId) }

    suspend fun getExploreDigitalSchool(grade: Int, courseProviderId: Int) = safeApiCall(
        ExploreDigitalSchoolResponseMapper()::mapFromEntity) {
        val userType = if(getUser().roles.contains(PartnerConst.RoleType.DSP.name)) PartnerConst.RoleType.DSP.name else PartnerConst.RoleType.DSM.name
        partnerApiService.getExploreDigitalSchool(getUser().id, userType,grade,courseProviderId)
    }

    suspend fun getSubjectDetails(offeringId:Int, courseId : Int) = safeApiCall(SubjectDetailsMapper()::mapFromEntity) {
        val userType = if(getUser().roles.contains(PartnerConst.RoleType.DSP.name)) PartnerConst.RoleType.DSP.name else PartnerConst.RoleType.DSM.name
        partnerApiService.getSubjectDetails( null, getUser().id, userType, offeringId,courseId)
    }

    /**
     * @param data String registration body data as string
     * @return Generic
     */
    suspend fun updateRegister(data: Map<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) { partnerApiService.updateRegister(data) }

    suspend fun getGrades(courseProviderId: Int) =
        safeApiCall(GradesResponseMapper()::mapFromEntity) {
            partnerApiService.getGrades(
                getUser().id,
                PartnerConst.RoleType.DSP.name,
                courseProviderId
            )
        }

    suspend fun updateFCMToken(data: HashMap<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity, false) {
            var map = (data as HashMap<String, Any>).apply {
                this[PartnerConst.PARTNER_ID] = getUser().partnerId
                this[PartnerConst.USER_TYPE] = PartnerConst.RoleType.DSP.name
                this[StudentConst.USER_ID] = getUser().id
            }
            partnerApiService.updateFCMToken(map)
        }

    suspend fun getStudentsForPormotion(
        userType: String,
        courseProviderId: Int?,
        digitalSchoolId: Int,
        grade: Int?,
        offeringId: Int?,
        filter: String?,
        page: Int
    ) = safeApiCall(EnrolledStudentsMapper()::mapFromEntity,false) {
        partnerApiService.getStudentsForPromotion(userType,courseProviderId, digitalSchoolId, grade, offeringId,filter,page,
            PartnerConst.PAGINATION_COUNT) }

    suspend fun promoteStudents(data: HashMap<String, Any>) = safeApiCall(GenericResponseMapper()::mapFromEntity) {
        var map = (data as HashMap<String, Any>).apply {
            this[PartnerConst.PARTNER_ID] = getUser().partnerId
            this[PartnerConst.USER_TYPE] = PartnerConst.RoleType.DSM.name
            this[StudentConst.USER_ID] = getUser().id
        }
        partnerApiService.promoteStudents(map)
    }
}

