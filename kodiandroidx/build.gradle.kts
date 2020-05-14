import appdependencies.Builds.COMPILE_VERSION
import appdependencies.Builds.MIN_VERSION
import appdependencies.Builds.TARGET_VERSION
import appdependencies.Libs
import appdependencies.Versions
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL
import org.jetbrains.dokka.gradle.DokkaTask
import resources.Resources.codeDirs
import resources.Resources.resDirs

plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    id("androidx.navigation.safeargs.kotlin")
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka")
    id("maven-publish")
}

android {
    compileSdkVersion(COMPILE_VERSION)
    defaultConfig {
        minSdkVersion(MIN_VERSION)
        targetSdkVersion(TARGET_VERSION)
        versionCode = appdependencies.Builds.KodiAndroidX.VERSION_CODE
        versionName = appdependencies.Builds.KodiAndroidX.VERSION_NAME
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            java.setSrcDirs(codeDirs)
            res.setSrcDirs(resDirs)
        }
    }

    dexOptions {
        javaMaxHeapSize = "4g"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    tasks.withType<KotlinCompile>().all {
        kotlinOptions.suppressWarnings = true
        kotlinOptions.jvmTarget = "1.8"
        kotlinOptions.noReflect = true
        kotlinOptions.freeCompilerArgs += listOf(
                "-XXLanguage:+InlineClasses"
        )
    }

    packagingOptions {
        exclude("META-INF/notice.txt")
    }

    // Declare the task that will monitor all configurations.
    configurations.all {
        // 2 Define the resolution strategy in case of conflicts.
        resolutionStrategy {
            // Fail eagerly on version conflict (includes transitive dependencies),
            // e.g., multiple different versions of the same dependency (group and name are equal).
            failOnVersionConflict()

            // Prefer modules that are part of this build (multi-project or composite build) over external modules.
            preferProjectModules()
        }
    }

    androidExtensions {
        isExperimental = true
        defaultCacheImplementation = org.jetbrains.kotlin.gradle.internal.CacheImplementation.HASH_MAP
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", Versions.kotlin))

    implementation(Libs.Core.coreKtx)
    api(project(":kodi"))

    testImplementation(Libs.Tests.junit)
    androidTestImplementation(Libs.Tests.runner)
    androidTestImplementation(Libs.Tests.espresso)
}

tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
        configuration {
            externalDocumentationLink {
                noJdkLink = true
                noAndroidSdkLink = true
                noStdlibLink = true
                packageListUrl = URL("https://kotlinlang.org/api/latest/jvm/stdlib/package-list")
            }
        }
    }
}

repositories {
    mavenCentral()
}
// comment it if you fork this project
apply {
    from("deploy.gradle")
}
