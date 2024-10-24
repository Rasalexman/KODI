plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    id("maven-publish")
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

publishing {
    publications {
        create<MavenPublication>("kodi") {
            from(components["kotlin"])

            // You can then customize attributes of the publication as shown below.
            groupId = kodiKmpNamespace
            artifactId = "kodi"
            version = kodiVersion

            //artifact(tasks["sourcesJar"])
            //artifact(tasks["javadocJar"])
        }
    }

    repositories {
        maven {
            name = "kodi"
            url = uri("${layout.buildDirectory}/publishing-repository")
        }
    }
}

dependencies {
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
}