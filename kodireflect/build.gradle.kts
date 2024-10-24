plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

val reflectVersion: String = libs.versions.kodiReflectVersion.get()
group = "com.rasalexman.kodireflect"
version = reflectVersion

android {
    val targetSdkVersion: Int = libs.versions.android.compileSdk.get().toInt()
    val minSdkVersion: Int = libs.versions.android.minSdk.get().toInt()

    compileSdk = targetSdkVersion
    defaultConfig {
        namespace = "com.rasalexman.kodireflect"
        minSdk = minSdkVersion
        version = reflectVersion
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
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
}

dependencies {
    implementation(libs.kotlin.reflect)
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

afterEvaluate {

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodireflect"
                artifactId = "kodireflect"
                version = reflectVersion
            }
            create<MavenPublication>("debug") {
                from(components["debug"])

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodireflect"
                artifactId = "kodireflect-debug"
                version = reflectVersion
            }
        }

        repositories {
            maven {
                name = "kodireflect"
                url = uri("${layout.buildDirectory}/publishing-repository")
            }
        }
    }
}
