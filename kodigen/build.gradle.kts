import resources.Resources.codeDirs
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jetbrains.dokka.gradle.DokkaTask

plugins {
    id("java-library")
    id("kotlin")
    id("com.jfrog.bintray")
    id("org.jetbrains.dokka")
    id("maven-publish")
    kotlin("kapt")
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(codeDirs)
    }
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib-jdk8", appdependencies.Versions.kotlin))
    implementation(project(":kodi"))

    implementation("com.squareup:kotlinpoet:1.6.0")
    implementation("com.google.auto.service:auto-service:1.0-rc7")
    kapt("com.google.auto.service:auto-service:1.0-rc7")
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.noReflect = true
    kotlinOptions.freeCompilerArgs += listOf(
            "-XXLanguage:+InlineClasses"
    )
}
/*
tasks {
    val dokka by getting(DokkaTask::class) {
        outputFormat = "html"
        outputDirectory = "$buildDir/dokka"
    }
}*/

repositories {
    mavenCentral()
}
// comment it if you fork this project
apply {
    from("deploy.gradle")
}