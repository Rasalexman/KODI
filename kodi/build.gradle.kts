plugins {
    id("com.android.library")
    kotlin("android")
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka-android")
    id("maven-publish")
}

apply {
    from("keystore.gradle")
}

/*
android {
    compileSdkVersion COMPILE_VERSION

    defaultConfig {
        minSdkVersion MIN_VERSION
        targetSdkVersion TARGET_VERSION
        versionCode libCode
        versionName libVersion

        testInstrumentationRunner "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility = 1.8
        targetCompatibility = 1.8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = ["-XXLanguage:+InlineClasses", "-Xexperimental=kotlin.ExperimentalUnsignedTypes"]
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlin_version"

    testImplementation 'junit:junit:4.13-rc-1'
    androidTestImplementation 'androidx.test:runner:1.3.0-alpha02'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.3.0-alpha02'
}

dokka {
    outputFormat = 'html'
    outputDirectory = "$buildDir/javadoc"
}

repositories {
    mavenCentral()
}
apply from: 'deploy.gradle'

*/