// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {

    val kotlin_version by extra("1.4.32")
    repositories {
        google()
        jcenter()

    }
    dependencies {
        classpath(Dependencies.androidPlugin)
        classpath(Dependencies.kotlinPlugin)
        classpath(Dependencies.hilt_gradle_plugin)
        classpath(Dependencies.navigation_plugin)
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath (Dependencies.google_services_plugin)
        classpath (Dependencies.crashlytics_plugin)
       // "classpath"("com.android.tools.build:gradle:7.3.0")
    }


}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri(Config.Repositories.gradleMaven) }
    }
}

tasks {
    val clean by registering(Delete::class) {
        delete(buildDir)
    }
}
