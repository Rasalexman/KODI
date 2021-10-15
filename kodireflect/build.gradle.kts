import appdependencies.Builds.COMPILE_VERSION
import appdependencies.Builds.MIN_VERSION
import appdependencies.Builds.TARGET_VERSION
import appdependencies.Versions

plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

android {
    compileSdk = (COMPILE_VERSION)
    defaultConfig {
        minSdk = (MIN_VERSION)
        targetSdk = (TARGET_VERSION)
        version = appdependencies.Builds.KodiReflect.VERSION_NAME
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packagingOptions {
        resources.excludes.add("META-INF/notice.txt")
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

    kotlinOptions {
        languageVersion = "1.5"
        apiVersion = "1.5"
        jvmTarget = "11"
    }
}

dependencies {
    implementation(kotlin("reflect", Versions.kotlin))
}

group = "com.rasalexman.kodireflect"
version = appdependencies.Builds.KodiReflect.VERSION_NAME

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

afterEvaluate {

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodireflect"
                artifactId = "kodireflect"
                version = appdependencies.Builds.KodiReflect.VERSION_NAME
            }
            create<MavenPublication>("debug") {
                from(components["debug"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodireflect"
                artifactId = "kodireflect-debug"
                version = appdependencies.Builds.KodiReflect.VERSION_NAME
            }
        }

        repositories {
            maven {
                name = "kodireflect"
                url = uri("${buildDir}/publishing-repository")
            }
        }
    }
}
