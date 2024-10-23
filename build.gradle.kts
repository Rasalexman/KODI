import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile

// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.kotlinMultiplatform) apply  false
    alias(libs.plugins.vanniktech.mavenPublish) apply false
}

buildscript {
    apply(from="versions.gradle.kts")
    val navigation: String by extra
    val jitpackPath: String by extra
    val pluginsPath: String by extra

    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri(jitpackPath) }
        maven { url = uri(pluginsPath) }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:${libs.versions.androidGradlePlugin.get()}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${libs.versions.kotlin.get()}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:$navigation")

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    apply(from="${rootDir}/versions.gradle.kts")
//    tasks.withType<KotlinJvmCompile>().configureEach {
//        compilerOptions {
//            apiVersion.set(KotlinVersion.KOTLIN_2_0)
//            languageVersion.set(KotlinVersion.KOTLIN_2_0)
//            jvmTarget.set(JvmTarget.JVM_21)
//            freeCompilerArgs.addAll(listOf(
//                "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
//                "-opt-in=kotlin.RequiresOptIn"
//            ))
//        }
//    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}
