package org.evidyaloka.core.student.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.evidyaloka.core.student.network.entity.*
import org.evidyaloka.core.student.network.entity.doubt.DoubtListResponse
import retrofit2.http.*

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
@JvmSuppressWildcards
interface StudentApiService {

    /**
     * Login via OTP
     */
    @POST("signin")
    suspend fun generateOTP(@Body data: Map<String, Any>): GenericResponse

    @POST("verifyOtp")
    suspend fun verifyOTP(@Body data: Map<String, Any>): LoginResponse

    @GET("schoolofferings")
    suspend fun getSchoolOfferings(
        @Query("digitalSchoolId") digitalSchoolId: Int,
        @Query("grade") grade: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String
    ): OfferingResponse

    @GET("offering")
    suspend fun getStudentOfferings(
        @Query("digitalSchoolId") digitalSchoolId: Int,
        @Query("studentId") studentId: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String
    ): OfferingResponse

    @GET("guardian/students")
    suspend fun getStudents(
        @Query("userId") userId: Int,
        @Query("userType") userType: String
    ): StudentsResponse

    @POST("offering")
    suspend fun updateOffering(@Body data: Map<String, Any>): UpdateOfferingResponse

    @POST("ping")
    suspend fun ping(@Body data: Map<String, Any>): PingResponse

    @POST("schedulecourse")
    suspend fun schedulecourse(@Body data: Map<String, Any>): GenericResponse

    @POST("schedulecourse")
    suspend fun updateSchedule(@Body data: Map<String, Any>): GenericResponse

    @GET("schedulecourse")
    suspend fun getStudyPreferences(
        @Query("studentId") studentId: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String
    ): StudyPreferenceResponse

    @GET("session")
    suspend fun getSessions(
        @Query("studentId") studentId: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("schoolId") schoolId: Int,
        @Query("startDate") startDate: Long,
        @Query("endDate") endDate: Long,
        @Query("offeringId") offeringId: Int?,
        @Query("classType") classType: Int?,
        @Query("page") page: Int,
        @Query("count") count: Int
    ): SessionResponse

    @GET("contentdetails")
    suspend fun getCoursePlayer(
        @Query("studentId") studentId: Int?,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("sessionId") sessionId: Int?,
        @Query("offeringId") offeringId: Int,
        @Query("topicId") topicId: Int,
        @Query("timetableId") timetableId: Int?,
        @Query("subtopicId") subtopicId: Int
    ): CoursePlayerResponse

    @GET("session/detail")
    suspend fun getSessionDetail(
        @Query("studentId") studentId: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("sessionId") sessionId: Int
    ): SubtopicsResponse

    @POST("session/attendance")
    suspend fun sendAttendance(@Body data: Map<String, Any>): GenericResponse

    @POST("doubt")
    suspend fun submitDoubt(@Body data: Map<String, Any>): GenericResponse

    @PUT("doubt")
    suspend fun editDoubt(@Body data: Map<String, Any>): GenericResponse

    @Multipart
    @POST("documents")
    suspend fun uploadFile(
        @Query("studentId") studentId: Int?,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("doc_type") docType: Int,
        @Query("format") format: Int,
        @Part("filename") filename: RequestBody,
        @Part file: MultipartBody.Part
    ): UploadDocumentResponse


    @GET("doubt")
    suspend fun getDoubts(
        @Query("studentId") studentId: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("schoolId") schoolId: Int,
        @Query("page") page: Int,
        @Query("count") count: Int,
        @Query("doubtId") id: Int? = null,
        @Query("offeringId") offeringId: Int? = null,
        @Query("topicId") topicId: Int? = null,
        @Query("subtopicId") subtopicId: Int? = null
    ): DoubtListResponse

    @GET("subjects")
    suspend fun getSubjects(
        @Query("studentId") studentId: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("schoolId") schoolId: Int
    ): SubjectResponse

    @GET("subjects/detail")
    suspend fun getSubjectDetails(
        @Query("studentId") studentId: Int?,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("offeringId") offeringId: Int,
        @Query("courseId") courseId: Int
    ): SubjectDetailsResponse

    @GET("subtopic/session")
    suspend fun getSessionDetails(
        @Query("studentId") studentId: Int?,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("offeringId") offeringId: Int,
        @Query("topicId") topicId: Int? = null,
        @Query("subtopicId") subtopicId: Int? = null
    ): SubTopicSessionResponse

    @POST("logout")
    suspend fun logout(@Body data: Map<String, Any>): GenericResponse


    @POST("content/viewstatus")
    suspend fun updateViewStatus(@Body data: Map<String, Any>): GenericResponse

    @POST("session/rate")
    suspend fun updateSessionRating(@Body data: Map<String, Any>): GenericResponse

    @POST("subjects/add")
    suspend fun addAdditionalOfferings(@Body data: Map<String, Any>): EditScheduleResponse

    @GET("missedsessions")
    suspend fun getMissedClasses(
        @Query("studentId") studentId: Int,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("schoolId") schoolId: Int
    ): MissedClassesResponse

    @GET("pincodes")
    suspend fun getPincodes(
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("searchText") searchText: String,
        @Query("page") page: Int,
        @Query("count") count: Int
    ): GetPincodeResponse

    @POST("guardian/sendotp")
    suspend fun generateOtpGuardianJoin(@Body data: Map<String, Any>): GenericResponse

    @POST("guardian/register")
    suspend fun registerGuardian(@Body data: Map<String, Any>): LoginResponse


    @GET("courseproviders")
    suspend fun getCourseProviders(
        @Query("userId") userId: Int,
        @Query("userType") userType: String
    ): CourseProviderResponse

    @GET("explore/schools/detail")
    suspend fun getExploreDigitalSchool(
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("grade") grade: Int,
        @Query("courseProviderId") courseProviderId: Int
    ): ExploreDigitalSchoolResponse

    @GET("languages")
    suspend fun getLanguages(): LanguagesResponse

    @POST("guardian/enroll_student")
    suspend fun enrollStudents(@Body data: Map<String, Any>): GenericResponse

    @GET("grades")
    suspend fun getGrades(
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("courseProviderId") courseProviderId: Int
    ): GradesResponse

    @POST("updatetoken")
    suspend fun updateFCMToken(@Body data: Map<String, Any>): GenericResponse

    @POST("guadian/update")
    suspend fun updateLocationDetails(@Body data: Map<String, Any>): GenericResponse

    @POST("content/update-download-status")
    suspend fun updateContentDownloadStatus(@Body data: Map<String, Any>): GenericResponse
}