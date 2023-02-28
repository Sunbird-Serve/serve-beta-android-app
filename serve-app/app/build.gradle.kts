plugins {
    id ("com.android.application")
    kotlin ("android")
    id ("kotlin-android-extensions")
    id ("dagger.hilt.android.plugin")
    id ("com.google.gms.google-services")
    id ("com.google.firebase.crashlytics")
    kotlin ("kapt")
    id("kotlin-android")
}

android {
    /* signingConfigs {
        getByName("debug") {
            storeFile = file("C:\\evidyaloka_debug")
            //storeFile = file("D:\\Datamatics Workspace\\eVidyaloka Release\\evidyaloka_debug")
            storePassword = "android"
            keyAlias = "evidyaloka_debug"
            keyPassword = "android"
        }
    }*/
    compileSdkVersion( Config.compileSdkVersion)
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        applicationId = Config.applicationId
        minSdkVersion (Config.minSdkVersion)
        targetSdkVersion (Config.targetSdkVersion)
        versionCode = Config.versionCode
        versionName = Config.versionName

        buildConfigField( "String", "TERMS_AND_CONDITIONS_URL", "\"https://www.evidyaloka.org/policies_terms_conditions\"")
        buildConfigField( "String", "ABOUT_US_URL", "\"https://www.evidyaloka.org/about/\"")
        buildConfigField( "String", "CONTACT_US_URL", "\"https://www.evidyaloka.org/contact_us/\"")
        buildConfigField( "String", "PRIVACY_POLICY", "\"https://www.evidyaloka.org/policies/\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        signingConfig = signingConfigs.getByName("debug")
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
//            buildConfigField 'String', 'BASE_URL', "\"https://uat2.evidyaloka.org/partner/api/v1/\""
//            buildConfigField 'String', 'API_KEY', "\"kMobilePartnerAndroidApp\""
//            buildConfigField 'String', 'API_PASSWORD', "\"6c41e1058c74d9a085a9466002ff3049\""
//            buildConfigField 'String', 'BASE_IMG_URL', "\"https://uat2.evidyaloka.org/\""
            buildConfigField("String", "BASE_URL", "\"https://serve.evidyaloka.org/partner/api/v1/\"")
            buildConfigField("String", "BASE_IMG_URL", "\"https://serve.evidyaloka.org/\"")
            buildConfigField( "String", "API_KEY", "\"kMobilePartnerAndroidApp\"")
            buildConfigField ("String", "API_PASSWORD", "\"6c41e1058c74d9a085a9466002ff3049\"")
            buildConfigField ("String", Config.Url.Key.VOLUNTEER_EXPLORE , Config.Url.Debug.VOLUNTEER_EXPLORE)
            signingConfig = signingConfigs.getByName("debug")

        }
        getByName("release") {
            isMinifyEnabled = true
            isShrinkResources = false
            proguardFiles( getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            buildConfigField ("String", "BASE_URL", "\"https://serve.evidyaloka.org/partner/api/v1/\"")
            buildConfigField ("String", "BASE_IMG_URL", "\"https://serve.evidyaloka.org/\"")
            buildConfigField ("String", "API_KEY", "\"kProdMobileEvdAndroidApp\"")
            buildConfigField ("String", "API_PASSWORD", "\"6c41e1058c74d9a085a9466002ff3049\"")
            buildConfigField ("String", Config.Url.Key.VOLUNTEER_EXPLORE , Config.Url.Debug.VOLUNTEER_EXPLORE)

        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
    buildFeatures {
        viewBinding = true
    }
    kapt {
        correctErrorTypes = true
    }
}

dependencies {
    implementation (project(Module.partner))
    implementation (project(Module.student))
    implementation (project(Module.core))
    api (project(Module.common))

    implementation(Dependencies.hilt_android)
    implementation(Dependencies.hilt_work)
    kapt(Dependencies.hilt_android_compiler)
    kapt(Dependencies.hilt_compiler)


}



apply {
    plugin("androidx.navigation.safeargs.kotlin")
}
