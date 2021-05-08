package appdependencies

object ClassPath {

    const val gradle = "com.android.tools.build:gradle:${Versions.gradle}"
    const val kotlingradle = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
    const val google = "com.google.gms:google-services:${Versions.google}"
    const val navisafe = "androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navisafe}"
    const val mavenplugin = "com.github.dcendents:android-maven-gradle-plugin:${Versions.maven}"
    const val dokkaplugin = "org.jetbrains.dokka:dokka-gradle-plugin:${Versions.dokka}"
}