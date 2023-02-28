plugins {
    id ("com.android.library")
    kotlin ("android")
}

android {
    compileSdkVersion( Config.compileSdkVersion)
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        minSdkVersion (Config.minSdkVersion)
        targetSdkVersion (Config.targetSdkVersion)
        versionCode = Config.versionCode
        versionName = Config.versionNameString

        buildConfigField( "Integer", "VersionCode", Config.versionCode.toString())
        buildConfigField( "String", "VersionName", Config.versionNameString)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
    implementation (Dependencies.Androidx.appCompat)
    //okhttp
    implementation (Dependencies.okhttp)
    implementation (Dependencies.okhttp_iterceptor)
    implementation ("com.google.android.exoplayer:exoplayer:2.11.8")

    implementation (Dependencies.firebase_crashlytics)
    implementation (Dependencies.firebase_analytics)

}

apply {
    plugin("androidx.navigation.safeargs.kotlin")
}