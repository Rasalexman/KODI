// Top-level build file where you can add configuration options common to all sub-projects/modules.

import appdependencies.ClassPath
import appdependencies.Versions

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://maven.fabric.io/public")
        }

    }
    dependencies {
        classpath(appdependencies.ClassPath.gradle)
        classpath(kotlin("gradle-plugin", version = appdependencies.Versions.kotlin))

        classpath(appdependencies.ClassPath.dokka)
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven { url = uri("https://www.jitpack.io") }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

/*
buildscript {
    ext.kotlin_version = '1.3.50'
    ext.dokka_version = '0.9.17'
    repositories {
        google()
        jcenter()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:3.5.2'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"

        // The Gradle Bintray Plugin allows you to publish artifacts to Bintray.
        classpath 'com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4'
        // Modification to the standard Maven plugin to be compatible with android-library projects (aar).
        classpath 'com.github.dcendents:android-maven-gradle-plugin:2.0'
        // Dokka is a documentation engine for Kotlin, performing the same function as javadoc for Java.
        // Just like Kotlin itself, Dokka fully supports mixed-language Java/Kotlin projects.
        // It understands standard Javadoc comments in Java files and KDoc comments in Kotlin files,
        // and can generate documentation in multiple formats including standard Javadoc, HTML
        // and Markdown.
        classpath "org.jetbrains.dokka:dokka-android-gradle-plugin:${dokka_version}"
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }

    ext {
        MIN_VERSION = 19
        COMPILE_VERSION = 29
        TARGET_VERSION = 29
        BUILD_TOOLS_VERSION = "29.0.2"

        libVersion = "1.2.9"
        libCode = 10209

        appCompatXVersion = '1.1.0'
        appCoreXVersion = '1.1.0'
        constraintlayout = '1.1.3'

        roomVersion = '2.2.1'
        kotprefVersion = '2.6.0'

        kdispatcherVersion = '1.0.1'
        retrofitVersion = '2.6.2'
        retrofitLogginVersion = '3.0.0'

        fastAdapterLib = '4.1.0'

        coilVersion = '0.8.0'
        coroutinesManagerVersion = "1.1.3"
        stickyVersion = "1.0.6"

        navigation = [
                fragment: '2.1.0',
                ui      : '2.1.0'
        ]
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        mavenCentral()
        maven { url "https://www.jitpack.io" }
        maven { url  "https://dl.bintray.com/sphc/Sticky" }
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}
*?