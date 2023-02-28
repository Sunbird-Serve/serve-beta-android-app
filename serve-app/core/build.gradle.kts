plugins {
    id ("com.android.library")
    kotlin ("android")
    id ("kotlin-android-extensions")
    id ("dagger.hilt.android.plugin")
    kotlin ("kapt")
    id("kotlin-android")
}

android {
    compileSdkVersion( 30)
    buildToolsVersion = "29.0.3"

    defaultConfig {
        minSdkVersion(21)
        targetSdkVersion(30)
        //versionCode = 1
       // versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", Config.Url.Key.BASE_URL, Config.Url.Prod.BASE_URL)
            buildConfigField("String", Config.Url.Key.PARTNER_URL, Config.Url.Prod.PARTNER_URL)
            buildConfigField("String", Config.Url.Key.STUDENT_URL, Config.Url.Prod.STUDENT_URL)
            buildConfigField("String", Config.Url.Key.BASE_IMG_URL, Config.Url.Prod.BASE_IMG_URL)
            buildConfigField( "String", Config.API.Key.API_KEY, Config.API.Value.Prod.API_KEY)
            buildConfigField ("String", Config.API.Key.API_PASSWORD, Config.API.Value.Prod.API_PASSWORD)
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField("String", Config.Url.Key.BASE_URL, Config.Url.Debug.BASE_URL)
            buildConfigField("String", Config.Url.Key.PARTNER_URL, Config.Url.Debug.PARTNER_URL)
            buildConfigField("String", Config.Url.Key.STUDENT_URL, Config.Url.Debug.STUDENT_URL)
            buildConfigField("String", Config.Url.Key.BASE_IMG_URL, Config.Url.Debug.BASE_IMG_URL)
            buildConfigField( "String", Config.API.Key.API_KEY, Config.API.Value.Debug.API_KEY)
            buildConfigField ("String", Config.API.Key.API_PASSWORD, Config.API.Value.Debug.API_PASSWORD)
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation (Dependencies.kotlinStdlib)
    implementation (Dependencies.Androidx.core)
    implementation (Dependencies.Androidx.paging)
    implementation (Dependencies.hilt_android)
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    implementation(Dependencies.Androidx.constraintlayout)
    kapt (Dependencies.hilt_android_compiler)
    kapt (Dependencies.hilt_compiler)

    implementation (Dependencies.coroutines)

    //okhttp
    implementation (Dependencies.okhttp)
    implementation (Dependencies.okhttp_iterceptor)

    //Retrofit
    implementation (Dependencies.retrofit)
    implementation (Dependencies.adapter_rxjava2)
    implementation (Dependencies.converter_gson)
    implementation (Dependencies.gson)
    implementation (Dependencies.firebase_crashlytics)
    implementation (Dependencies.firebase_analytics)

    //Room
    implementation(Dependencies.room)
    kapt(Dependencies.room_kapt)
    implementation(Dependencies.room_ktx)

}