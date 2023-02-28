package org.evidyaloka.core.student.repository

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.evidyaloka.core.Constants.CommonConst
import org.evidyaloka.core.Constants.StudentConst
import org.evidyaloka.core.Constants.StudentConst.DOUBT_PAGINATION_COUNT
import org.evidyaloka.core.get
import org.evidyaloka.core.helper.Resource
import org.evidyaloka.core.repository.BaseRepository
import org.evidyaloka.core.set
import org.evidyaloka.core.student.database.dao.CourseContentDAO
import org.evidyaloka.core.student.database.entity.BookmarkEntity
import org.evidyaloka.core.student.database.entity.CourseContentEntity
import org.evidyaloka.core.student.database.entity.StudentEntity
import org.evidyaloka.core.student.model.*
import org.evidyaloka.core.student.network.StudentApiService
import org.evidyaloka.core.student.network.mapper.*
import java.io.InputStream

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
class StudentRepository constructor(
    private val studentApiService: StudentApiService,
    private val courseContentDAO: CourseContentDAO,
    private val prefs: SharedPreferences
) : BaseRepository() {

    private val TAG = "StudentRepository"
    private lateinit var _user: User
    private lateinit var _settings: Settings
    private var _studentList: MutableList<Student> = mutableListOf()
    private var studentList: MutableLiveData<List<Student>> = MutableLiveData()
    lateinit var _selectedStudent: Student
    private var selectedStudent: MutableLiveData<Student> = MutableLiveData()

    fun setSelectedStudent(student: Student) {
        try {
            if (!this::_selectedStudent.isInitialized || _selectedStudent.id != student.id) {
                _selectedStudent = student
                selectedStudent.postValue(_selectedStudent)
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun getSelectedStudent() = selectedStudent

    suspend fun generateOTP(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) { studentApiService.generateOTP(data) }

    suspend fun verifyOTP(data: Map<String, Any>) =
        safeApiCall(LoginResponseMapper()::mapFromEntity) { studentApiService.verifyOTP(data) }

    suspend fun getStudents() =
        safeApiCall(StudentsResponseMapper()::mapFromEntity) {
            studentApiService.getStudents(
                getUser().id,
                StudentConst.UserType.guardian.toString()
            )
        }

    suspend fun getSchoolOfferings() = safeApiCall(OfferingMapper()::mapFromEntity) {

        studentApiService.getSchoolOfferings(
            _selectedStudent.schools?.id ?: 0,
            _selectedStudent.grade.toInt(),
            getUser().id,
            StudentConst.UserType.guardian.toString()
        )

    }

    suspend fun getStudentOfferings() = safeApiCall(OfferingMapper()::mapFromEntity) {
        studentApiService.getStudentOfferings(
            _selectedStudent.schools?.id ?: 0,
            _selectedStudent.id,
            getUser().id,
            StudentConst.UserType.guardian.toString()
        )
    }

    suspend fun updateOffering(data: Map<String, Any>) =
        safeApiCall(UpdateOfferingMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.DIGITAL_SCHOOL_ID] = _selectedStudent.schools?.id ?: 0
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.updateOffering(data)
        }

    fun getUser(): User =
        if (this::_user.isInitialized) {
            _user
        } else {
            Gson().fromJson(prefs.get<String>(CommonConst.USER_DETAILS), User::class.java)
        }

    fun saveUser(user: User) {
        _user = user
        prefs[CommonConst.USER_DETAILS] = Gson().toJson(user)
        user.sessionId.isNotEmpty()?.let {
            prefs[CommonConst.SESSION_ID] = user.sessionId
        }
        Log.e("User", _user.name)
    }

    fun clearUser() {
        prefs.edit().clear().apply()
        _user = User()
    }

    fun updateUser(user: User) {
        /*user.apply {
            organization = _user.organization
        }*/
        prefs[CommonConst.USER_DETAILS] = user
        _user = user
    }

    fun shouldShowcourseStartDate() = prefs.get<Boolean>(CommonConst.SHOW_START_DATE_DIALOG)
    fun setCourseStartDateDialogShouldShow(shouldShow: Boolean) {
        prefs[CommonConst.SHOW_START_DATE_DIALOG] = shouldShow
    }

    fun setStudentList(list: List<Student>) {
        _studentList = list as MutableList<Student>
        studentList.postValue(list)
    }

    fun getStudentList() = studentList

    fun updateStudentOnboardingStatus(status: String): Boolean {
        _selectedStudent.onboardingStatus = status
        var index = _studentList.indexOfFirst { it.id == _selectedStudent.id }
        _studentList[index].status = _selectedStudent.status
        return true
    }

    /**
     * Schedule Classes
     */
    suspend fun schedulecourse(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {

            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.DIGITAL_SCHOOL_ID] = _selectedStudent.schools?.id ?: 0
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.schedulecourse(data)
        }

    suspend fun updateSchedule(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                //this[StudentConst.DIGITAL_SCHOOL_ID] = _selectedStudent.schools?.id ?: 0
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.updateSchedule(data)
        }

    suspend fun getStudyPreferences() =
        safeApiCall(StudyPreferenceResponseMapper()::mapFromEntity, false) {
            studentApiService.getStudyPreferences(
                _selectedStudent.id,
                getUser().id,
                StudentConst.UserType.guardian.toString()
            )
        }


    fun setSettings(settings: Settings) {
        prefs[CommonConst.USER_SETTINGS] = Gson().toJson(settings)
        _settings = settings
    }

    fun getSettings(): Settings =
        if (this::_settings.isInitialized) {
            _settings
        } else {
            Gson().fromJson(
                prefs.get<String>(CommonConst.USER_SETTINGS) ?: "{}",
                Settings::class.java
            )
        }

    suspend fun ping() = safeApiCall(PingResponseMapper()::mapFromEntity, false) {
        val data = HashMap<String, Any>().apply {
            this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
            this[StudentConst.USER_ID] = getUser().id
        }

        if (this::_selectedStudent.isInitialized) {
            data[StudentConst.STUDENT_ID] = _selectedStudent.id
        }

        studentApiService.ping(data)
    }

    suspend fun getSessions(
        startDate: Long,
        endDate: Long,
        offeringId: Int? = null,
        classType: Int?,
        page: Int
    ) = safeApiCall(SessionResponseMapper()::mapFromEntity, false) {
        studentApiService.getSessions(
            _selectedStudent.id,
            getUser().id,
            StudentConst.UserType.guardian.toString(),
            _selectedStudent.schools?.id ?: 0,
            startDate,
            endDate,
            offeringId,
            classType,
            page,
            StudentConst.PAGINATION_COUNT
        )
    }

    suspend fun getCoursePlayer(
        sessionId: Int?,
        offeringId: Int,
        topicId: Int,
        timetableId: Int?,
        subtopicId: Int
    ) = safeApiCall(CoursePlayerResponseMapper()::mapFromEntity) {
        studentApiService.getCoursePlayer(
            if (::_selectedStudent.isInitialized) _selectedStudent.id else null,
            getUser().id,
            StudentConst.UserType.guardian.toString(),
            sessionId,
            offeringId,
            topicId,
            timetableId,
            subtopicId
        )
    }

    suspend fun sendAttendance(data: HashMap<String, Any?>): Resource<Any?> =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.sendAttendance(data)
        }

    suspend fun getSessionDetail(sessionId: Int) =
        safeApiCall(SubtopicsResponseMapper()::mapFromEntity) {
            studentApiService.getSessionDetail(
                _selectedStudent.id,
                getUser().id,
                StudentConst.UserType.guardian.toString(),
                sessionId
            )
        }

    suspend fun uploadFile(
        docType: Int,
        format: Int,
        filename: String,
        inputStream: InputStream,
        studentId: Int? = if (::_selectedStudent.isInitialized) _selectedStudent.id else null,
        updateProgress: ((progress: Int) -> Unit)
    ): Resource<UploadDocument?> = withContext(
        Dispatchers.IO
    ) {
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
        val mFilename: RequestBody =
            filename.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        return@withContext safeApiCall(UploadDocumentResponseMapper()::mapFromEntity) {
            studentApiService.uploadFile(
                studentId,
                getUser().id,
                StudentConst.UserType.guardian.toString(),
                docType,
                format,
                mFilename,
                file
            )
        }
    }

    suspend fun submitDoubt(data: HashMap<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.student.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.submitDoubt(data)
        }

    suspend fun editDoubt(data: HashMap<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.student.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.editDoubt(data)
        }

    suspend fun getDoubts(
        page: Int,
        id: Int? = null,
        offeringId: Int? = null,
        topicId: Int? = null,
        subtopicId: Int? = null
    ) = safeApiCall(DoubtListResponseMapper()::mapFromEntity) {
        studentApiService.getDoubts(
            _selectedStudent.id,
            getUser().id,
            StudentConst.UserType.guardian.toString(),
            _selectedStudent.schools?.id ?: 0,
            page,
            DOUBT_PAGINATION_COUNT,
            id,
            offeringId,
            topicId,
            subtopicId
        )
    }

    suspend fun getSubjects() = safeApiCall(SubjectMapper()::mapFromEntity) {
        studentApiService.getSubjects(
            _selectedStudent.id,
            getUser().id,
            StudentConst.UserType.guardian.toString(),
            _selectedStudent.schools?.id ?: 0
        )
    }

    suspend fun getSubjectDetails(offeringId: Int, courseId: Int) =
        safeApiCall(SubjectDetailsMapper()::mapFromEntity) {
            studentApiService.getSubjectDetails(
                if (::_selectedStudent.isInitialized) _selectedStudent.id else null,
                getUser().id,
                StudentConst.UserType.guardian.toString(),
                offeringId,
                courseId
            )
        }

    suspend fun getSessionDetails(offeringId: Int, topicId: Int, subTopicId: Int) =
        safeApiCall(SubTopicSessionResponseMapper()::mapFromEntity) {
            studentApiService.getSessionDetails(
                if (::_selectedStudent.isInitialized) _selectedStudent.id else null,
                getUser().id,
                StudentConst.UserType.guardian.toString(),
                offeringId,
                topicId,
                subTopicId
            )
        }

    suspend fun logout(map: HashMap<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            var data = map
            if (::_selectedStudent.isInitialized)
                data[StudentConst.STUDENT_ID] = _selectedStudent.id
            data[StudentConst.USER_TYPE] = StudentConst.UserType.student.toString()
            data[StudentConst.USER_ID] = getUser().id
            studentApiService.logout(data)
        }

    suspend fun updateViewStatus(data: HashMap<String, Any?>): Resource<Any?> =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.updateViewStatus(data)
        }

    suspend fun updateSessionRating(data: HashMap<String, Any?>): Resource<Any?> =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.updateSessionRating(data)
        }

    suspend fun addAdditionalOfferings(data: HashMap<String, Any?>): Resource<EditSchedule?> =
        safeApiCall(EditScheduleResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.addAdditionalOfferings(data)
        }

    suspend fun getMissedClasses(): Resource<MissedClasses?> =
        safeApiCall(MissedClassesResponseMapper()::mapFromEntity) {
            studentApiService.getMissedClasses(
                _selectedStudent.id,
                getUser().id,
                StudentConst.UserType.guardian.toString(),
                _selectedStudent.schools?.id ?: 0
            )
        }

    suspend fun getPincodes(searchText: String, page: Int) =
        safeApiCall(PincodeResponseMapper()::mapFromEntity, false) {
            studentApiService.getPincodes(
                getUser().id, StudentConst.UserType.guardian.toString(),
                searchText, page, CommonConst.PINCODE_PAGINATION_COUNT
            )
        }

    suspend fun generateOtpGuardianJoin(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            studentApiService.generateOtpGuardianJoin(
                data
            )
        }

    suspend fun registerGuardian(data: Map<String, Any>) =
        safeApiCall(LoginResponseMapper()::mapFromEntity) { studentApiService.registerGuardian(data) }


    suspend fun getCourseProviders() = safeApiCall(CourseProviderMapper()::mapFromEntity) {
        studentApiService.getCourseProviders(
            getUser().id,
            StudentConst.UserType.guardian.toString()
        )
    }

    suspend fun getExploreDigitalSchool(grade: Int, courseProviderId: Int) =
        safeApiCall(ExploreDigitalSchoolResponseMapper()::mapFromEntity) {
            studentApiService.getExploreDigitalSchool(
                getUser().id,
                StudentConst.UserType.guardian.toString(),
                grade,
                courseProviderId
            )
        }

    suspend fun getLanguages() =
        safeApiCall(LanguageResponseMapper()::mapFromEntity) { studentApiService.getLanguages() }

    suspend fun enrollStudents(data: Map<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            var map = (data as HashMap<String, Any>).apply {
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.enrollStudents(map)
        }

    suspend fun getGrades(courseProviderId: Int) =
        safeApiCall(GradesResponseMapper()::mapFromEntity) {
            studentApiService.getGrades(
                getUser().id,
                StudentConst.UserType.guardian.toString(),
                courseProviderId
            )
        }

    suspend fun updateFCMToken(data: HashMap<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity, false) {
            var map = (data as HashMap<String, Any>).apply {
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.updateFCMToken(map)
        }

    suspend fun updateLocationDetails(data: HashMap<String, Any>) =
        safeApiCall(GenericResponseMapper()::mapFromEntity, false) {
            var map = (data as HashMap<String, Any>).apply {
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.updateLocationDetails(map)
        }

    suspend fun updateContentDownloadStatus(data: HashMap<String, Any?>): Resource<Any?> =
        safeApiCall(GenericResponseMapper()::mapFromEntity) {
            (data as HashMap<String, Any>).apply {
                this[StudentConst.STUDENT_ID] = _selectedStudent.id
                this[StudentConst.USER_TYPE] = StudentConst.UserType.guardian.toString()
                this[StudentConst.USER_ID] = getUser().id
            }
            studentApiService.updateContentDownloadStatus(data)
        }


    // Local database functions

    suspend fun insertContentDetails(courseContentEntity: CourseContentEntity) {
        courseContentEntity.contentId?.toLong()?.let {
            courseContentDAO.getDownloadDetailsByContentId(it)
                ?.let { courseContentDAO.deleteCourseContent(it) }
        }
        courseContentEntity.studentId = _selectedStudent.id
        courseContentDAO.insertStudent(
            StudentEntity(
                _selectedStudent.id,
                _selectedStudent.name,
                _selectedStudent.grade
            )
        )
        courseContentDAO.insertCourseContent(courseContentEntity)
    }

    //Student functions
    fun getStudentWithContent(studentId: Int) = courseContentDAO.getContentByStudentId(studentId)

    fun getAllStudents() = courseContentDAO.getAllStudents()

    fun getStudentById(id: Int) = courseContentDAO.getStudentById(id)

    fun getSelectedStudentContent() = courseContentDAO.getContentByStudentId(if (this::_selectedStudent.isInitialized) _selectedStudent.id else 0)


    //Content functions

    fun getAllDownloadContentDetails() = courseContentDAO.getAll()

    suspend fun getDownloadContentDetailsByContentId(id: Long) =
        downloadContentDetailsByContentId(id)

    suspend fun updateDownloadContentStatus(id: Long, status: Int?, reason: String? = "") =
        updateDownloadStatus(id, status, reason)

    fun downloadContentDetailsByContentId(contentId: Long) =
        courseContentDAO.getDownloadDetailsByContentId(contentId)

    fun downloadContentDetailsById(id: Long) = courseContentDAO.getDownloadDetailsById(id)

    fun updateDownloadStatus(id: Long, status: Int?, reason: String? = "") =
        courseContentDAO.updateStatus(id, status, reason)

    fun updateUrl(id: Long, localUrl: String? = "") =
        courseContentDAO.updateUrl(id, localUrl)

    fun updateOfflineProgress(contentId: Long, progress: Long) =
        courseContentDAO.updateProgress(contentId, progress)

    suspend fun deleteByContentId(contentId: Long) = courseContentDAO.deleteByContentId(contentId)

    //    Bookmark DB operations
    suspend fun insertBookmark(bookmarkEntity: BookmarkEntity) {
        bookmarkEntity.subtopicId?.toLong()?.let {

        }
        bookmarkEntity.studentId = _selectedStudent.id
        courseContentDAO.insertStudent(
            StudentEntity(
                _selectedStudent.id,
                _selectedStudent.name,
                _selectedStudent.grade
            )
        )
        courseContentDAO.insertBookmark(bookmarkEntity)
    }

    fun deleteBookmark(bookmarkEntity: BookmarkEntity) = courseContentDAO.deleteBookmark(bookmarkEntity)

    fun getBookmarkBySubtopicId(subTopicId: Int): BookmarkEntity = courseContentDAO.getBookmarkBySubtopicId(subTopicId)

    fun getStudentBookmarks() = courseContentDAO.getBookmarkByStudentId(_selectedStudent.id)

}

