import appdependencies.Builds.COMPILE_VERSION
import appdependencies.Builds.MIN_VERSION
import appdependencies.Builds.TARGET_VERSION
import appdependencies.Libs
import appdependencies.Versions
import resources.Resources.codeDirs

plugins {
    id("com.android.library")
    kotlin("android")
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
        }
    }

    /*dexOptions {
        javaMaxHeapSize = "4g"
    }*/

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib", Versions.kotlin))

    implementation(Libs.Core.coreKtx)
    api(project(":kodi"))
}

group = "com.rasalexman.kodiandroidx"
version = appdependencies.Builds.KodiAndroidX.VERSION_NAME

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodiandroidx"
                artifactId = "kodiandroidx"
                version = appdependencies.Builds.KodiAndroidX.VERSION_NAME
            }
            create<MavenPublication>("debug") {
                from(components["debug"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodiandroidx"
                artifactId = "kodiandroidx-debug"
                version = appdependencies.Builds.KodiAndroidX.VERSION_NAME
            }
        }

        repositories {
            maven {
                name = "kodiandroidx"
                url = uri(layout.buildDirectory.dir("repo"))
            }
        }
    }
}

