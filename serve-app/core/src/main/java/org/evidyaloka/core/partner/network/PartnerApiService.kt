package org.evidyaloka.core.partner.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.evidyaloka.core.partner.network.entity.*
import org.evidyaloka.core.partner.network.entity.course.academic.AcademicYearResponse
import org.evidyaloka.core.partner.network.entity.course.course.CourseResponse
import org.evidyaloka.core.partner.network.entity.course.grades.GradesResponse
import org.evidyaloka.core.partner.network.entity.course.offerings.CourseOfferingResponse
import org.evidyaloka.core.partner.network.entity.course.provider.CourseProviderResponse
import org.evidyaloka.core.partner.network.entity.ping.PingResponse
import org.evidyaloka.core.partner.network.entity.student.EnrolledStudentsResponse
import org.evidyaloka.core.partner.network.entity.student.StudentResponse
import retrofit2.http.*

/**
 * @author Madhankumar
 * created on 26-12-2020
 *
 */
@JvmSuppressWildcards
interface PartnerApiService {

    @POST("login")
    suspend fun doLogin(@Body data: Map<String, Any>): LoginResponse

    @POST("register")
    suspend fun doRegister(@Body data: Map<String, Any>): GenericResponse

    @POST("reset_password")
    suspend fun changePassword(@Body data: Map<String, Any>): GenericResponse

    @GET("profile/{id}")
    suspend fun getProfile(@Path("id") id: Int): UserResponse

    @PUT("profile/{id}")
    suspend fun saveProfile(@Path("id") id: Int, @Body data: Map<String, Any>): GenericResponse

    @GET("school")
    suspend fun getUserSchoolList(
        @Query("userType") userType: String,
        @Query("partnerId") partnerId: Int
    ): SchoolsResponse

    @POST("users/assignrole")
    suspend fun assignDsmToSchool(
        @Query("userId") dsmId: Int,
        @Query("schoolId") schoolId: Int
    ): GenericResponse

    @POST("school")
    suspend fun submitDigitalSchool(@Body data: Map<String, Any>): GenericResponse

    @PUT("school/{id}")
    suspend fun updateDigitalSchool(
        @Path("id") id: Int,
        @Body data: Map<String, Any>
    ): GenericResponse

    @GET("school")
    suspend fun getDigitalSchool(
        @Query("userType") userType: String,
        @Query("partnerId") partnerId: Int,
        @Query("schoolId") id: Int
    ): SchoolsResponse

    @GET("users")
    suspend fun getUserList(): UserListResponse

    @Multipart
    @POST("documents")
    suspend fun uploadFile(
        @Query("doc_type") docType: Int,
        @Query("format") format: Int,
        @Part("filename") filename: RequestBody,
        @Part file: MultipartBody.Part
    ): UploadDocumentResponse

    /*
     * Update User is to update DSM user
     * @param userId, pass Dsm Id
     */

    @PUT("users/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Int,
        @Body data: Map<String, Any>
    ): GenericResponse

    /**
     * Get All the users
     * @param userType String Type of User[DSM/DSP]
     * @param partnerId String partnerId of current user
     * @param page Int Current Page number
     * @param count Int number of items to shown in a single page
     * @param searchKey String key[name] to search for
     * @return List<UserEntity>
     */
    @GET("users")
    suspend fun getAllUsers(
        @Query("userType") userType: String, @Query("partnerId") partnerId: Int,
        @Query("page") page: Int, @Query("count") count: Int,
        @Query("searchKey") searchKey: String
    ): UserListResponse

    /**
     * Get the details of a user
     * @param userType String Type of User[DSM/DSP]
     * @param partnerId String partnerId of current user
     * @param userId Int id of user for which details are required
     * @return List<UserEntity>
     */
    @GET("users")
    suspend fun getUserDetail(
        @Query("userType") userType: String,
        @Query("partnerId") partnerId: Int,
        @Query("userId") userId: Int
    ): UserListResponse

    /*
     * To get DSM user Details by passing @param userId related to DSM
     */

    @GET("user/{userId}")
    suspend fun getUser(@Path("userId") userId: Int): UserResponse

    /**
     * Creates a DSM user for the partner.
     * @param data Map<String, Any> request body
     * @return Generic
     */
    @POST("users")
    suspend fun addUser(@Body data: Map<String, Any>): GenericResponse

    /*
     * Gets List of course providers
     * @param code String; courseProvider by default null
     * @return List<CourseProvider>
     */

    @GET("courseprovider")
    suspend fun getCourseProvider(@Query("code") code: String? = null): CourseProviderResponse

    @GET("grades")
    suspend fun getGrades(
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("courseProviderId") courseProviderId: Int
    ): GradesResponse

    /*
     * Fetches List of Academic Years for a course provider
     * @param courseProviderId Int; denotes choosen courseProvider's id
     * @return List<AcademicYear>
     */
    @GET("academic")
    suspend fun getAcademicYear(@Query("courseProviderId") id: Int): AcademicYearResponse

    /*
    * Fetches List of Course
    * @param grade Int; denotes grade choosen
    * @param CourseProviderCode String; denotes choosen course provider's code
    * @return List<Course>
    */
    @GET("course")
    suspend fun getCourse(
        @Query("grade") grade: Int,
        @Query("courseProvider") CourseProviderCode: String,
        @Query("languageId") languageId: Int? = null
    ): CourseResponse

    /*
   * Fetches List of CourseOffering
   * @param grade Int; denotes grade chosen
   * @param digitalSchoolId Int; denotes Digital School's Id
   * @param AcademicYearId Int; denotes chosen Academic Year's Id choosen
   * @param courseProviderId Int; denotes chosen course provider's id
   * @return List<CourseOffering>
   */
    @GET("offering")
    suspend fun getCourseOfferings(
        @Query("courseProviderId") courseProviderId: Int?,
        @Query("digitalSchoolId") digitalSchoolId: Int,
        @Query("grade") grade: Int?,
        @Query("AcademicYearId") AcademicYearId: Int?,
        @Query("languageId") languageId: Int?
    ): CourseOfferingResponse

    @GET("students")
    suspend fun getEnrolledStudents(
        @Query("userType") userType: String,
        @Query("courseProviderId") courseProviderId: Int?,
        @Query("digitalSchoolId") digitalSchoolId: Int,
        @Query("grade") grade: Int?,
        @Query("offeringId") offeringId: Int?,
        @Query("academicYearId") academicYearId: Int?,
        @Query("page") page: Int, @Query("count") count: Int = 50
    ): EnrolledStudentsResponse

    @POST("student/enroll")
    suspend fun enrolledStudents(@Body data: Map<String, Any>): GenericResponse

    @PUT("student/{studentId}")
    suspend fun updateStudents(
        @Path("studentId") studentId: Int,
        @Query("digitalSchoolId") digitalSchoolId: Int,
        @Body data: Map<String, Any>
    ): GenericResponse

    @GET("student/{studentId}")
    suspend fun getStudentDetails(
        @Path("studentId") studentId: Int,
        @Query("digitalSchoolId") digitalSchoolId: Int
    ): StudentResponse

    @POST("offering")
    suspend fun addCourse(@Body data: Map<String, Any>): GenericResponse

    @GET("checkcourse")
    suspend fun checkCourse(
        @Query("courseId") schoolId: Int,
        @Query("schoolId") courseId: Int,
        @Query("userId") userId: Int
    ): CheckCourseResponse

    @GET("languages")
    suspend fun getLanguages(): LanguagesResponse

    /*PING*/
    @POST("ping")
    suspend fun ping(@Body data: Map<String, Any>): PingResponse

    /*Logout*/
    @POST("logout")
    suspend fun logout(@Body data: Map<String, Any>): GenericResponse

    @GET("states")
    suspend fun getStateList(): StateListResponse

    @GET("pincodes")
    suspend fun getPincode(
        @Query("stateId") stateId: Int,
        @Query("searchText") searchText: String?,
        @Query("partnerId") partnerId: Int,
        @Query("page") page: Int,
        @Query("count") count: Int
    ): PinCodeResponse

    @POST("schoollocation/add")
    suspend fun saveState(@Body data: Map<String, Any>): GenericResponse

    @POST("schoollocation/delete")
    suspend fun deleteLocation(@Body data: Map<String, Any>): GenericResponse

    @POST("b2c/send_otp")
    suspend fun sendOtp(@Body data: Map<String, Any>): GenericResponse

    @POST("b2c/register")
    suspend fun verifyOtp(@Body data: Map<String, Any>): LoginResponse

    @GET("content/detail")
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

    @GET("explore/schools/detail")
    suspend fun getExploreDigitalSchool(
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("grade") grade: Int,
        @Query("courseProviderId") courseProviderId: Int
    ): ExploreDigitalSchoolResponse

    @GET("subject/detail")
    suspend fun getSubjectDetails(
        @Query("studentId") studentId: Int?,
        @Query("userId") userId: Int,
        @Query("userType") userType: String,
        @Query("offeringId") offeringId: Int,
        @Query("courseId") courseId: Int
    ): SubjectDetailsResponse

    @POST("b2c/update_details")
    suspend fun updateRegister(@Body data: Map<String, Any>): GenericResponse

    @POST("updatetoken")
    suspend fun updateFCMToken(@Body data: Map<String, Any>): GenericResponse

    @GET("students/promotelist")
    suspend fun getStudentsForPromotion(
        @Query("userType") userType: String,
        @Query("courseProviderId") courseProviderId: Int?,
        @Query("digitalSchoolId") digitalSchoolId: Int,
        @Query("grade") grade: Int?,
        @Query("offeringId") offeringId: Int?,
        @Query("filter") filter: String?, @Query("page") page: Int, @Query("count") count: Int = 50
    ): EnrolledStudentsResponse

    @POST("students/promotion")
    suspend fun promoteStudents(@Body data: Map<String, Any>): GenericResponse
}