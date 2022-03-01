plugins {
    id("java-library")
    kotlin("jvm")
    id("maven-publish")
    id("com.google.devtools.ksp") version "1.6.10-1.0.4"
}

val codePath: String by rootProject.extra
val kodiVersion: String by rootProject.extra

val srcDirs = listOf(codePath)
group = "com.rasalexman.kodiksp"
version = kodiVersion

sourceSets {
    getByName("main") {
        java.setSrcDirs(srcDirs)
    }
}

tasks.register<Jar>(name = "sourceJar") {
    from(sourceSets["main"].java.srcDirs)
    archiveClassifier.set("sources")
}

java {
    this.sourceSets {
        getByName("main") {
            java.setSrcDirs(srcDirs)
        }
    }
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11

    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api(project(":kodi"))
    val kotlinpoet: String by rootProject.extra
    val kspapi: String by rootProject.extra

    implementation(kotlinpoet)
    implementation(kspapi)
}