plugins {
    id("com.android.library")
    kotlin("android")
    id("kotlin-android-extensions")
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
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
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

    annotationProcessor(Dependencies.glide_compiler)
    api("com.github.YarikSOffice:lingver:1.3.0")
    api("androidx.appcompat:appcompat:1.2.0")
    api("androidx.constraintlayout:constraintlayout:2.0.4")
    api("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    api(Dependencies.Androidx.appCompat)
    api(Dependencies.Androidx.cardview)
    api(Dependencies.Androidx.constraintlayout)
    api(Dependencies.Androidx.coordinatorlayout)
    api(Dependencies.Androidx.core)
    api(Dependencies.Androidx.fragment_ktx)
    api(Dependencies.Androidx.legacy_support)
    api(Dependencies.Androidx.lifecycle_livedata)
    api(Dependencies.Androidx.lifecycle_viewmodel)
    api(Dependencies.Androidx.navigation_fragment)
    api(Dependencies.Androidx.navigation_ui_ktx)
    api(Dependencies.Androidx.paging)
    api(Dependencies.Androidx.preference)
    api(Dependencies.Androidx.viewpager2)
    api(Dependencies.acra_mail)
    api(Dependencies.acra_toast)
    api(Dependencies.adapter_rxjava2)
    api(Dependencies.auth)
    api(Dependencies.auth_api_phone)
    api(Dependencies.compose_material)
    api(Dependencies.converter_gson)
    api(Dependencies.coroutines)
    api(Dependencies.firebase_analytics)
    api(Dependencies.firebase_crashlytics)
    api(Dependencies.firebase_messaging)
    api(Dependencies.glide)
    api(Dependencies.gson)
    api(Dependencies.hilt_android)
    api(Dependencies.kotlinStdlib)
    api(Dependencies.location)
    api(Dependencies.material)
    api(Dependencies.minddev_pdf_viewer)
    api(Dependencies.okhttp)
    api(Dependencies.okhttp_iterceptor)
    api(Dependencies.pager_indicator)
    api(Dependencies.retrofit)
    kapt(Dependencies.hilt_android_compiler)
    kapt(Dependencies.hilt_compiler)

    implementation(project(Module.core))
    implementation(project(Module.player))
    api(Dependencies.Androidx.work_manager)
    api(Dependencies.Androidx.work_gcm)
    api(Dependencies.Androidx.work_multiprocess)
}

apply {
    plugin("androidx.navigation.safeargs.kotlin")
}