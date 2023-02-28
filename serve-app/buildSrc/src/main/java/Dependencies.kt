object Version {
    const val kotlin = "1.4.32"
    const val gradle = "4.1.0"
    const val hilt = "2.38.1"
    const val navVersion = "2.3.2"
    const val googleServicesVersion = "4.3.8"
    const val crashlyticsPluginVersion= "2.5.2"

    const val hilt_version = "2.38.1"
    const val hilt_viewmodels = "1.0.0-alpha03"
    const val hilt_compiler = "1.0.0"
    const val hilt_work = "1.0.0"
    const val lifecycle = "2.3.0"
    const val coroutines = "1.4.1"
    const val retrofit_version = "2.6.1"
    const val gson_version = "2.8.9"
    const val okhttp_version = "4.9.0"
    const val glide_version = "4.11.0"
    const val navigation_version = "2.3.3"
    const val fragment_ktx = "1.3.0-rc02"
    const val lifecycle_viewmodel_version = "2.3.0"
    const val easy_permission_version = "3.0.0"
    const val pager_indicator = "1.3"
    const val acraVersion = "5.7.0"
    const val authVersion = "17.0.0"
    const val authApiPhoneVersion = "17.0.0"
    const val pdfViewerversion = "2.8.2"
    const val minddevPdfViewerversion = "1.0.4"
    const val firebase_bom_version = "28.2.0"
    const val firebase_crashlytics_version = "17.4.1"
    const val firebase_analytics_version = "18.0.3"
    const val firebase_messaging_version ="22.0.0"
    const val location = "18.0.0"
    const val room_version = "2.3.0"


    object Androidx {
        const val core = "1.3.2"
        const val appCompat = "1.2.0"
        const val coordinatorLayout = "1.1.0"
        const val constraintlayout = "2.0.4"
        const val legacy_support = "1.0.0"
        const val preference = "1.1.1"
        const val cardview = "1.0.0"
        const val viewpager2 = "1.0.0"
        const val paging = "2.1.2"
        const  val work_version = "2.6.0"
    }
}

object Dependencies {
    //plugins
    const val kotlinStdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"
    const val kotlinPlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"
    const val androidPlugin = "com.android.tools.build:gradle:${Version.gradle}"
    const val hilt_gradle_plugin = "com.google.dagger:hilt-android-gradle-plugin:${Version.hilt}"
    const val navigation_plugin =
        "androidx.navigation:navigation-safe-args-gradle-plugin:${Version.navVersion}"
    const val google_services_plugin =
        "com.google.gms:google-services:${Version.googleServicesVersion}"
    const val crashlytics_plugin =
        "com.google.firebase:firebase-crashlytics-gradle:${Version.crashlyticsPluginVersion}"

    //UI
    const val material = "com.google.android.material:material:1.2.1"
    const val compose_material = "androidx.compose.material:material:1.0.0-alpha09"

    //Hilt
    const val hilt_android = "com.google.dagger:hilt-android:${Version.hilt_version}"
    const val hilt_android_compiler =
        "com.google.dagger:hilt-android-compiler:${Version.hilt_version}"
    const val hilt_lifecycle = "androidx.hilt:hilt-lifecycle-viewmodel:${Version.hilt_viewmodels}"
    const val hilt_compiler = "androidx.hilt:hilt-compiler:${Version.hilt_compiler}"
    const val hilt_work = "androidx.hilt:hilt-work:${Version.hilt_work}"

    //Retrofit
    const val retrofit = "com.squareup.retrofit2:retrofit:${Version.retrofit_version}"
    const val adapter_rxjava2 = "com.squareup.retrofit2:adapter-rxjava2:${Version.retrofit_version}"
    const val converter_gson = "com.squareup.retrofit2:converter-gson:${Version.retrofit_version}"

    //Room
    const val room = "androidx.room:room-runtime:${Version.room_version}"
    const val room_kapt = "androidx.room:room-compiler:${Version.room_version}"
    const val room_ktx = "androidx.room:room-ktx:${Version.room_version}"

    //gson
    const val gson = "com.google.code.gson:gson:${Version.gson_version}"

    //okhttp
    const val okhttp = "com.squareup.okhttp3:okhttp:${Version.okhttp_version}"
    const val okhttp_iterceptor =
        "com.squareup.okhttp3:logging-interceptor:${Version.okhttp_version}"

    //Glide
    const val glide = "com.github.bumptech.glide:glide:${Version.glide_version}"
    const val glide_compiler = "com.github.bumptech.glide:compiler:${Version.glide_version}"

    //coroutines
    const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Version.coroutines}"

    //permissions (google samples link: https://github.com/googlesamples/easypermissions)
    const val easypermissions = "pub.devrel:easypermissions:${Version.easy_permission_version}"

    //RecyclerView Pager Indicator
    const val pager_indicator =
        "com.github.rbro112:Android-Indefinite-Pager-Indicator:${Version.pager_indicator}"

    //crash reporter
    const val acra_mail = "ch.acra:acra-mail:${Version.acraVersion}"
    const val acra_toast = "ch.acra:acra-toast:${Version.acraVersion}"

    //Firebase
    const val firebase_bom = "com.google.firebase:firebase-bom:${Version.firebase_bom_version}"
    const val firebase_crashlytics = "com.google.firebase:firebase-crashlytics-ktx:${Version.firebase_crashlytics_version}"
    const val firebase_analytics = "com.google.firebase:firebase-analytics-ktx:${Version.firebase_analytics_version}"
    const val firebase_messaging = "com.google.firebase:firebase-messaging-ktx:${Version.firebase_messaging_version}"

    object Androidx {
        const val core = "androidx.core:core-ktx:${Version.Androidx.core}"
        const val appCompat = "androidx.appcompat:appcompat:${Version.Androidx.appCompat}"
        const val coordinatorlayout =
            "androidx.coordinatorlayout:coordinatorlayout:${Version.Androidx.coordinatorLayout}"
        const val constraintlayout =
            "androidx.constraintlayout:constraintlayout:${Version.Androidx.constraintlayout}"
        const val legacy_support =
            "androidx.legacy:legacy-support-v4:${Version.Androidx.legacy_support}"
        const val preference = "androidx.preference:preference:${Version.Androidx.preference}"

        //cardview
        const val cardview = "androidx.cardview:cardview:${Version.Androidx.cardview}"
        const val viewpager2 = "androidx.viewpager2:viewpager2:${Version.Androidx.viewpager2}"
        //navigation
        const val navigation_fragment =
            "androidx.navigation:navigation-fragment-ktx:${Version.navigation_version}"
        const val navigation_ui_ktx =
            "androidx.navigation:navigation-ui-ktx:${Version.navigation_version}"
        const val fragment_ktx = "androidx.fragment:fragment-ktx:${Version.fragment_ktx}"

        //lifecycle
        const val lifecycle_livedata = "androidx.lifecycle:lifecycle-livedata-ktx:${Version.lifecycle}"
        const val lifecycle_viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Version.lifecycle_viewmodel_version}"
        //paging library
        const val paging =  "androidx.paging:paging-runtime-ktx:${Version.Androidx.paging}"

        //Workmanager
        const val work_manager = "androidx.work:work-runtime-ktx:${Version.Androidx.work_version}"
        const val work_gcm = "androidx.work:work-gcm:${Version.Androidx.work_version}"
        const val work_multiprocess = "androidx.work:work-multiprocess:${Version.Androidx.work_version}"
    }

    //One tap SMS verification
    const val auth = "com.google.android.gms:play-services-auth:${Version.authVersion}"
    const val auth_api_phone = "com.google.android.gms:play-services-auth-api-phone:${Version.authApiPhoneVersion}"
    //const val pdf_viewer = "com.github.barteksc:android-pdf-viewer:${Version.pdfViewerversion}"
    const val minddev_pdf_viewer = "com.github.mkw8263:MindevPDFViewer:${Version.minddevPdfViewerversion}"

    const val location = "com.google.android.gms:play-services-location:${Version.location}"


}

