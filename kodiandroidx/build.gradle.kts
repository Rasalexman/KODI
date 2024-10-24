plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
}

val kodiVersion: String = libs.versions.kodiVersion.get()
group = "com.rasalexman.kodiandroidx"
version = kodiVersion

android {

    val targetSdkVersion: Int = libs.versions.android.compileSdk.get().toInt()
    val minSdkVersion: Int = libs.versions.android.minSdk.get().toInt()
    val codePath: String by rootProject.extra
    val srcDirs = listOf(codePath)

    compileSdk = targetSdkVersion
    defaultConfig {
        namespace = "com.rasalexman.kodiandroidx"
        minSdk = minSdkVersion
        version = kodiVersion
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            //isDebuggable = true
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }

    sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources.excludes.add("META-INF/notice.txt")
    }

    kotlinOptions {
        this.freeCompilerArgs += listOf(
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
        )
    }
}

dependencies {
    val coreKtx: String by rootProject.extra
    implementation(coreKtx)
    api(project(":kodikmp"))
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.getByName("release"))

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodiandroidx"
                artifactId = "kodiandroidx"
                version = kodiVersion
            }
            create<MavenPublication>("debug") {
                from(components.getByName("debug"))

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodiandroidx"
                artifactId = "kodiandroidx-debug"
                version = kodiVersion
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

