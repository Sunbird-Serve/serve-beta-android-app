package org.evidyaloka.core

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.evidyaloka.core.Constants.PartnerConst
import org.evidyaloka.core.partner.network.PartnerApiService
import org.evidyaloka.core.partner.network.entity.ErrorData
import org.evidyaloka.core.student.database.db.CourseDB
import org.evidyaloka.core.student.database.db.MIGRATION_4_5
import org.evidyaloka.core.student.network.StudentApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

/**
 * @author Madhankumar
 * created on 24-12-2020
 *
 */
@InstallIn(SingletonComponent::class)
@Module
object
AppModule {

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthTokenString

    private val errorResponse: MutableLiveData<ErrorData> = MutableLiveData()

    @Singleton
    @Provides
    fun getErrorResponse(): MutableLiveData<ErrorData> =
        errorResponse

    @Singleton
    @Provides
    @AuthTokenString
    fun getAuthToken(): String {
        var data = ByteArray(0)
        try {
            data =
                (BuildConfig.API_KEY + ":" + BuildConfig.API_PASSWORD).toByteArray(
                    charset("UTF-8")
                )
        } catch (e: UnsupportedEncodingException) {
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        }
        return "Basic " + Base64.encodeToString(data, Base64.NO_WRAP)
    }


    @Singleton
    @Provides
    fun getInterceptor(): HttpLoggingInterceptor {
        var httpLoggingInterceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        }
        return httpLoggingInterceptor
    }

    @Singleton
    @Provides
    fun provideGsonBuilder(): Gson {
        return GsonBuilder()
            .create()
        //TODO .excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create()
    }

    @Singleton
    @Provides
    fun getHeaderInterceptor(
        prefs: SharedPreferences,
        @AuthTokenString authToken: String
    ): Interceptor {
        return object : Interceptor {
            @Throws(IOException::class)
            override fun intercept(chain: Interceptor.Chain): Response {
                val request =
                    chain.request().newBuilder()
                prefs.get<String>(PartnerConst.SESSION_ID)?.let { sessionId ->
                    request.header(PartnerConst.SESSION_ID, sessionId)
                }
                request.header(PartnerConst.AUTH_BASIC, authToken)
                val response = chain.proceed(request.build())
                if (response.code == 401 || response.code == 403) {
                    errorResponse.postValue(ErrorData(response.code, "Unauthorized request"))
                }
                return response
            }
        }
    }

    @Singleton
    @Provides
    fun provideOkHttp(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        networkInterceptor: Interceptor
    ): OkHttpClient = (OkHttpClient().newBuilder()
        .connectTimeout(120, TimeUnit.SECONDS)
        .readTimeout(120, TimeUnit.SECONDS)
        .writeTimeout(120, TimeUnit.SECONDS))
        .addNetworkInterceptor(networkInterceptor)
        .addInterceptor(httpLoggingInterceptor)
        .build()

    @Singleton
    @Provides
    fun providePartnerApiService(gson: Gson, okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL.plus(BuildConfig.PARTNER_URL))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(PartnerApiService::class.java)

    @Singleton
    @Provides
    fun provideStudentApiService(gson: Gson, okHttpClient: OkHttpClient) = Retrofit.Builder()
        .baseUrl(BuildConfig.BASE_URL.plus(BuildConfig.STUDENT_URL))
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()
        .create(StudentApiService::class.java)

    @Singleton
    @Provides
    fun provideCourseDatabase(context: Application) = Room.databaseBuilder(
        context,
        CourseDB::class.java,
        "CourseContent-DB"
    ).addMigrations(MIGRATION_4_5).build().courseContentDAO()

    @Singleton
    @Provides
    fun preference(context: Application): SharedPreferences =
        context.getSharedPreferences("eVidyalokaPref", Context.MODE_PRIVATE)


    @Singleton
    @Provides
    fun getContext(context: Application): Context = context
}

