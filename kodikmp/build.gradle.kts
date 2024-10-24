plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("maven-publish")
//    alias(libs.plugins.vanniktech.mavenPublish)
}

val kodiKmpNamespace: String by extra
val codePathKmp: String by rootProject.extra
val kodiVersion: String = libs.versions.kodiVersion.get()

val srcDirs = listOf(codePathKmp)
group = kodiKmpNamespace
version = kodiVersion

kotlin {
    jvm()
    androidTarget {
        publishLibraryVariants("release")
    }
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                //put your multiplatform dependencies here
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(libs.kotlin.test)
            }
        }
    }
}

android {
    namespace = kodiKmpNamespace
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components.getByName("kotlin"))

                // You can then customize attributes of the publication as shown below.
                groupId = "com.rasalexman.kodi"
                artifactId = "kodikmp"
                version = kodiVersion
            }
        }

        repositories {
            maven {
                name = "kodikmp"
                url = uri(layout.buildDirectory.dir("repo"))
            }
        }
    }
}

//mavenPublishing {
//    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
//
//    signAllPublications()
//
//    coordinates(group.toString(), "kodikmp", version.toString())
//
//    pom {
//        name = "Kotlin Dependency Injection Library"
//        description = "Multiplatform dependency injection."
//        inceptionYear = "2024"
//        url = "https://github.com/Rasalexman/KODI/"
//        licenses {
//            license {
//                name = "The Apache License, Version 2.0"
//                url = "https://www.apache.org/licenses/LICENSE-2.0.txt"
//                distribution = "https://www.apache.org/licenses/LICENSE-2.0.txt"
//            }
//        }
//        developers {
//            developer {
//                id = "rasalexman"
//                name = "Aleksandr Minkin"
//                url = "https://github.com/Rasalexman"
//            }
//        }
//        scm {
//            url = "https://github.com/Rasalexman/KODI/"
//            connection = "scm:git:git://github.com/Rasalexman/KODI.git"
//            developerConnection = "scm:git:ssh://git@github.com:Rasalexman/KODI.git"
//        }
//    }
//}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
}