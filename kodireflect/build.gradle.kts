plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

val reflectVersion: String by rootProject.extra
group = "com.rasalexman.kodireflect"
version = reflectVersion

android {
    val buildSdkVersion: Int by rootProject.extra
    val minSdkVersion: Int by rootProject.extra

    compileSdk = buildSdkVersion
    defaultConfig {
        minSdk = minSdkVersion
        targetSdk = buildSdkVersion
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
}

dependencies {
    val kotlinVersion: String by rootProject.extra
    implementation(kotlin("reflect", kotlinVersion))
}

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
                url = uri("${buildDir}/publishing-repository")
            }
        }
    }
}
