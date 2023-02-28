plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
    id("dagger.hilt.android.plugin")
    kotlin("kapt")
    id("kotlin-android")
}

android {
    compileSdkVersion(Config.compileSdkVersion)
    buildToolsVersion = Config.buildToolsVersion

    defaultConfig {
        minSdkVersion(Config.minSdkVersion)
        targetSdkVersion(Config.targetSdkVersion)
        versionCode = Config.versionCode
        versionName = Config.versionNameString

        buildConfigField("Integer", "VersionCode", Config.versionCode.toString())
        buildConfigField("String", "VersionName", Config.versionNameString)
        buildConfigField(
            "String",
            Config.Url.Key.TERMS_AND_CONDITIONS_URL,
            Config.Url.Prod.TERMS_AND_CONDITIONS_URL
        )
        buildConfigField("String", Config.Url.Key.ABOUT_US_URL, Config.Url.Prod.ABOUT_US_URL)
        buildConfigField("String", Config.Url.Key.CONTACT_US_URL, Config.Url.Prod.CONTACT_US_URL)
        buildConfigField("String", Config.Url.Key.PRIVACY_POLICY_URL, Config.Url.Prod.PRIVACY_POLICY_URL)

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", Config.Url.Key.BASE_IMG_URL, Config.Url.Debug.BASE_IMG_URL)
            buildConfigField("String", Config.Url.Key.FORGET_PASSWORD_URL, Config.Url.Debug.FORGET_PASSWORD_URL)
            buildConfigField( "String", Config.Url.Key.FAQ, Config.Url.Debug.FAQ)
            buildConfigField( "String", Config.Url.Key.P_LOUNGE, Config.Url.Prod.P_LOUNGE)
        }
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", Config.Url.Key.BASE_IMG_URL, Config.Url.Prod.BASE_IMG_URL)
            buildConfigField("String", Config.Url.Key.FORGET_PASSWORD_URL, Config.Url.Prod.FORGET_PASSWORD_URL)
            buildConfigField( "String", Config.Url.Key.FAQ, Config.Url.Prod.FAQ)
            buildConfigField( "String", Config.Url.Key.P_LOUNGE, Config.Url.Prod.P_LOUNGE)
        }

    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(project(Module.core))
    api(project(Module.common))

    implementation(Dependencies.hilt_android)
    kapt(Dependencies.hilt_android_compiler)
    kapt(Dependencies.hilt_compiler)
}
apply {
    plugin("androidx.navigation.safeargs.kotlin")
}