import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import resources.Resources.codeDirs

plugins {
    id("java-library")
    id("kotlin")
    kotlin("kapt")
}

sourceSets {
    getByName("main") {
        java.setSrcDirs(codeDirs)
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.suppressWarnings = true
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.noReflect = true
    kotlinOptions.freeCompilerArgs += listOf(
            "-XXLanguage:+InlineClasses"
    )
}

dependencies {
    //implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(kotlin("stdlib", appdependencies.Versions.kotlin))
    implementation(project(":kodi"))

    implementation("com.squareup:kotlinpoet:1.8.0")
    implementation("com.google.auto.service:auto-service:1.0")
    kapt("com.google.auto.service:auto-service:1.0")
}

// comment this apply function if you fork this project
apply {
    from("deploy.gradle")
}